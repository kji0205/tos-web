package kr.go.togetherschool.tosweb.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.togetherschool.tosweb.common.ApiResponse;
import kr.go.togetherschool.tosweb.common.ErrorStatus;
import kr.go.togetherschool.tosweb.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    private final JwtService jwtService;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        Optional<String> accessToken = jwtService.extractAccessToken(request);
        if (accessToken.isPresent()) {
            Optional<String> memberId = jwtService.extractMemberId(accessToken.get());
            log.info("Access denied: memberId = " + memberId.get());
        }
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(ErrorStatus.MEMBER_NOT_AUTHENTICATED.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(ErrorStatus.MEMBER_NOT_AUTHENTICATED.getCode(),
                ErrorStatus.MEMBER_NOT_AUTHENTICATED.getMessage(), null)));
        response.setStatus(HttpStatus.FORBIDDEN.value());
    }
}