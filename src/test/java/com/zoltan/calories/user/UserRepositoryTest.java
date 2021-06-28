package com.zoltan.calories.user;

import com.zoltan.calories.RepositoryTestConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(RepositoryTestConfiguration.class)
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Test
    void test_findByUsername_userExists_userReturned() {
        User user = new User("username", "password", Set.of(Role.ROLE_USER), true);
        user = entityManager.persist(user);
        entityManager.flush();

        Optional<User> foundUserOptional = userRepository.findByUsername("username");

        assertThat(foundUserOptional.isPresent()).isTrue();
        assertThat(foundUserOptional.get()).isEqualTo(user);
    }

    @Test
    @WithMockUser(username = "user1")
    void getCurrentUser() {
        User user1 = new User("user1", "password", Set.of(Role.ROLE_USER), true);
        User user2 = new User("user2", "password", Set.of(Role.ROLE_USER), true);
        user1 = entityManager.persist(user1);
        user2 = entityManager.persist(user2);

        User currentUser = userRepository.getCurrentUser();

        assertThat(currentUser).isEqualTo(user1);
    }
}