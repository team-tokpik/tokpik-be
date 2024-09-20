package org.example.tokpik_be.login.controller;

import lombok.RequiredArgsConstructor;
import org.example.tokpik_be.exception.GeneralException;
import org.example.tokpik_be.exception.LoginException;
import org.example.tokpik_be.login.dto.response.LoginResponse;
import org.example.tokpik_be.login.service.LoginCommandService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
