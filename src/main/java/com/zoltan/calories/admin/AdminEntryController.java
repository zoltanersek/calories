package com.zoltan.calories.admin;

import com.zoltan.calories.entry.EntryDto;
import com.zoltan.calories.entry.EntryService;
import com.zoltan.calories.entry.UpdateEntryRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.security.RolesAllowed;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "api/admin/entry")
@RolesAllowed("ROLE_ADMIN")
@AllArgsConstructor
public class AdminEntryController {
    private final EntryService entryService;

    @GetMapping
    public Page<EntryDto> getAllEntries(@RequestParam(value = "search", required = false) String search, Pageable p) {
        return entryService.getAllEntries(search, p);
    }

    @PutMapping(path = "/{id}")
    public EntryDto updateEntry(@PathVariable("id") @NotNull @PositiveOrZero Long id, @RequestBody @Valid UpdateEntryRequest entryDto) {
        return entryService.updateEntry(id, entryDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteEntry(@PathVariable("id") @NotNull @PositiveOrZero Long id) {
        entryService.deleteEntry(id);
    }
}
