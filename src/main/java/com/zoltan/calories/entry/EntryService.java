package com.zoltan.calories.entry;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.nutritionix.NutritionixService;
import com.zoltan.calories.setting.SettingDto;
import com.zoltan.calories.setting.SettingService;
import com.zoltan.calories.setting.Settings;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

    public Page<EntryDto> getAllEntries(Pageable pageable) {
        //todo: advanced filtering
        return entryRepository.getEntriesForCurrentUser(pageable)
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
        Optional<Entry> existingEntryOptional = entryRepository.getEntryByIdForCurrentUser(id);
        if (existingEntryOptional.isEmpty()) {
            throw new NotFoundException("Entry " + id + " not found for user");
        }
        Entry existingEntry = existingEntryOptional.get();
        if (!existingEntry.getId().equals(entryDto.getId())) {
            throw new ValidationException("Id change for entry not supported");
        }
        existingEntry.setDate(entryDto.getDate());
        existingEntry.setTime(entryDto.getTime());
        existingEntry.setCalories(entryDto.getCalories());
        existingEntry.setText(entryDto.getText());
        return entryMapper.toEntryDto(existingEntry).withUnderBudget(isDailyTotalHigherThanTargetForDate(entryDto.getDate()));
    }

    public void deleteEntry(Long id) {
        entryRepository.deleteByIdForCurrentUser(id);
    }

    private boolean isDailyTotalHigherThanTargetForDate(LocalDate localDate) {
        int total = entryRepository.getTotalForDayForCurrentUser(localDate).orElse(0);
        Optional<SettingDto> setting = settingService.getSetting(Settings.CALORIES_DAILY_TARGET);

        return setting.map(SettingDto::getValue)
                .map(Integer::parseInt)
                .map(target -> total <= target)
                .orElse(true);
    }


}
