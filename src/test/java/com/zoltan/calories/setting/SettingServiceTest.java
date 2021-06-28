package com.zoltan.calories.setting;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.search.BasicOperation;
import com.zoltan.calories.search.SearchParser;
import com.zoltan.calories.user.Role;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserMapper;
import com.zoltan.calories.user.UserService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class SettingServiceTest {

    @MockBean
    private SettingRepository settingRepository;
    @MockBean
    private UserService userService;
    private SettingMapper settingMapper;
    private UserMapper userMapper;
    @MockBean
    private SearchParser searchParser;

    private SettingService settingService;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        settingMapper = new SettingMapper(userMapper);
        settingService = new SettingService(settingRepository, userService, settingMapper, searchParser);
    }

    @Test
    void test_getAllSettings_settingsFound_settingsReturned() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2500", user);
        given(searchParser.parse(eq("search"), ArgumentMatchers.<Function<BasicOperation, Specification<Setting>>>any()))
                .willReturn(new SettingSpecification(new BasicOperation()));
        given(settingRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(new PageImpl<>(List.of(setting)));

        Page<SettingDto> search = settingService.getAllSettings("search", Pageable.unpaged());

        assertThat(search.getContent().size()).isEqualTo(1);
        SettingDto foundSetting = search.getContent().get(0);
        assertThat(foundSetting).isEqualTo(settingMapper.toSettingDtoWithUserDto(setting));
    }

    @Test
    void test_getAllSettingsForUser_settingsFound_settingsReturned() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2500", user);
        given(searchParser.parse(eq("search"), ArgumentMatchers.<Function<BasicOperation, Specification<Setting>>>any()))
                .willReturn(new SettingSpecification(new BasicOperation()));
        given(settingRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(new PageImpl<>(List.of(setting)));

        Page<SettingDto> search = settingService.getAllSettingsForUser("search", Pageable.unpaged());

        assertThat(search.getContent().size()).isEqualTo(1);
        SettingDto foundSetting = search.getContent().get(0);
        assertThat(foundSetting).isEqualTo(settingMapper.toSettingDto(setting));

    }

    @Test
    void test_createSetting_invalidName_exceptionThrown() {
        SettingDto settingDto = SettingDto.builder().name("invalid").value("2500").build();

        try {
            settingService.createSetting(settingDto);
            fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Setting invalid not found");
        }
    }

    @Test
    void test_createSetting_alreadySet_exceptionThrown() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        SettingDto settingDto = SettingDto.builder().name(Settings.CALORIES_DAILY_TARGET).value("2500").build();
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "1000", user);
        given(settingRepository.getSettingByNameForCurrentUser(Settings.CALORIES_DAILY_TARGET)).willReturn(Optional.of(setting));
        given(userService.getCurrentUser()).willReturn(user);

        try {
            settingService.createSetting(settingDto);
            fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Setting calories.daily.target already set for user");
        }
    }

    @Test
    void test_createSetting_validSetting_settingCreated() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        SettingDto settingDto = SettingDto.builder().name(Settings.CALORIES_DAILY_TARGET).value("2500").build();
        doAnswer(invocation -> {
            Setting s = (Setting)invocation.getArguments()[0];
            s.setId(1L);
            return s;
        }).when(settingRepository).save(any());
        given(userService.getCurrentUser()).willReturn(user);

        settingDto = settingService.createSetting(settingDto);

        ArgumentCaptor<Setting> settingArgumentCaptor = ArgumentCaptor.forClass(Setting.class);
        verify(settingRepository).save(settingArgumentCaptor.capture());
        Setting savedSetting = settingArgumentCaptor.getValue();
        assertThat(savedSetting.getId()).isEqualTo(1L);
        assertThat(savedSetting.getUser()).isEqualTo(user);
        assertThat(savedSetting.getName()).isEqualTo(Settings.CALORIES_DAILY_TARGET);
        assertThat(savedSetting.getValue()).isEqualTo("2500");
        assertThat(settingDto).isEqualTo(settingMapper.toSettingDto(savedSetting));
    }

    @Test
    void test_getSettingForUser_settingExists_settingReturned() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "1000", user);
        given(settingRepository.getSettingByNameForCurrentUser(Settings.CALORIES_DAILY_TARGET)).willReturn(Optional.of(setting));
        given(userService.getCurrentUser()).willReturn(user);

        Optional<SettingDto> settingForUser = settingService.getSettingForUser(Settings.CALORIES_DAILY_TARGET);
        assertThat(settingForUser.isPresent()).isTrue();
        assertThat(settingForUser.get()).isEqualTo(settingMapper.toSettingDto(setting));
    }

    @Test
    void test_getSettingForUser_settingDoesntExist_emptyReturned() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        given(userService.getCurrentUser()).willReturn(user);

        Optional<SettingDto> settingForUser = settingService.getSettingForUser(Settings.CALORIES_DAILY_TARGET);
        assertThat(settingForUser.isPresent()).isFalse();
    }

    @Test
    void test_updateSetting_settingDoesntExist_exceptionThrown() {
        given(settingRepository.findById(any())).willReturn(Optional.empty());
        SettingDto settingDto = new SettingDto(Settings.CALORIES_DAILY_TARGET, "3000", null, null);

        try {
            settingService.updateSetting(1L, settingDto);
            Assertions.fail("Expected NotFoundException");
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("Setting 1 not found");
        }
    }

    @Test
    void test_updateSetting_differentId_exceptionThrown() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2000", user);
        setting.setId(1L);
        given(settingRepository.findById(any())).willReturn(Optional.of(setting));
        SettingDto settingDto = new SettingDto(Settings.CALORIES_DAILY_TARGET, "3000", null, null);

        try {
            settingService.updateSetting(2L, settingDto);
            Assertions.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Id change for setting not supported");
        }
    }

    @Test
    void test_updateSetting_differentName_exceptionThrown() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2000", user);
        setting.setId(1L);
        given(settingRepository.findById(any())).willReturn(Optional.of(setting));
        SettingDto settingDto = new SettingDto("new value", "3000", null, null);

        try {
            settingService.updateSetting(1L, settingDto);
            Assertions.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Name change for setting not supported");
        }
    }

    @Test
    void test_updateSetting_settingValid_valueUpdated() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2000", user);
        setting.setId(1L);
        given(settingRepository.findById(any())).willReturn(Optional.of(setting));
        SettingDto settingDto = new SettingDto(Settings.CALORIES_DAILY_TARGET, "3000", null, null);
        settingDto = settingService.updateSetting(1L, settingDto);

        assertThat(settingDto.getValue()).isEqualTo("3000");
    }

    @Test
    void test_updateSettingForUser_settingDoesntExist_exceptionThrown() {
        given(settingRepository.getSettingByNameForCurrentUser(any())).willReturn(Optional.empty());
        SettingDto settingDto = new SettingDto(Settings.CALORIES_DAILY_TARGET, "3000", null, null);

        try {
            settingService.updateSettingForUser(Settings.CALORIES_DAILY_TARGET, settingDto);
            Assertions.fail("Expected NotFoundException");
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("Setting calories.daily.target not found for user");
        }
    }

    @Test
    void test_updateSettingForUser_differentName_exceptionThrown() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2000", user);
        setting.setId(1L);
        given(settingRepository.getSettingByNameForCurrentUser(any())).willReturn(Optional.of(setting));
        SettingDto settingDto = new SettingDto("new value", "3000", null, null);

        try {
            settingService.updateSettingForUser(Settings.CALORIES_DAILY_TARGET, settingDto);
            Assertions.fail("Expected ValidationException");
        } catch (ValidationException ex) {
            assertThat(ex.getMessage()).isEqualTo("Name change for setting not supported");
        }
    }

    @Test
    void test_updateSettingForUser_settingValid_valueUpdated() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2000", user);
        setting.setId(1L);
        given(settingRepository.getSettingByNameForCurrentUser(any())).willReturn(Optional.of(setting));
        SettingDto settingDto = new SettingDto(Settings.CALORIES_DAILY_TARGET, "3000", null, null);
        settingDto = settingService.updateSettingForUser(Settings.CALORIES_DAILY_TARGET, settingDto);

        assertThat(settingDto.getValue()).isEqualTo("3000");
    }

    @Test
    void test_deleteSetting_settingFound_repositoryCalled() {
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Setting setting = new Setting(Settings.CALORIES_DAILY_TARGET, "2000", user);
        setting.setId(1L);
        given(settingRepository.findById(eq(1L))).willReturn(Optional.of(setting));

        settingService.deleteSetting(1L);

        ArgumentCaptor<Setting> settingArgumentCaptor = ArgumentCaptor.forClass(Setting.class);
        verify(settingRepository).delete(settingArgumentCaptor.capture());
        Setting foundSetting = settingArgumentCaptor.getValue();
        assertThat(foundSetting).isEqualTo(setting);
    }

    @Test
    void test_deleteSettingForUser_settingFound_repositoryCalled() {
        settingService.deleteSettingForUser(Settings.CALORIES_DAILY_TARGET);

        verify(settingRepository).deleteByNameForCurrentUser(Settings.CALORIES_DAILY_TARGET);
    }
}