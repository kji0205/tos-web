package kr.go.togetherschool.tosweb.filter;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.togetherschool.tosweb.common.ApiResponse;
import kr.go.togetherschool.tosweb.common.ErrorStatus;
import kr.go.togetherschool.tosweb.entity.CustomUserDetails;
import kr.go.togetherschool.tosweb.entity.Member;
import kr.go.togetherschool.tosweb.repository.MemberRepository;
import kr.go.togetherschool.tosweb.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();//5

    private static final String NO_CHECK_URL = "/api/member/login";//1
    private static final String NO_CHECK_URL_2 = "/api/member/login/oauth2";
    private static final String NO_CHECK_URL_3 = "/api/member/login-extension";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(request.getRequestURI().equals(NO_CHECK_URL) || request.getRequestURI().contains(NO_CHECK_URL_2) ||
                request.getRequestURI().equals(NO_CHECK_URL_3)) {
            filterChain.doFilter(request, response);
            return;//안해주면 아래로 내려가서 계속 필터를 진행해버림
        }

        checkAccessTokenAndAuthentication(request, response, filterChain);

    }

    private void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            jwtService.extractAccessToken(request).filter(jwtService::isTokenValid).flatMap(jwtService::extractMemberId)
                    .flatMap(memberRepository::findByMemberId).ifPresent(this::saveAuthentication);
            filterChain.doFilter(request, response);
        } catch (NullPointerException e) {
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setStatus(ErrorStatus.MEMBER_AUTHORIZATION_NOT_VALID.getHttpStatus().value());
            response.setContentType("application/json");
            response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(ErrorStatus.MEMBER_AUTHORIZATION_NOT_VALID.getCode(),
                    ErrorStatus.MEMBER_AUTHORIZATION_NOT_VALID.getMessage(), e.getMessage())));
            log.info("Authentication failed: " + e.getClass().toString() + " : " + e.getMessage());
        }
    }


    private void saveAuthentication(Member member) {
        CustomUserDetails userDetails = CustomUserDetails.create(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,authoritiesMapper.mapAuthorities(userDetails.getAuthorities()));


        SecurityContext context = SecurityContextHolder.createEmptyContext();//5
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        log.info("Authentication success: memberId = {}", member.getMemberId());
    }
}
