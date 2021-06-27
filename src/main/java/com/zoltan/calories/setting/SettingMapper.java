package com.zoltan.calories.setting;

import com.zoltan.calories.user.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SettingMapper {

    private UserMapper userMapper;

    public SettingDto toSettingDto(Setting setting) {
        return SettingDto.builder()
                .name(setting.getName())
                .value(setting.getValue())
                .build();
    }

    public SettingDto toSettingDtoWithUserDto(Setting setting) {
        return SettingDto.builder()
                .id(setting.getId())
                .name(setting.getName())
                .value(setting.getValue())
                .user(userMapper.toUserDto(setting.getUser()))
                .build();
    }
}
