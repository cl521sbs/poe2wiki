package com.poe2wiki.controller;

import com.poe2wiki.common.ApiResult;
import com.poe2wiki.dto.LoginRequest;
import com.poe2wiki.dto.LoginResponse;
import com.poe2wiki.dto.RegisterRequest;
import com.poe2wiki.dto.UserProfile;
import com.poe2wiki.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ApiResult<LoginResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ApiResult.success(userService.register(req));
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        return ApiResult.success(userService.login(req));
    }

    @PostMapping("/refresh")
    public ApiResult<LoginResponse> refresh(@RequestBody Map<String, String> body) {
        return ApiResult.success(userService.refreshToken(body.get("refreshToken")));
    }

    @PostMapping("/logout")
    public ApiResult<Void> logout(Authentication auth, @RequestBody Map<String, String> body) {
        Long userId = (Long) auth.getPrincipal();
        userService.logout(userId, body.get("refreshToken"));
        return ApiResult.success();
    }

    @GetMapping("/profile")
    public ApiResult<UserProfile> profile(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResult.success(userService.getProfile(userId));
    }

    @PutMapping("/profile")
    public ApiResult<UserProfile> updateProfile(Authentication auth, @RequestBody UserProfile update) {
        Long userId = (Long) auth.getPrincipal();
        return ApiResult.success(userService.updateProfile(userId, update));
    }
}
