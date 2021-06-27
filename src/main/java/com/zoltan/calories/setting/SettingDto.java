package com.zoltan.calories.setting;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zoltan.calories.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SettingDto {
    @NotEmpty
    private String name;
    @NotEmpty
    private String value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long id;
}
