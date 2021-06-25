package com.zoltan.calories.user;

import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.Collections;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto createUser(CreateUserRequest createUserRequest) {
        if (userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
            throw new ValidationException("Username taken");
        }
        User user = User.builder()
                .enabled(true)
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .authorities(Collections.emptySet())
                .build();

        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }
}
