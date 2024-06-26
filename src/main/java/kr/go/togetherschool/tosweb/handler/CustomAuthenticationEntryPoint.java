package kr.go.togetherschool.tosweb.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.go.togetherschool.tosweb.common.ApiResponse;
import kr.go.togetherschool.tosweb.common.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authenticationException) throws IOException {

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(ErrorStatus._UNAUTHORIZED.getHttpStatus().value());
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.onFailure(ErrorStatus._UNAUTHORIZED.getCode(),
                ErrorStatus._UNAUTHORIZED.getMessage(), null)));
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        log.info("Access denied : {}, requestURI = {} ", ErrorStatus._UNAUTHORIZED.getMessage(), request.getRequestURI());
    }
}
