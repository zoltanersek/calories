package com.zoltan.calories.entry;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "api/entry")
@AllArgsConstructor
public class EntryController {
    private final EntryService entryService;

    @GetMapping
    public Page<EntryDto> getAllEntries(Pageable p) {
        return entryService.getAllEntries(p);
    }

    @PutMapping(path = "/{id}")
    public EntryDto updateEntry(@PathVariable("id") @NotNull @PositiveOrZero Long id, @RequestBody @Valid EntryDto entryDto) {
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
