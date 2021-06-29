package com.zoltan.calories.admin;

import com.zoltan.calories.user.UserDto;
import com.zoltan.calories.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "api/user/")
@RolesAllowed({"ROLE_ADMIN", "ROLE_USER_MANAGER"})
@AllArgsConstructor
public class UserManagementController {
    private final UserService userService;

    @GetMapping
    public Page<UserDto> getAllUsers(@RequestParam(value = "search", required = false) String search, Pageable p) {
        return userService.getAllUsers(search, p);
    }

    @PutMapping(path = "/{id}")
    public UserDto updateUser(@PathVariable("id") @PositiveOrZero Long id, @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteUser(@PathVariable("id") @PositiveOrZero Long id) {
        userService.deleteUser(id);
    }
}
