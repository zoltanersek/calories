package com.zoltan.calories.user;

import com.zoltan.calories.config.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AuthenticationControllerTest {

    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private JwtUtil jwtUtil;
    private UserMapper userMapper;
    @MockBean
    private UserService userService;

    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        authenticationController = new AuthenticationController(authenticationManager, jwtUtil, userMapper, userService);
    }

    @Test
    void test_login_credentialsOkay_okResponse() {
        User user = new User(1L, "username", "password", Set.of(Role.ROLE_USER), true);
        Authentication authentication = mock(Authentication.class);
        given(authentication.getPrincipal()).willReturn(user);
        given(authenticationManager.authenticate(any())).willReturn(authentication);
        given(jwtUtil.generateToken(eq(user))).willReturn("token.username");

        AuthRequest authRequest = new AuthRequest("username", "password");
        ResponseEntity<UserDto> responseEntity = authenticationController.login(authRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0)).isEqualTo("token.username");
        assertThat(responseEntity.getBody()).isEqualTo(userMapper.toUserDto(user));
    }

    @Test
    void test_login_credentialsInvalid_unauthorizedResponse() {
        given(authenticationManager.authenticate(any())).willThrow(new BadCredentialsException("invalid credentials"));

        AuthRequest authRequest = new AuthRequest("username", "password");
        ResponseEntity<UserDto> responseEntity = authenticationController.login(authRequest);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void test_register_requestOkay_serviceCalled() {
        CreateUserRequest createUserRequest = new CreateUserRequest("username", "password");
        authenticationController.register(createUserRequest);

        verify(userService).createUser(eq(createUserRequest));
    }
}