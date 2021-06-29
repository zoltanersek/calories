package com.zoltan.calories.admin;

import com.zoltan.calories.user.Role;
import com.zoltan.calories.user.UserDto;
import com.zoltan.calories.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class UserManagementControllerTest {

    @MockBean
    private UserService userService;
    private UserManagementController userManagementController;

    @BeforeEach
    void setUp() {
        userManagementController = new UserManagementController(userService);
    }

    @Test
    void getAllUsers_usersPresent_userServiceCalled() {
        UserDto userDto = new UserDto("1", "username", Set.of(Role.ROLE_USER), true);
        Page<UserDto> page = new PageImpl<>(List.of(userDto));
        given(userService.getAllUsers(anyString(), any())).willReturn(page);

        List<UserDto> search = userManagementController.getAllUsers("search", Pageable.unpaged()).getContent();

        assertThat(search.size()).isEqualTo(1);
        assertThat(search.get(0)).isEqualTo(userDto);
    }

    @Test
    void updateUser_validRequest_userServiceCalled() {
        UserDto userDto = new UserDto("1", "username", Set.of(Role.ROLE_USER), true);

        userManagementController.updateUser(1L, userDto);

        verify(userService).updateUser(eq(1L), eq(userDto));
    }

    @Test
    void deleteUser_validId_userServiceCalled() {
        userManagementController.deleteUser(1L);

        verify(userService).deleteUser(1L);
    }
}