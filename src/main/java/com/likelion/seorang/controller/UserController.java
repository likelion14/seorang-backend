package com.likelion.seorang.controller;

import com.likelion.seorang.common.ApiSuccess;
import com.likelion.seorang.common.CustomUserDetails;
import com.likelion.seorang.config.JwtProvider;
import com.likelion.seorang.dto.LoginFormDto;
import com.likelion.seorang.dto.LoginResDto;
import com.likelion.seorang.dto.SignupFormDto;
import com.likelion.seorang.entity.User;
import com.likelion.seorang.service.UsersService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;


// 회원가입, 로그인 로직
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;
    private final JwtProvider jwtProvider;

    @Value("${jwt.refresh-exp-millis}")
    private long refreshExpMillis;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupFormDto signupFormDto) {

        usersService.signup(signupFormDto);

        return ResponseEntity
                .status(201)
                .body(new ApiSuccess(201, "성공적으로 처리되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(
            @Valid @RequestBody LoginFormDto loginFormDto,
            HttpServletResponse response
    ) {
        LoginResDto tokenResponse = usersService.login(loginFormDto);

        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", tokenResponse.getRefreshToken())
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(refreshExpMillis / 1000)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResDto> refresh(
            @CookieValue(name = "refresh_token", required = false) String refreshToken
    ) {
        LoginResDto tokenResponse = usersService.refresh(refreshToken);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenResponse.getAccessToken())
                .body(tokenResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @AuthenticationPrincipal CustomUserDetails me,
            HttpServletResponse response
    ) {
        usersService.logout(me.getId());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .path("/")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity
                .status(200)
                .body(new ApiSuccess(200, "성공적으로 처리되었습니다."));
    }
}