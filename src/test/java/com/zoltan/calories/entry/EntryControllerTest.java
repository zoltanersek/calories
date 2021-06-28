package com.zoltan.calories.entry;

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
class EntryControllerTest {

    @MockBean
    private EntryService entryService;

    private EntryController entryController;

    @BeforeEach
    public void setUp() {
        entryController = new EntryController(entryService);
    }

    @Test
    void getAllEntries_entriesPresent_entryServiceCalled() {
        EntryDto entryDto = new EntryDto(1L, LocalDate.now(), LocalTime.now(), "entry", 200, true, null);
        Page<EntryDto> page = new PageImpl<>(List.of(entryDto));
        given(entryService.getAllEntriesForCurrentUser(anyString(), any())).willReturn(page);

        List<EntryDto> foundEntries = entryController.getAllEntries("filter", null).getContent();

        assertThat(foundEntries.size()).isEqualTo(1);
        assertThat(foundEntries.get(0).getText()).isEqualTo("entry");
    }

    @Test
    void updateEntry_validRequest_entryServiceCalled() {
        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(1L, LocalDate.now(), LocalTime.now(), "entry", 200);

        entryController.updateEntry(1L, updateEntryRequest);

        verify(entryService).updateEntryForCurrentUser(eq(1L), eq(updateEntryRequest));
    }

    @Test
    void createEntry_validRequest_entryServiceCalled() {
        CreateEntryRequest createEntryRequest = new CreateEntryRequest(LocalDate.now(), LocalTime.now(), "entry", 200);

        entryController.createEntry(createEntryRequest);

        verify(entryService).createEntry(eq(createEntryRequest));
    }

    @Test
    void deleteEntry_validId_entryServiceCalled() {
        entryController.deleteEntry(1L);

        verify(entryService).deleteEntryForCurrentUser(eq(1L));
    }
}