package com.poe2wiki.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.poe2wiki.common.RateLimiter;
import com.poe2wiki.dto.*;
import com.poe2wiki.entity.RefreshToken;
import com.poe2wiki.entity.User;
import com.poe2wiki.exception.BusinessException;
import com.poe2wiki.mapper.RefreshTokenMapper;
import com.poe2wiki.mapper.UserMapper;
import com.poe2wiki.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final RefreshTokenMapper refreshTokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RateLimiter rateLimiter;

    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (!rateLimiter.tryAcquire("rate:register:" + req.getUsername(), 3, 3600)) {
            throw new BusinessException(429, "注册请求过于频繁，请1小时后再试");
        }

        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername())) > 0) {
            throw new BusinessException("用户名已被注册");
        }

        if (userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, req.getEmail())) > 0) {
            throw new BusinessException("邮箱已被注册");
        }

        User user = new User();
        user.setUsername(req.getUsername());
        user.setEmail(req.getEmail());
        user.setPasswordHash(passwordEncoder.encode(req.getPassword()));
        user.setNickname(req.getUsername());
        user.setRole("user");
        user.setStatus("active");
        userMapper.insert(user);

        return buildLoginResponse(user);
    }

    public LoginResponse login(LoginRequest req) {
        if (!rateLimiter.tryAcquire("rate:login:" + req.getUsername(), 5, 900)) {
            throw new BusinessException(429, "登录尝试过于频繁，请15分钟后再试");
        }

        User user = userMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, req.getUsername()));

        if (user == null || !passwordEncoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if ("banned".equals(user.getStatus())) {
            throw new BusinessException(403, "账号已被封禁");
        }

        return buildLoginResponse(user);
    }

    public LoginResponse refreshToken(String refreshTokenStr) {
        RefreshToken rt = refreshTokenMapper.selectOne(
            new LambdaQueryWrapper<RefreshToken>()
                .eq(RefreshToken::getToken, refreshTokenStr));

        if (rt == null || rt.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(401, "Refresh token 无效或已过期");
        }

        User user = userMapper.selectById(rt.getUserId());
        if (user == null || "banned".equals(user.getStatus())) {
            throw new BusinessException(401, "用户不存在或已被封禁");
        }

        refreshTokenMapper.deleteById(rt.getId());
        return buildLoginResponse(user);
    }

    public void logout(Long userId, String refreshTokenStr) {
        refreshTokenMapper.delete(
            new LambdaQueryWrapper<RefreshToken>()
                .eq(RefreshToken::getUserId, userId)
                .eq(RefreshToken::getToken, refreshTokenStr));
    }

    public UserProfile getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return toProfile(user);
    }

    public UserProfile updateProfile(Long userId, UserProfile update) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (update.getNickname() != null) user.setNickname(update.getNickname());
        if (update.getBio() != null) user.setBio(update.getBio());
        if (update.getAvatarUrl() != null) user.setAvatarUrl(update.getAvatarUrl());
        userMapper.updateById(user);
        return toProfile(user);
    }

    private LoginResponse buildLoginResponse(User user) {
        String accessToken = jwtUtils.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshTokenStr = jwtUtils.generateRefreshToken(user.getId());

        RefreshToken rt = new RefreshToken();
        rt.setUserId(user.getId());
        rt.setToken(refreshTokenStr);
        rt.setExpiresAt(LocalDateTime.now().plusDays(7));
        refreshTokenMapper.insert(rt);

        return new LoginResponse(accessToken, refreshTokenStr, 7200, toProfile(user));
    }

    private UserProfile toProfile(User user) {
        UserProfile profile = new UserProfile();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setEmail(user.getEmail());
        profile.setNickname(user.getNickname());
        profile.setAvatarUrl(user.getAvatarUrl());
        profile.setBio(user.getBio());
        profile.setRole(user.getRole());
        return profile;
    }
}
