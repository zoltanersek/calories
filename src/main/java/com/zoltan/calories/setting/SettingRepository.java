package com.zoltan.calories.setting;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long> {
    @Query("select s from Setting s where s.user.username = ?#{ principal?.username }")
    Page<Setting> getSettingsForCurrentUser(Pageable pageable);

    @Query("select s from Setting s where s.name = :name and s.user.username = ?#{ principal?.username }")
    Optional<Setting> getSettingByNameForCurrentUser(String name);

    @Modifying
    @Transactional
    @Query("delete from Setting s where s.name = :name and s.user in (select u from User u where u.username = ?#{ principal?.username })")
    void deleteByNameForCurrentUser(String name);
}
