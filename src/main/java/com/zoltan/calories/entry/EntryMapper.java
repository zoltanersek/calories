package com.zoltan.calories.entry;

import org.springframework.stereotype.Component;

@Component
public class EntryMapper {

    public EntryDto toEntryDto(Entry entry) {
        return new EntryDto(entry.getId(), entry.getDate().toLocalDate(), entry.getDate().toLocalTime(), entry.getText(), entry.getCalories());
    }

}
