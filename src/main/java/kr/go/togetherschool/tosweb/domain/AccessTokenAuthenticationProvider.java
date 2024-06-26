package kr.go.togetherschool.tosweb.domain;


import jakarta.transaction.Transactional;
import kr.go.togetherschool.tosweb.entity.CustomUserDetails;
import kr.go.togetherschool.tosweb.entity.Member;
import kr.go.togetherschool.tosweb.repository.MemberRepository;
import kr.go.togetherschool.tosweb.service.LoadMemberService;
import kr.go.togetherschool.tosweb.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
@Transactional
@Slf4j
public class AccessTokenAuthenticationProvider implements AuthenticationProvider {

    private final LoadMemberService loadMemberService;
    private final MemberRepository memberRepository;
    private final MemberService memberService;


    @SneakyThrows
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        CustomUserDetails oAuth2User = loadMemberService.getOAuth2UserDetails((AccessTokenSocialTypeToken) authentication);

        Member member = saveOrGet(oAuth2User);
        oAuth2User.updateAuthorities(member);

        return AccessTokenSocialTypeToken.builder().principal(oAuth2User).authorities(oAuth2User.getAuthorities()).build();
    }


    private Member saveOrGet(CustomUserDetails oAuth2User) {
        Optional<Member> member = memberRepository.findBySocialTypeAndMemberId(oAuth2User.getSocialType(), oAuth2User.getMemberId());
        if (member.isEmpty()) {
            member = Optional.ofNullable(memberService.createDefaultOAuth2Member(oAuth2User));
        }
        log.info("회원 정보 : Idx = {}, memberId = {}, email = {}, socialType = {}",
                member.get().getIdx(), member.get().getMemberId(), member.get().getEmail(), member.get().getSocialType().getSocialName());
        return member.get();
    }


    @Override
    public boolean supports(Class<?> authentication) {
        //AccessTokenSocialTypeToken타입의  authentication 객체이면 해당 Provider가 처리한다.
        return AccessTokenSocialTypeToken.class.isAssignableFrom(authentication);
    }


}
