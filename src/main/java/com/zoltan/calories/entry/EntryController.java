package com.zoltan.calories.entry;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "api/entry")
@AllArgsConstructor
@Log4j2
public class EntryController {
    private final EntryService entryService;

    @GetMapping
    public Page<EntryDto> getAllEntries(@RequestParam(value = "search", required = false) String search, Pageable p) {
        log.info("Got search {}", search);
        return entryService.getAllEntries(search, p);
    }

    @PutMapping(path = "/{id}")
    public EntryDto updateEntry(@PathVariable("id") @NotNull @PositiveOrZero Long id, @RequestBody @Valid UpdateEntryRequest entryDto) {
        return entryService.updateEntry(id, entryDto);
    }

    @PostMapping
    public EntryDto createEntry(@RequestBody @Valid CreateEntryRequest createEntryRequest) {
        return entryService.createEntry(createEntryRequest);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteEntry(@PathVariable("id") @NotNull @PositiveOrZero Long id) {
        entryService.deleteEntry(id);
    }
}
