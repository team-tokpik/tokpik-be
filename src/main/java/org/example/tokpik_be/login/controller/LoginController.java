package org.example.tokpik_be.login.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.LoginException;
import org.example.tokpik_be.login.dto.request.AccessTokenRefreshRequest;
import org.example.tokpik_be.login.dto.response.AccessTokenRefreshResponse;
import org.example.tokpik_be.login.dto.response.LoginResponse;
import org.example.tokpik_be.login.service.LoginCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Login API", description = "로그인 관련 API")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginCommandService loginCommandService;

    @GetMapping("/oauth/kakao")
    public ResponseEntity<LoginResponse> kakaoLogin(
        @RequestParam("code") String code,
        @RequestParam(name = "error", required = false) String error) {

        if (StringUtils.hasText(error)) {
            throw new GeneralException(LoginException.LOGIN_FAIL);
        }

        LoginResponse response = loginCommandService.kakaoLogin(code);

        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "access token refresh", description = "refresh token으로 새 access token 발급")
    @ApiResponse(responseCode = "200", description = "access token 정상 발급")
    @PostMapping("/refresh")
    public ResponseEntity<AccessTokenRefreshResponse> refreshAccessToken(
        @RequestBody @Valid AccessTokenRefreshRequest request) {

        AccessTokenRefreshResponse response = loginCommandService.refreshAccessToken(request);

        return ResponseEntity.ok().body(response);
    }
}
