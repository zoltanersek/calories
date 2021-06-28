package com.zoltan.calories.entry;

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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(RepositoryTestConfiguration.class)
class EntryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired EntryRepository entryRepository;

    @Test
    @WithMockUser(username = "user1")
    public void test_getEntryByIdForCurrentUser_multipleUsers_onlyCurrentUserRowsReturned() {
        User user1 = new User("user1", "user1", Set.of(Role.ROLE_USER), true);
        User user2 = new User("user2", "user2", Set.of(Role.ROLE_USER), true);
        LocalTime localTime = LocalTime.of(12, 0);
        Entry entry1 = new Entry(LocalDate.now(), localTime, "entry1", 200, user1);
        Entry entry2 = new Entry(LocalDate.now(), localTime, "entry2", 200, user2);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entry1 = entityManager.persist(entry1);
        entry2 = entityManager.persist(entry2);
        entityManager.flush();

        Optional<Entry> foundEntryOptional = entryRepository.getEntryByIdForCurrentUser(entry1.getId());
        assertThat(foundEntryOptional.isPresent()).isTrue();
        assertThat(foundEntryOptional.get()).isEqualTo(entry1);

        foundEntryOptional = entryRepository.getEntryByIdForCurrentUser(entry2.getId());
        assertThat(foundEntryOptional.isPresent()).isFalse();
    }

    @Test
    @WithMockUser(username = "user1")
    public void test_deleteByIdForCurrentUser_multipleUsers_onlyCurrentUserRowsDeleted() {
        User user1 = new User("user1", "user1", Set.of(Role.ROLE_USER), true);
        User user2 = new User("user2", "user2", Set.of(Role.ROLE_USER), true);
        LocalTime localTime = LocalTime.of(12, 0);
        Entry entry1 = new Entry(LocalDate.now(), localTime, "entry1", 200, user1);
        Entry entry2 = new Entry(LocalDate.now(), localTime, "entry2", 200, user2);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entry1 = entityManager.persist(entry1);
        entry2 = entityManager.persist(entry2);
        entityManager.flush();

        entryRepository.deleteByIdForCurrentUser(entry1.getId());
        entityManager.clear();
        Entry foundEntry = entityManager.find(Entry.class, entry1.getId());
        assertThat(foundEntry).isNull();

        entryRepository.deleteByIdForCurrentUser(entry2.getId());
        entityManager.clear();
        foundEntry = entityManager.find(Entry.class, entry2.getId());
        assertThat(foundEntry).isEqualTo(entry2);
    }

    @Test
    @WithMockUser(username = "user1")
    public void test_getTotalForDayForCurrentUser_multipleUsers_onlyCurrentUserSumReturned() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user1 = new User("user1", "user1", Set.of(Role.ROLE_USER), true);
        User user2 = new User("user2", "user2", Set.of(Role.ROLE_USER), true);
        Entry entry1 = new Entry(localDate, localTime, "entry1", 200, user1);
        Entry entry2 = new Entry(localDate, localTime, "entry2", 200, user2);
        Entry entry3 = new Entry(localDate, localTime, "entry3", 150, user1);
        Entry entry4 = new Entry(localDate.minusDays(1), localTime, "entry4", 100, user1);
        entityManager.persist(user1);
        entityManager.persist(user2);
        entry1 = entityManager.persist(entry1);
        entry2 = entityManager.persist(entry2);
        entry3 = entityManager.persist(entry3);
        entry4 = entityManager.persist(entry4);
        entityManager.flush();

        Optional<Integer> totalForDayForCurrentUser = entryRepository.getTotalForDayForCurrentUser(localDate);
        assertThat(totalForDayForCurrentUser.isPresent()).isTrue();
        assertThat(totalForDayForCurrentUser.get()).isEqualTo(350);
    }
}