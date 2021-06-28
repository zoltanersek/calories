package com.zoltan.calories.setting;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.search.SearchParser;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class SettingService {
    private final SettingRepository settingRepository;
    private final UserService userService;
    private final SettingMapper settingMapper;
    private final SearchParser searchParser;

    public Page<SettingDto> getAllSettings(String search, Pageable pageable) {
        Specification<Setting> specification = null;
        if (!ObjectUtils.isEmpty(search)) {
            specification = searchParser.parse(search, SettingSpecification::new);
        }
        return settingRepository.findAll(specification, pageable).map(settingMapper::toSettingDtoWithUserDto);
    }

    public Page<SettingDto> getAllSettingsForUser(String search, Pageable pageable) {
        Specification<Setting> specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), userService.getCurrentUser());
        if (!ObjectUtils.isEmpty(search)) {
            specification = specification.and(searchParser.parse(search, SettingSpecification::new));
        }
        return settingRepository.findAll(specification, pageable).map(settingMapper::toSettingDto);
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

    public Optional<SettingDto> getSettingForUser(String name) {
        return settingRepository.getSettingByNameForCurrentUser(name).map(settingMapper::toSettingDto);
    }

    @Transactional
    public SettingDto updateSetting(Long id, SettingDto settingDto) {
        Optional<Setting> existingSettingOptional = settingRepository.findById(id);
        if (existingSettingOptional.isEmpty()) {
            throw new NotFoundException("Setting " + id + " not found");
        }
        Setting existingSetting = existingSettingOptional.get();
        if (!existingSetting.getId().equals(id)) {
            throw new ValidationException("Id change for setting not supported");
        }
        if (!existingSetting.getName().equals(settingDto.getName())) {
            throw new ValidationException("Name change for setting not supported");
        }
        existingSetting.setValue(settingDto.getValue());
        return settingMapper.toSettingDtoWithUserDto(existingSetting);
    }

    @Transactional
    public SettingDto updateSettingForUser(String name, SettingDto settingDto) {
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

    public void deleteSettingForUser(String name) {
        settingRepository.deleteByNameForCurrentUser(name);
    }

    public void deleteSetting(Long id) {
        settingRepository.findById(id).ifPresent(settingRepository::delete);
    }
}
