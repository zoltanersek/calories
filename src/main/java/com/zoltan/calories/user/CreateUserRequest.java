package com.zoltan.calories.user;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
}
