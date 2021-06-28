package com.zoltan.calories.entry;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.nutritionix.NutritionixService;
import com.zoltan.calories.search.SearchParser;
import com.zoltan.calories.setting.SettingDto;
import com.zoltan.calories.setting.SettingService;
import com.zoltan.calories.setting.Settings;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EntryService {
    private final EntryRepository entryRepository;
    private final UserService userService;
    private final EntryMapper entryMapper;
    private final SettingService settingService;
    private final NutritionixService nutritionixService;
    private final SearchParser searchParser;

    public Page<EntryDto> getAllEntries(String search, Pageable pageable) {
        Specification<Entry> specification = null;
        if (!ObjectUtils.isEmpty(search)) {
            specification = searchParser.parse(search, EntrySpecification::new);
        }
        return entryRepository.findAll(specification, pageable)
                .map(entryMapper::toEntryDtoWithUserDto);
    }

    public Page<EntryDto> getAllEntriesForCurrentUser(String search, Pageable pageable) {
        Specification<Entry> specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), userService.getCurrentUser());
        if (!ObjectUtils.isEmpty(search)) {
            specification = specification.and(searchParser.parse(search, EntrySpecification::new));
        }
        return entryRepository.findAll(specification, pageable)
                .map(entryMapper::toEntryDto)
                .map(entryDto -> entryDto.withUnderBudget(isDailyTotalHigherThanTargetForDate(entryDto.getDate())));
    }

    public EntryDto createEntry(CreateEntryRequest createEntryRequest) {
        User user = userService.getCurrentUser();
        if (createEntryRequest.getCalories() == null) {
            createEntryRequest.setCalories(nutritionixService.tryGetCaloriesForItem(createEntryRequest.getText()));
        }
        Entry entry = new Entry(createEntryRequest.getDate(), createEntryRequest.getTime(),
                createEntryRequest.getText(), createEntryRequest.getCalories(), user);
        return entryMapper.toEntryDto(entryRepository.save(entry)).withUnderBudget(isDailyTotalHigherThanTargetForDate(entry.getDate()));
    }

    @Transactional
    public EntryDto updateEntry(Long id, UpdateEntryRequest entryDto) {
        Optional<Entry> existingEntryOptional = entryRepository.findById(id);
        if (existingEntryOptional.isEmpty()) {
            throw new NotFoundException("Entry " + id + " not found");
        }
        Entry existingEntry = existingEntryOptional.get();
        updateEntryFields(existingEntry, entryDto);
        return entryMapper.toEntryDtoWithUserDto(existingEntry);
    }

    @Transactional
    public EntryDto updateEntryForCurrentUser(Long id, UpdateEntryRequest updateEntryRequest) {
        Optional<Entry> existingEntryOptional = entryRepository.getEntryByIdForCurrentUser(id);
        if (existingEntryOptional.isEmpty()) {
            throw new NotFoundException("Entry " + id + " not found for user");
        }
        Entry existingEntry = existingEntryOptional.get();
        updateEntryFields(existingEntry, updateEntryRequest);
        return entryMapper.toEntryDto(existingEntry).withUnderBudget(isDailyTotalHigherThanTargetForDate(updateEntryRequest.getDate()));
    }

    public void deleteEntry(Long id) {
        entryRepository.findById(id).ifPresent(entryRepository::delete);
    }

    public void deleteEntryForCurrentUser(Long id) {
        entryRepository.deleteByIdForCurrentUser(id);
    }

    private boolean isDailyTotalHigherThanTargetForDate(LocalDate localDate) {
        int total = entryRepository.getTotalForDayForCurrentUser(localDate).orElse(0);
        Optional<SettingDto> setting = settingService.getSettingForUser(Settings.CALORIES_DAILY_TARGET);

        return setting.map(SettingDto::getValue)
                .map(Integer::parseInt)
                .map(target -> total <= target)
                .orElse(true);
    }

    private void updateEntryFields(Entry existingEntry, UpdateEntryRequest updateEntryRequest) {
        if (!existingEntry.getId().equals(updateEntryRequest.getId())) {
            throw new ValidationException("Id change for entry not supported");
        }
        existingEntry.setDate(updateEntryRequest.getDate());
        existingEntry.setTime(updateEntryRequest.getTime());
        existingEntry.setCalories(updateEntryRequest.getCalories());
        existingEntry.setText(updateEntryRequest.getText());
    }

}
