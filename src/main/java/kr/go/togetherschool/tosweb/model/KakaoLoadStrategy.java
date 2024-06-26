package kr.go.togetherschool.tosweb.model;

import jakarta.transaction.Transactional;
import kr.go.togetherschool.tosweb.common.ErrorStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Slf4j
@Transactional
public class KakaoLoadStrategy extends SocialLoadStrategy {

    protected OAuth2UserInfo sendRequestToSocialSite(HttpEntity request) {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(SocialType.KAKAO.getUserInfoUrl(),// -> /v2/user/me
                    SocialType.KAKAO.getMethod(),
                    request,
                    RESPONSE_TYPE);

            return new KakaoOAuth2UserInfo(response.getBody());

        } catch (Exception e) {
            log.error(ErrorStatus.KAKAO_SOCIAL_LOGIN_FAIL.getMessage(), e.getMessage());
            throw e;
        }
    }
}