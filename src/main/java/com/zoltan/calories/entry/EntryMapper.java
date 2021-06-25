package com.zoltan.calories.entry;

import org.springframework.stereotype.Component;

@Component
public class EntryMapper {

    public EntryDto toEntryDto(Entry entry) {
        return EntryDto.builder()
                .id(entry.getId())
                .date(entry.getDate())
                .time(entry.getTime())
                .text(entry.getText())
                .calories(entry.getCalories())
                .build();
    }

}
