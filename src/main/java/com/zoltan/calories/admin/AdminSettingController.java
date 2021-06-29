package com.zoltan.calories.admin;

import com.zoltan.calories.setting.SettingDto;
import com.zoltan.calories.setting.SettingService;
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
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "api/admin/setting")
@RolesAllowed("ROLE_ADMIN")
@AllArgsConstructor
public class AdminSettingController {
    private final SettingService settingService;

    @GetMapping
    public Page<SettingDto> getAllSettings(@RequestParam(value = "search", required = false) String search, Pageable p) {
        return settingService.getAllSettings(search, p);
    }

    @PutMapping(path = "/{id}")
    public SettingDto updateSetting(@PathVariable("id") @PositiveOrZero Long id, @RequestBody @Valid SettingDto settingDto) {
        return settingService.updateSetting(id, settingDto);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteSetting(@PathVariable("id") @PositiveOrZero Long id) {
        settingService.deleteSetting(id);
    }
}
