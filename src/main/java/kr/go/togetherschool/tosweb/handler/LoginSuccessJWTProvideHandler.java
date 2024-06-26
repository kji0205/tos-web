package kr.go.togetherschool.tosweb.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.togetherschool.tosweb.common.ApiResponse;
import kr.go.togetherschool.tosweb.dto.LoginResponseDto;
import kr.go.togetherschool.tosweb.entity.Member;
import kr.go.togetherschool.tosweb.model.JwtToken;
import kr.go.togetherschool.tosweb.repository.MemberRepository;
import kr.go.togetherschool.tosweb.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional
public class LoginSuccessJWTProvideHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;

    private final MemberRepository memberRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String REFRESH_TOKEN = "refreshToken";



    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String memberId = extractMemberId(authentication);
        JwtToken jwtToken = jwtService.createJwtToken(authentication);

        Member member = memberRepository.findByMemberId(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        member.updateRefreshToken(jwtToken.getRefreshToken());

        jwtService.sendAccessToken(response, jwtToken);
        log.info( "로그인에 성공합니다. memberId: {}" , memberId);
        log.info( "AccessToken 을 발급합니다. AccessToken: {}" ,jwtToken.getAccessToken());
        log.info( "RefreshToken 을 발급합니다. RefreshToken: {}" ,jwtToken.getRefreshToken());

        SecurityContext context = SecurityContextHolder.createEmptyContext();//5
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");

        LoginResponseDto.LoginDto loginDto = LoginResponseDto.LoginDto.builder()
                .memberId(memberId)
                .accessToken(jwtToken.getAccessToken())
                .role(member.getRole().getTitle())
                .build();

//		setCookieForLocal(response, jwtToken); // 개발단계에서 사용

        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onSuccess(loginDto)));



        if(request.getServerName().equals("localhost")){
            setCookieForLocal(response, jwtToken);
        }
        else{
            setCookieForProd(response, jwtToken);
        }

    }

    private void setCookieForLocal(HttpServletResponse response, JwtToken jwtToken) {
//		Cookie cookie = new Cookie(REFRESH_TOKEN, jwtToken.getRefreshToken());
//		cookie.setHttpOnly(true);  //httponly 옵션 설정
//		cookie.setSecure(true); //https 옵션 설정
//		cookie.setPath("/"); // 모든 곳에서 쿠키열람이 가능하도록 설정
//		cookie.setDomain(jwtService.getDomain());
//		cookie.setMaxAge(60 * 60 * 24 * 10); //쿠키 만료시간 24시간 * 10일
        String cookieValue = "refreshToken="+ jwtToken.getRefreshToken() +"; "+"Path=/; "+"Domain="+jwtService.getDomain()+"; "+"Max-Age=1800; HttpOnly; SameSite=None; Secure";
        response.addHeader("Set-Cookie",cookieValue);

//		response.addCookie(cookie);
    }

    private void setCookieForProd(HttpServletResponse response, JwtToken jwtToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN, jwtToken.getRefreshToken());
        cookie.setHttpOnly(true);  //httponly 옵션 설정
        cookie.setSecure(true); //https 옵션 설정
        cookie.setPath("/"); // 모든 곳에서 쿠키열람이 가능하도록 설정
        cookie.setMaxAge(60 * 60 * 24); //쿠키 만료시간 24시간

        response.addCookie(cookie);
    }


    private String extractMemberId(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }
    private String extractPassword(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getPassword();
    }
}
