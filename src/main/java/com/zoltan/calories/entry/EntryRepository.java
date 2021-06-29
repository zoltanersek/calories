package com.zoltan.calories.entry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Optional;

public interface EntryRepository extends JpaRepository<Entry, Long>, JpaSpecificationExecutor<Entry> {
    @Query("select e from Entry e where e.id = :id and e.user.username = ?#{ principal?.username }")
    Optional<Entry> getEntryByIdForCurrentUser(Long id);

    @Modifying
    @Transactional
    @Query("delete from Entry e where e.id = :id and e.user in (select u from User u where u.username = ?#{ principal?.username })")
    void deleteByIdForCurrentUser(Long id);

    @Query("select sum(e.calories) as totalCalories from Entry e where e.date = :date and e.user.username = ?#{ principal?.username }")
    Optional<Integer> getTotalForDayForCurrentUser(LocalDate date);
}
