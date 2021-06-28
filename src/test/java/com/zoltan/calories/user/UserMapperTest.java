package com.zoltan.calories.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void test_toUserDto_userDtoReturned() {
        User user = new User(1L, "username", "password", Set.of(Role.ROLE_USER), true);

        UserDto userDto = userMapper.toUserDto(user);

        assertThat(userDto.getUsername()).isEqualTo("username");
        assertThat(userDto.getId()).isEqualTo("1");
    }

    @Test
    void test_toUserDtoWithRoles_userDtoReturned() {
        User user = new User(1L, "username", "password", Set.of(Role.ROLE_USER), true);

        UserDto userDto = userMapper.toUserDtoWithRoles(user);

        assertThat(userDto.getUsername()).isEqualTo("username");
        assertThat(userDto.getId()).isEqualTo("1");
        assertThat(userDto.getEnabled()).isEqualTo(true);
        assertThat(userDto.getAuthorities()).isEqualTo(Set.of(Role.ROLE_USER));
    }
}