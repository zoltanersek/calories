package com.zoltan.calories.setting;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettingDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String value;
}
