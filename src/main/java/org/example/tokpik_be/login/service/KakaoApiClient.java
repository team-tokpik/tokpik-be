package org.example.tokpik_be.login.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.tokpik_be.login.dto.response.KakaoUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    @Value("${KAKAO_API_CLIENT_ID}")
    private String clientId;
    @Value("${KAKAO_LOGIN_REDIRECT_URL}")
    private String redirectUrl;

    private final String contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE
        .concat(";charset=utf-8");

    public KakaoUserResponse requestKakaoUser(String code) {
        String accessToken = getAccessToken(code);

        return getKakaoUser(accessToken);
    }

    private String getAccessToken(String code) {
        WebClient webClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com/oauth/token")
            .build();

        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("grant_type", "authorization_code");
        loginRequest.add("client_id", clientId);
        loginRequest.add("redirect_uri", redirectUrl);
        loginRequest.add("code", code);

        Map<String, String> response = webClient.post()
            .header(HttpHeaders.CONTENT_TYPE, contentType)
            .body(BodyInserters.fromFormData(loginRequest))
            .retrieve()
            .bodyToMono(Map.class)
            .onErrorResume(WebClientResponseException.class, ex -> {
                Map<String, String> errorBody;

                try {
                    errorBody = new ObjectMapper().readValue(ex.getResponseBodyAsString(),
                        Map.class);
                    log.error("API Error : code : {}, error : {}, description : {}",
                        ex.getStatusCode(),
                        errorBody.get("error"),
                        errorBody.get("error_description")
                    );
                } catch (JsonProcessingException e) {
                    log.error("error parsing error response", e);
                }

                return Mono.error(ex);
            })
            .block();

        return Optional.ofNullable(response.get("access_token"))
            .orElseThrow(() -> new IllegalStateException("error occur when get access token"));
    }

    private KakaoUserResponse getKakaoUser(String accessToken) {
        WebClient webClient = WebClient.builder()
            .baseUrl("https://kapi.kakao.com/v2/user/me")
            .build();

        String bearerToken = "Bearer ".concat(accessToken);

        Map<String, Object> response = webClient.post()
            .header(HttpHeaders.AUTHORIZATION, bearerToken)
            .header(HttpHeaders.CONTENT_TYPE, contentType)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

        Map<String, Object> kakaoAccount = (Map<String, Object>) response.get("kakao_account");
        String email = Optional.ofNullable(kakaoAccount.get("email"))
            .map(String::valueOf)
            .orElseThrow(() -> new IllegalStateException("error occur when get email"));
        Map<String, String> profile = (Map<String, String>) kakaoAccount.get("profile");
        String profileImageUrl = Optional.ofNullable(profile.get("profile_image_url"))
            .map(String::valueOf)
            .orElseThrow(() -> new IllegalStateException("error occur when get profile_image_url"));

        return new KakaoUserResponse(email, profileImageUrl);
    }
}
