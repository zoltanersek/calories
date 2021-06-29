package com.zoltan.calories.entry;

import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class EntryMapperTest {

    private EntryMapper entryMapper;
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        entryMapper = new EntryMapper(userMapper);
    }

    @Test
    void toEntryDto_entryValid_entryDtoReturned() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        User user = new User(1L, "test", "test", Set.of(), true);
        Entry entry = new Entry(localDate, localTime, "entry", 200, user);
        entry.setId(1L);

        EntryDto entryDto = entryMapper.toEntryDto(entry);

        assertThat(entryDto).isNotNull();
        assertThat(entryDto.getId()).isEqualTo(1L);
        assertThat(entryDto.getText()).isEqualTo("entry");
        assertThat(entryDto.getCalories()).isEqualTo(200);
        assertThat(entryDto.getDate()).isEqualTo(localDate);
        assertThat(entryDto.getTime()).isEqualTo(localTime);
        assertThat(entryDto.getUser()).isNull();
    }

    @Test
    void toEntryDtoWithUserDto() {
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        User user = new User(1L, "test", "test", Set.of(), true);
        Entry entry = new Entry(localDate, localTime, "entry", 200, user);
        entry.setId(1L);

        EntryDto entryDto = entryMapper.toEntryDtoWithUserDto(entry);

        assertThat(entryDto).isNotNull();
        assertThat(entryDto.getId()).isEqualTo(1L);
        assertThat(entryDto.getText()).isEqualTo("entry");
        assertThat(entryDto.getCalories()).isEqualTo(200);
        assertThat(entryDto.getDate()).isEqualTo(localDate);
        assertThat(entryDto.getTime()).isEqualTo(localTime);
        assertThat(entryDto.getUser()).isNotNull();
        assertThat(entryDto.getUser()).isEqualTo(userMapper.toUserDto(user));
    }
}