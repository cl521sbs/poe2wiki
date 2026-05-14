package com.poe2wiki.dto;

import lombok.Data;

@Data
public class UserProfile {
    private Long id;
    private String username;
    private String nickname;
    private String email;
    private String avatarUrl;
    private String bio;
    private String role;
}
