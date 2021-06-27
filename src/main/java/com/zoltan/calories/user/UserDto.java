package com.zoltan.calories.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
public class UserDto {
    @NotEmpty
    private String id;
    @NotEmpty
    private String username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotEmpty
    private Set<Role> authorities;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull
    private Boolean enabled;
}
