package kr.go.togetherschool.tosweb.util;

import kr.go.togetherschool.tosweb.entity.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class SecurityUtil {
    public static Optional<String> getLoginMemberId() {
        CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return Optional.ofNullable(user.getMemberId());
    }

}