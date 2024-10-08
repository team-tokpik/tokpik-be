package org.example.tokpik_be.login.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.LoginException;
import org.example.tokpik_be.login.dto.response.KakaoUserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class KakaoApiClient {

    private final ObjectMapper objectMapper;
    @Value("${KAKAO_API_CLIENT_ID}")
    private String clientId;
    @Value("${KAKAO_LOGIN_REDIRECT_URL}")
    private String redirectUrl;

    public KakaoUserResponse requestKakaoUser(String code) {
        WebClient webClient = WebClient.builder()
            .baseUrl("https://kauth.kakao.com/oauth/token")
            .build();

        MultiValueMap<String, String> loginRequest = new LinkedMultiValueMap<>();
        loginRequest.add("grant_type", "authorization_code");
        loginRequest.add("client_id", clientId);
        loginRequest.add("redirect_uri", redirectUrl);
        loginRequest.add("code", code);

        try {
            Map<String, String> response = webClient.post()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(loginRequest))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                    apiResponse -> Mono.error(new GeneralException(LoginException.LOGIN_FAIL)))
                .bodyToMono(Map.class)
                .block();

            return toKakaoLoginResponse(response);
        } catch (GeneralException e) {
            throw e;
        }
    }

    private KakaoUserResponse toKakaoLoginResponse(Map<String, String> response) {

        String idToken = response.get("id_token");
        String payload = idToken.split("\\.")[1];

        byte[] decodedPayload = Base64.getUrlDecoder().decode(payload);

        try {
            Map<String, String> loginResponse = objectMapper.readValue(decodedPayload, Map.class);
            String email = loginResponse.get("email");
            String profilePhotoUrl = loginResponse.get("picture");

            return new KakaoUserResponse(email, profilePhotoUrl);

        } catch (IOException e) {
            throw new IllegalStateException("카카오 id_token 파싱 중 예외 발생");
        }
    }
}
