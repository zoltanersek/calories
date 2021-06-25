package com.zoltan.calories.conf;

import com.zoltan.calories.entry.Entry;
import com.zoltan.calories.entry.EntryRepository;
import com.zoltan.calories.setting.Setting;
import com.zoltan.calories.setting.SettingRepository;
import com.zoltan.calories.user.Role;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;

@Configuration
@Log4j2
public class LoadDatabase {

    @Bean
    CommandLineRunner initDatabase(EntryRepository entryRepository,
                                   UserRepository userRepository,
                                   SettingRepository settingRepository,
                                   PasswordEncoder passwordEncoder) {
        return args -> {
            User user = new User("zoltan", passwordEncoder.encode("zoltan"), Collections.singleton(new Role("ROLE_ADMIN")), true);
            log.info("Preloading " + userRepository.save(user));

            Entry entry = new Entry(LocalDateTime.now(), "apple", 90, user);
            log.info("Preloading " + entryRepository.save(entry));

            Setting setting = new Setting("daily.goal", "2500", user);
            log.info("Preloading " + settingRepository.save(setting));
        };
    }
}
