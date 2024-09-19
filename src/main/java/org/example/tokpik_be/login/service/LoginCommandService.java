package org.example.tokpik_be.login.service;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.login.dto.response.KakaoUserResponse;
import org.example.tokpik_be.login.dto.response.LoginResponse;
import org.example.tokpik_be.user.domain.User;
import org.example.tokpik_be.user.service.UserCommandService;
import org.example.tokpik_be.user.service.UserQueryService;
import org.example.tokpik_be.util.JwtUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoginCommandService {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;
    private final KakaoApiClient kakaoApiClient;
    private final JwtUtil jwtUtil;

    public LoginResponse kakaoLogin(String code) {

        KakaoUserResponse kakaoUserResponse = kakaoApiClient.requestKakaoUser(code);
        String email = kakaoUserResponse.email();

        if (userQueryService.notExistByEmail(email)) {
            User user = new User(email, kakaoUserResponse.profilePhotoUrl());
            userCommandService.save(user);
        }

        User user = userQueryService.findByEmail(email);
        long userId = user.getId();
        String accessToken = jwtUtil.generateAccessToken(userId);
        String refreshToken = jwtUtil.generateRefreshToken(userId);
        user.updateRefreshToken(refreshToken);

        return new LoginResponse(user.requiresProfile(), accessToken, refreshToken);
    }
}
