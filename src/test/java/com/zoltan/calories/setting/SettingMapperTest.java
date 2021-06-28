package com.zoltan.calories.setting;

import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class SettingMapperTest {

    private SettingMapper settingMapper;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        settingMapper = new SettingMapper(userMapper);
    }

    @Test
    public void test_toSettingDto_settingDtoReturned() {
        User user = new User(1L, "test", "test", Set.of(), true);
        Setting setting = new Setting("name", "value", user);

        SettingDto settingDto = settingMapper.toSettingDto(setting);

        assertThat(settingDto.getName()).isEqualTo("name");
        assertThat(settingDto.getValue()).isEqualTo("value");
        assertThat(settingDto.getId()).isNull();
        assertThat(settingDto.getUser()).isNull();
    }

    @Test
    public void test_toSettingDtoWithUserDto_settingDtoReturned() {
        User user = new User(1L, "test", "test", Set.of(), true);
        Setting setting = new Setting("name", "value", user);
        setting.setId(1L);

        SettingDto settingDto = settingMapper.toSettingDtoWithUserDto(setting);

        assertThat(settingDto.getName()).isEqualTo("name");
        assertThat(settingDto.getValue()).isEqualTo("value");
        assertThat(settingDto.getId()).isEqualTo(1L);
        assertThat(settingDto.getUser()).isEqualTo(userMapper.toUserDto(user));
    }

}