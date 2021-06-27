package com.zoltan.calories.user;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.search.SearchParser;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.zoltan.calories.user.Role.ROLE_USER;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final SearchParser searchParser;

    @Transactional
    public UserDto createUser(CreateUserRequest createUserRequest) {
        if (userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
            throw new ValidationException("Username taken");
        }
        User user = User.builder()
                .enabled(true)
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .authorities(Set.of(ROLE_USER))
                .build();

        userRepository.save(user);
        return userMapper.toUserDto(user);
    }

    public User getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public Page<UserDto> getAllUsers(String search, Pageable pageable) {
        Specification<User> specification = null;
        if (!ObjectUtils.isEmpty(search)) {
            specification = searchParser.parse(search, UserSpecification::new);
        }
        return userRepository.findAll(specification, pageable).map(userMapper::toUserDtoWithRoles);
    }

    @Transactional
    public UserDto updateUser(Long id, UserDto userDto) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (existingUserOptional.isEmpty()) {
            throw new NotFoundException("User " + id + " not found");
        }
        User existingUser = existingUserOptional.get();
        if (!existingUser.getId().toString().equals(userDto.getId())) {
            throw new ValidationException("Id change for user not supported");
        }
        if (!existingUser.getUsername().equals(userDto.getUsername())) {
            throw new ValidationException("Username change for user not supported");
        }
        Set<Role> newAuthorities = new HashSet<>();
        for (Role role : userDto.getAuthorities()) {
            if (!List.of(Role.values()).contains(role)) {
                throw new ValidationException("Role " + role + "not defined");
            }
            newAuthorities.add(role);
        }
        existingUser.setAuthorities(newAuthorities);
        existingUser.setEnabled(userDto.getEnabled());
        return userMapper.toUserDtoWithRoles(existingUser);
    }

    public void deleteUser(Long id) {
        userRepository.findById(id).ifPresent(userRepository::delete);
    }
}
