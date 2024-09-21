package org.example.tokpik_be.login.service;

import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.LoginException;
import org.example.tokpik_be.login.dto.request.AccessTokenRefreshRequest;
import org.example.tokpik_be.login.dto.response.AccessTokenRefreshResponse;
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

        Date generateAt = new Date();
        String accessToken = jwtUtil.generateAccessToken(userId, generateAt);
        String refreshToken = jwtUtil.generateRefreshToken(userId, generateAt);
        user.updateRefreshToken(refreshToken);

        return new LoginResponse(user.requiresProfile(), accessToken, refreshToken);
    }

    public AccessTokenRefreshResponse refreshAccessToken(AccessTokenRefreshRequest request) {

        String refreshToken = request.refreshToken();

        long userId = jwtUtil.parseUserIdFromToken(refreshToken);
        User user = userQueryService.findById(userId);
        if (user.notEqualRefreshToken(refreshToken)) {
            throw new GeneralException(LoginException.INVALID_JWT);
        }

        Date generateAt = new Date();
        String accessToken = jwtUtil.generateAccessToken(userId, generateAt);

        return new AccessTokenRefreshResponse(accessToken);
    }
}
