package com.zoltan.calories.entry;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;


public interface EntryRepository extends JpaRepository<Entry, Long> {
    @Query("select e from Entry e where e.user.username = ?#{ principal?.username }")
    Page<Entry> getEntriesForCurrentUser(Pageable pageable);

    @Query("select e from Entry e where e.id = :id and e.user.username = ?#{ principal?.username }")
    Optional<Entry> getEntryByIdForCurrentUser(Long id);

    @Modifying
    @Transactional
    @Query("delete from Entry e where e.id = :id and e.user in (select u from User u where u.username = ?#{ principal?.username })")
    void deleteByIdForCurrentUser(Long id);
}
