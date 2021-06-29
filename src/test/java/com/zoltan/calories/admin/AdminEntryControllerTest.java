package com.zoltan.calories.admin;

import com.zoltan.calories.entry.EntryDto;
import com.zoltan.calories.entry.EntryService;
import com.zoltan.calories.entry.UpdateEntryRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AdminEntryControllerTest {

    @MockBean
    private EntryService entryService;
    private AdminEntryController adminEntryController;

    @BeforeEach
    void setUp() {
        adminEntryController = new AdminEntryController(entryService);
    }

    @Test
    void getAllEntries_entriesPresent_entryServiceCalled() {
        EntryDto entryDto = new EntryDto(1L, LocalDate.now(), LocalTime.now(), "entry", 200, true, null);
        Page<EntryDto> page = new PageImpl<>(List.of(entryDto));
        given(entryService.getAllEntries(anyString(), any())).willReturn(page);

        List<EntryDto> foundEntries = adminEntryController.getAllEntries("filter", null).getContent();

        assertThat(foundEntries.size()).isEqualTo(1);
        assertThat(foundEntries.get(0).getText()).isEqualTo("entry");
    }

    @Test
    void updateEntry() {
        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(1L, LocalDate.now(), LocalTime.now(), "entry", 200);

        adminEntryController.updateEntry(1L, updateEntryRequest);

        verify(entryService).updateEntry(eq(1L), eq(updateEntryRequest));
    }

    @Test
    void deleteEntry_validId_entryServiceCalled() {
        adminEntryController.deleteEntry(1L);

        verify(entryService).deleteEntry(eq(1L));
    }
}