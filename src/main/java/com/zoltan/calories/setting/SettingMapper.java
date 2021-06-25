package com.zoltan.calories.setting;

import org.springframework.stereotype.Component;

@Component
public class SettingMapper {

    public SettingDto toSettingDto(Setting setting) {
        return new SettingDto(setting.getName(), setting.getValue());
    }
}
