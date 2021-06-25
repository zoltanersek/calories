package com.zoltan.calories.entry;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEntryRequest {
    @NotNull
    @PositiveOrZero
    Long id;
    @NotNull
    private LocalDate date;
    @NotNull
    @JsonFormat(pattern = "HH:mm")
    private LocalTime time;
    @NotEmpty
    private String text;
    @PositiveOrZero
    private Integer calories;
}
