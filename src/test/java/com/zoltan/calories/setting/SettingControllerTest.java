package com.zoltan.calories.setting;

import com.zoltan.calories.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class SettingControllerTest {

    @MockBean
    private SettingService settingService;

    private SettingController settingController;

    @BeforeEach
    public void setUp() {
        settingController = new SettingController(settingService);
    }

    @Test
    void getAllSettings_settingsPresent_settingServiceCalled() {
        SettingDto settingDto = new SettingDto("name", "value", null, null);
        Page<SettingDto> page = new PageImpl<>(List.of(settingDto));
        given(settingService.getAllSettingsForUser(anyString(), any())).willReturn(page);

        List<SettingDto> foundSettings = settingController.getAllSettings("filter", null).getContent();

        assertThat(foundSettings.size()).isEqualTo(1);
        assertThat(foundSettings.get(0).getName()).isEqualTo("name");
        assertThat(foundSettings.get(0).getValue()).isEqualTo("value");
    }

    @Test
    void getSetting_settingPresent_settingServiceCalled() {
        SettingDto settingDto = new SettingDto("name", "value", null, null);
        given(settingService.getSettingForUser(eq("name"))).willReturn(Optional.of(settingDto));

        SettingDto foundSettings = settingController.getSetting("name");

        assertThat(foundSettings.getName()).isEqualTo("name");
        assertThat(foundSettings.getValue()).isEqualTo("value");
    }

    @Test
    void getSetting_settingNotFound_exceptionThrown() {
        given(settingService.getSettingForUser(eq("name"))).willReturn(Optional.empty());

        try {
            settingController.getSetting("name");
            fail("Expected NotFoundException");
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("Setting name not found for user");
        }
    }

    @Test
    void updateSetting_validRequest_settingServiceCalled() {
        SettingDto settingDto = new SettingDto("name", "value", null, null);

        settingController.updateSetting("name", settingDto);

        verify(settingService).updateSettingForUser(eq("name"), eq(settingDto));
    }

    @Test
    void createSetting_validRequest_settingServiceCalled() {
        SettingDto settingDto = new SettingDto("name", "value", null, null);

        settingController.createSetting(settingDto);

        verify(settingService).createSetting(eq(settingDto));
    }

    @Test
    void deleteSetting_validName_settingServiceCalled() {
        settingController.deleteSetting("name");

        verify(settingService).deleteSettingForUser(eq("name"));
    }
}