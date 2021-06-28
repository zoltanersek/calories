package com.zoltan.calories.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.zoltan.calories.user.UserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@With
public class EntryDto {
    Long id;
    private LocalDate date;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    private String text;
    private Integer calories;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean underBudget;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserDto user;
}

