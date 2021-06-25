package com.zoltan.calories.setting;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SettingService {
    private final SettingRepository settingRepository;
    private final UserService userService;
    private final SettingMapper settingMapper;

    public Page<SettingDto> getAllSettings(Pageable pageable) {
        return settingRepository.getSettingsForCurrentUser(pageable).map(settingMapper::toSettingDto);
    }

    public SettingDto createSetting(SettingDto settingDto) {
        if (!Settings.NAMES.contains(settingDto.getName())) {
            throw new ValidationException("Setting " + settingDto.getName() + " not found");
        }
        if (settingRepository.getSettingByNameForCurrentUser(settingDto.getName()).isPresent()) {
            throw new ValidationException("Setting " + settingDto.getName() + " already set for user");
        }
        User user = userService.getCurrentUser();
        Setting setting = new Setting(settingDto.getName(), settingDto.getValue(), user);
        return settingMapper.toSettingDto(settingRepository.save(setting));
    }

    public Optional<SettingDto> getSetting(String name) {
        return settingRepository.getSettingByNameForCurrentUser(name).map(settingMapper::toSettingDto);
    }

    @Transactional
    public SettingDto updateSetting(String name, SettingDto settingDto) {
        Optional<Setting> existingSettingOptional = settingRepository.getSettingByNameForCurrentUser(name);
        if (existingSettingOptional.isEmpty()) {
            throw new NotFoundException("Setting " + name + " not found for user");
        }
        Setting existingSetting = existingSettingOptional.get();
        if (!existingSetting.getName().equals(settingDto.getName())) {
            throw new ValidationException("Name change for setting not supported");
        }
        existingSetting.setValue(settingDto.getValue());
        return settingMapper.toSettingDto(existingSetting);
    }

    public void deleteSetting(String name) {
        settingRepository.deleteByNameForCurrentUser(name);
    }
}
