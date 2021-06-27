package com.zoltan.calories.user;

import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .build();
    }

    public UserDto toUserDtoWithRoles(User user) {
        return UserDto.builder()
                .id(user.getId().toString())
                .username(user.getUsername())
                .authorities(user.getAuthorities())
                .enabled(user.isEnabled())
                .build();
    }

}
