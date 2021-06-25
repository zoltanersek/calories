package com.zoltan.calories.setting;

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
import javax.validation.constraints.NotEmpty;


@RestController
@RequestMapping(path = "api/setting")
@AllArgsConstructor
public class SettingController {
    private final SettingService settingService;

    @GetMapping
    public Page<SettingDto> getAllSettings(Pageable p) {
        return settingService.getAllSettings(p);
    }

    @GetMapping(path = "/{name}")
    public SettingDto getSetting(@PathVariable("name") @NotEmpty String name) {
        return settingService.getSetting(name);
    }

    @PutMapping(path = "/{name}")
    public SettingDto updateSetting(@PathVariable("name") @NotEmpty String name, @RequestBody @Valid SettingDto settingDto) {
        return settingService.updateSetting(name, settingDto);
    }

    @PostMapping
    public SettingDto createSetting(@RequestBody @Valid SettingDto settingDto) {
        return settingService.createSetting(settingDto);
    }

    @DeleteMapping(path = "/{name}")
    public void deleteSetting(@PathVariable("name") @NotEmpty String name) {
        settingService.deleteSetting(name);
    }
}
