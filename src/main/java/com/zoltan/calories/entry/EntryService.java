package com.zoltan.calories.entry;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EntryService {
    private final EntryRepository entryRepository;
    private final UserRepository userRepository;
    private final EntryMapper entryMapper;

    public Page<EntryDto> getAllEntries(Pageable pageable) {
        //todo: advanced filtering
        return entryRepository.getEntriesForCurrentUser(pageable).map(entryMapper::toEntryDto);
    }

    public EntryDto createEntry(CreateEntryRequest createEntryRequest) {
        //todo: connect to provider, set to 0 if null
        User user = userRepository.getCurrentUser();
        Entry entry = new Entry(LocalDateTime.of(createEntryRequest.getDate(), createEntryRequest.getTime()),
                createEntryRequest.getText(), createEntryRequest.getCalories(), user);
        return entryMapper.toEntryDto(entryRepository.save(entry));
    }

    @Transactional
    public EntryDto updateEntry(Long id, EntryDto entryDto) {
        Optional<Entry> existingEntryOptional = entryRepository.getEntryByIdForCurrentUser(id);
        if (existingEntryOptional.isEmpty()) {
            throw new NotFoundException("Entry " + id + " not found for user");
        }
        Entry existingEntry = existingEntryOptional.get();
        if (!existingEntry.getId().equals(entryDto.getId())) {
            throw new ValidationException("Id change for entry not supported");
        }
        existingEntry.setDate(LocalDateTime.of(entryDto.getDate(), entryDto.getTime()));
        existingEntry.setCalories(entryDto.getCalories());
        existingEntry.setText(entryDto.getText());
        return entryMapper.toEntryDto(existingEntry);
    }

    public void deleteEntry(Long id) {
        entryRepository.deleteByIdForCurrentUser(id);
    }


}
