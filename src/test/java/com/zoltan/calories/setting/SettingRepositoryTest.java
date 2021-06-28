package com.zoltan.calories.setting;

import com.zoltan.calories.RepositoryTestConfiguration;
import com.zoltan.calories.user.Role;
import com.zoltan.calories.user.User;
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
class SettingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    SettingRepository settingRepository;

    @Test
    @WithMockUser(username = "user1")
    void test_getSettingByNameForCurrentUser_multipleUsers_correctRowReturned() {
        User user1 = new User("user1", "user1", Set.of(Role.ROLE_USER), true);
        User user2 = new User("user2", "user2", Set.of(Role.ROLE_USER), true);
        Setting setting1 = new Setting("name", "correct_value", user1);
        Setting setting2 = new Setting("name", "incorrect_value", user2);
        entityManager.persist(user1);
        entityManager.persist(user2);
        setting1 = entityManager.persist(setting1);
        setting2 = entityManager.persist(setting2);
        entityManager.flush();

        Optional<Setting> foundSettingOptional = settingRepository.getSettingByNameForCurrentUser("name");
        assertThat(foundSettingOptional.isPresent()).isTrue();
        assertThat(foundSettingOptional.get()).isEqualTo(setting1);
    }

    @Test
    @WithMockUser(username = "user1")
    void test_deleteByNameForCurrentUser_multipleUsers_onlyCurrentUserRowsDeleted() {
        User user1 = new User("user1", "user1", Set.of(Role.ROLE_USER), true);
        User user2 = new User("user2", "user2", Set.of(Role.ROLE_USER), true);
        Setting setting1 = new Setting("name", "setting1", user1);
        Setting setting2 = new Setting("name", "setting2", user2);
        entityManager.persist(user1);
        entityManager.persist(user2);
        setting1 = entityManager.persist(setting1);
        setting2 = entityManager.persist(setting2);
        entityManager.flush();

        settingRepository.deleteByNameForCurrentUser("name");
        entityManager.clear();
        Setting foundSetting = entityManager.find(Setting.class, setting1.getId());
        assertThat(foundSetting).isNull();

        foundSetting = entityManager.find(Setting.class, setting2.getId());
        assertThat(foundSetting).isEqualTo(setting2);
    }
}