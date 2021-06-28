package com.zoltan.calories.user;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.search.BasicOperation;
import com.zoltan.calories.search.SearchParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class UserServiceTest {

    @MockBean
    private UserRepository userRepository;
    private UserMapper userMapper;
    @MockBean
    private PasswordEncoder passwordEncoder;
    @MockBean
    private SearchParser searchParser;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        userService = new UserService(userRepository, userMapper, passwordEncoder, searchParser);
    }

    @Test
    void test_createUser_usernameTaken_exceptionThrown() {
        User user = new User("username", "password", Set.of(), true);
        given(userRepository.findByUsername(eq("username"))).willReturn(Optional.of(user));

        CreateUserRequest createUserRequest = new CreateUserRequest("username", "password");
        try {
            userService.createUser(createUserRequest);
            fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Username taken");
        }
    }

    @Test
    void test_createUser_validRequest_userCreated() {
        given(passwordEncoder.encode(eq("password"))).willReturn("password_encoded");
        given(userRepository.findByUsername(eq("username"))).willReturn(Optional.empty());
        doAnswer(invocation -> {
            User u = (User)invocation.getArguments()[0];
            u.setId(1L);
            return u;
        }).when(userRepository).save(any());

        CreateUserRequest createUserRequest = new CreateUserRequest("username", "password");
        UserDto userDto = userService.createUser(createUserRequest);

        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertThat(savedUser.getUsername()).isEqualTo("username");
        assertThat(savedUser.getPassword()).isEqualTo("password_encoded");
        assertThat(savedUser.getAuthorities()).isEqualTo(Set.of(Role.ROLE_USER));
        assertThat(savedUser.getId()).isEqualTo(1L);
        assertThat(userDto).isEqualTo(userMapper.toUserDto(savedUser));
    }

    @Test
    void test_getCurrentUser_repositoryCalled() {
        userService.getCurrentUser();

        verify(userRepository).getCurrentUser();
    }

    @Test
    void test_getAllUsers_usersFound_usersReturned() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        given(searchParser.parse(eq("search"), ArgumentMatchers.<Function<BasicOperation, Specification<User>>>any()))
                .willReturn(new UserSpecification(new BasicOperation()));
        given(userRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(new PageImpl<>(List.of(user)));

        Page<UserDto> search = userService.getAllUsers("search", Pageable.unpaged());

        assertThat(search.getContent().size()).isEqualTo(1);
        UserDto foundUser = search.getContent().get(0);
        assertThat(foundUser).isEqualTo(userMapper.toUserDtoWithRoles(user));
    }

    @Test
    void test_updateUser_userDoesntExist_exceptionThrown() {
        given(userRepository.findById(any())).willReturn(Optional.empty());
        UserDto userDto = new UserDto("1","username", Set.of(), true);

        try {
            userService.updateUser(1L, userDto);
            Assertions.fail("Expected NotFoundException");
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("User 1 not found");
        }
    }

    @Test
    void test_updateUser_differentId_exceptionThrown() {
        User user = new User(1L, "username", "password", Set.of(), true);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        UserDto userDto = new UserDto("2","username", Set.of(), true);

        try {
            userService.updateUser(1L, userDto);
            Assertions.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Id change for user not supported");
        }
    }

    @Test
    void test_updateUser_usernameDifferent_exceptionThrown() {
        User user = new User(1L, "username", "password", Set.of(), true);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        UserDto userDto = new UserDto("1","username_changed", Set.of(), true);

        try {
            userService.updateUser(1L, userDto);
            Assertions.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Username change for user not supported");
        }
    }

    @Test
    void test_updateUser_updateValid_userUpdated() {
        User user = new User(1L, "username", "password", Set.of(Role.ROLE_USER, Role.ROLE_USER_MANAGER), false);
        given(userRepository.findById(any())).willReturn(Optional.of(user));
        UserDto userDto = new UserDto("1","username", Set.of(Role.ROLE_ADMIN), true);

        userDto = userService.updateUser(1L, userDto);

        assertThat(userDto.getEnabled()).isEqualTo(true);
        assertThat(userDto.getAuthorities()).isEqualTo(Set.of(Role.ROLE_ADMIN));
    }

    @Test
    void deleteUser() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        given(userRepository.findById(eq(1L))).willReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository).delete(eq(user));
    }
}