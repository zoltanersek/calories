package com.zoltan.calories.entry;

import com.zoltan.calories.user.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EntryMapper {

    private final UserMapper userMapper;

    public EntryDto toEntryDto(Entry entry) {
        return EntryDto.builder()
                .id(entry.getId())
                .date(entry.getDate())
                .time(entry.getTime())
                .text(entry.getText())
                .calories(entry.getCalories())
                .build();
    }

    public EntryDto toEntryDtoWithUserDto(Entry entry) {
        return EntryDto.builder()
                .id(entry.getId())
                .date(entry.getDate())
                .time(entry.getTime())
                .text(entry.getText())
                .calories(entry.getCalories())
                .user(userMapper.toUserDto(entry.getUser()))
                .build();
    }

}
