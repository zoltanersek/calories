package com.zoltan.calories.config;

import com.zoltan.calories.entry.Entry;
import com.zoltan.calories.entry.EntryRepository;
import com.zoltan.calories.setting.Setting;
import com.zoltan.calories.setting.SettingRepository;
import com.zoltan.calories.setting.Settings;
import com.zoltan.calories.user.Role;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

@Configuration
@Log4j2
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(EntryRepository entryRepository,
                                   UserRepository userRepository,
                                   SettingRepository settingRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            User user = new User("zoltan", passwordEncoder.encode("zoltan"),
                    Set.of(Role.ROLE_ADMIN, Role.ROLE_USER), true);
            log.info("Preloading " + userRepository.save(user).getUsername());

            Entry entry = new Entry(LocalDate.now(), LocalTime.now(), "apple", 90, user);
            log.info("Preloading " + entryRepository.save(entry).getId() + " for user zoltan");

            Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2500", user);
            log.info("Preloading " + settingRepository.save(setting).getName() + " for user zoltan");
        };
    }
}
