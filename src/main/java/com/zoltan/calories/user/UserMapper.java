package com.zoltan.calories.user;

import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserDto toUserDto(User user) {
        return new UserDto(user.getId().toString(), user.getUsername());
    }

}
