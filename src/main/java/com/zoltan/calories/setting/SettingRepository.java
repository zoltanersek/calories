package com.zoltan.calories.setting;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;
import java.util.Optional;

public interface SettingRepository extends JpaRepository<Setting, Long>, JpaSpecificationExecutor<Setting> {
    @Query("select s from Setting s where s.name = :name and s.user.username = ?#{ principal?.username }")
    Optional<Setting> getSettingByNameForCurrentUser(String name);

    @Modifying
    @Transactional
    @Query("delete from Setting s where s.name = :name and s.user in (select u from User u where u.username = ?#{ principal?.username })")
    void deleteByNameForCurrentUser(String name);
}
