package com.zoltan.calories.admin;

import com.zoltan.calories.setting.SettingDto;
import com.zoltan.calories.setting.SettingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class AdminSettingControllerTest {

    @MockBean
    private SettingService settingService;

    private AdminSettingController adminSettingController;

    @BeforeEach
    void setUp() {
        adminSettingController = new AdminSettingController(settingService);
    }

    @Test
    void getAllSettings_settingsPresent_settingServiceCalled() {
        SettingDto settingDto = new SettingDto("name", "value", null, null);
        Page<SettingDto> page = new PageImpl<>(List.of(settingDto));
        given(settingService.getAllSettings(anyString(), any())).willReturn(page);

        List<SettingDto> foundSettings = adminSettingController.getAllSettings("filter", null).getContent();

        assertThat(foundSettings.size()).isEqualTo(1);
        assertThat(foundSettings.get(0).getName()).isEqualTo("name");
        assertThat(foundSettings.get(0).getValue()).isEqualTo("value");
    }

    @Test
    void updateSetting_validRequest_settingServiceCalled() {
        SettingDto settingDto = new SettingDto("name", "value", null, null);

        adminSettingController.updateSetting(1L, settingDto);

        verify(settingService).updateSetting(eq(1L), eq(settingDto));
    }

    @Test
    void deleteSetting_validName_settingServiceCalled() {
        adminSettingController.deleteSetting(1L);

        verify(settingService).deleteSetting(eq(1L));
    }
}