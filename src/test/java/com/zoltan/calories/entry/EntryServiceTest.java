package com.zoltan.calories.entry;

import com.zoltan.calories.NotFoundException;
import com.zoltan.calories.nutritionix.NutritionixService;
import com.zoltan.calories.search.BasicOperation;
import com.zoltan.calories.search.SearchParser;
import com.zoltan.calories.setting.SettingDto;
import com.zoltan.calories.setting.SettingService;
import com.zoltan.calories.setting.Settings;
import com.zoltan.calories.user.Role;
import com.zoltan.calories.user.User;
import com.zoltan.calories.user.UserMapper;
import com.zoltan.calories.user.UserService;
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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
class EntryServiceTest {

    @MockBean
    private EntryRepository entryRepository;
    @MockBean
    private UserService userService;
    @MockBean
    private SettingService settingService;
    @MockBean
    private NutritionixService nutritionixService;
    @MockBean
    private SearchParser searchParser;
    private UserMapper userMapper;
    private EntryMapper entryMapper;
    private EntryService entryService;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        entryMapper = new EntryMapper(userMapper);
        entryService = new EntryService(entryRepository, userService, entryMapper, settingService, nutritionixService, searchParser);
    }

    @Test
    void test_getAllEntries_entriesFound_entriesReturned() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Entry entry = new Entry(localDate, LocalTime.now(), "entry", 3100, user);
        entry.setId(1L);
        given(entryRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(new PageImpl<>(List.of(entry)));
        given(searchParser.parse(eq("search"), ArgumentMatchers.<Function<BasicOperation, Specification<Entry>>>any()))
                .willReturn(new EntrySpecification(new BasicOperation()));

        Page<EntryDto> search = entryService.getAllEntries("search", Pageable.unpaged());

        assertThat(search.getContent().size()).isEqualTo(1);
        EntryDto foundEntry = search.getContent().get(0);
        assertThat(foundEntry).isEqualTo(entryMapper.toEntryDtoWithUserDto(entry));
    }

    @Test
    void test_getAllEntriesForCurrentUser_entriesFound_entriesReturned() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Entry entry = new Entry(localDate, LocalTime.now(), "entry", 3100, user);
        entry.setId(1L);
        SettingDto settingDto = SettingDto.builder().name(Settings.CALORIES_DAILY_TARGET).value("2500").build();
        given(entryRepository.findAll(any(Specification.class), any(Pageable.class))).willReturn(new PageImpl<>(List.of(entry)));
        given(searchParser.parse(eq("search"), ArgumentMatchers.<Function<BasicOperation, Specification<Entry>>>any()))
                .willReturn(new EntrySpecification(new BasicOperation()));
        given(entryRepository.getTotalForDayForCurrentUser(localDate)).willReturn(Optional.of(3100));
        given(settingService.getSettingForUser(eq(Settings.CALORIES_DAILY_TARGET))).willReturn(Optional.of(settingDto));

        Page<EntryDto> search = entryService.getAllEntriesForCurrentUser("search", Pageable.unpaged());

        assertThat(search.getContent().size()).isEqualTo(1);
        EntryDto foundEntry = search.getContent().get(0);
        assertThat(foundEntry).isEqualTo(entryMapper.toEntryDto(entry).withUnderBudget(false));

    }

    @Test
    void test_createEntry_caloriesProvided_nutritionixNotCalled() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        SettingDto settingDto = SettingDto.builder().name(Settings.CALORIES_DAILY_TARGET).value("2500").build();
        given(userService.getCurrentUser()).willReturn(user);
        given(entryRepository.getTotalForDayForCurrentUser(localDate)).willReturn(Optional.of(3100));
        given(settingService.getSettingForUser(eq(Settings.CALORIES_DAILY_TARGET))).willReturn(Optional.of(settingDto));
        doAnswer(invocation -> {
            Entry e = (Entry)invocation.getArguments()[0];
            e.setId(1L);
            return e;
        }).when(entryRepository).save(any());

        CreateEntryRequest createEntryRequest =
                new CreateEntryRequest(LocalDate.now(), localTime, "entry", 200);
        EntryDto entryDto = entryService.createEntry(createEntryRequest);

        ArgumentCaptor<Entry> entryArgumentCaptor = ArgumentCaptor.forClass(Entry.class);
        verify(entryRepository).save(entryArgumentCaptor.capture());
        Entry savedEntry = entryArgumentCaptor.getValue();
        assertThat(savedEntry.getUser()).isEqualTo(user);
        assertThat(savedEntry.getCalories()).isEqualTo(200);
        assertThat(savedEntry.getDate()).isEqualTo(localDate);
        assertThat(savedEntry.getTime()).isEqualTo(localTime);
        assertThat(savedEntry.getText()).isEqualTo("entry");
        assertThat(entryDto).isEqualTo(entryMapper.toEntryDto(savedEntry).withUnderBudget(false));
    }

    @Test
    void test_createEntry_caloriesNotProvided_nutritionixCalled() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        SettingDto settingDto = SettingDto.builder().name(Settings.CALORIES_DAILY_TARGET).value("2500").build();
        given(userService.getCurrentUser()).willReturn(user);
        given(entryRepository.getTotalForDayForCurrentUser(localDate)).willReturn(Optional.of(3100));
        given(settingService.getSettingForUser(eq(Settings.CALORIES_DAILY_TARGET))).willReturn(Optional.of(settingDto));
        given(nutritionixService.tryGetCaloriesForItem("entry")).willReturn(300);
        doAnswer(invocation -> {
            Entry e = (Entry)invocation.getArguments()[0];
            e.setId(1L);
            return e;
        }).when(entryRepository).save(any());

        CreateEntryRequest createEntryRequest =
                new CreateEntryRequest(LocalDate.now(), localTime, "entry", null);
        EntryDto entryDto = entryService.createEntry(createEntryRequest);

        ArgumentCaptor<Entry> entryArgumentCaptor = ArgumentCaptor.forClass(Entry.class);
        verify(entryRepository).save(entryArgumentCaptor.capture());
        Entry savedEntry = entryArgumentCaptor.getValue();
        assertThat(savedEntry.getUser()).isEqualTo(user);
        assertThat(savedEntry.getCalories()).isEqualTo(300);
        assertThat(savedEntry.getDate()).isEqualTo(localDate);
        assertThat(savedEntry.getTime()).isEqualTo(localTime);
        assertThat(savedEntry.getText()).isEqualTo("entry");
        assertThat(entryDto).isEqualTo(entryMapper.toEntryDto(savedEntry).withUnderBudget(false));
    }

    @Test
    void test_updateEntry_entryNotFound_exceptionThrown() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        given(entryRepository.findById(any())).willReturn(Optional.empty());

        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(1L, localDate, localTime, "entry_updated", 500);
        try {
            entryService.updateEntry(1L, updateEntryRequest);
            fail("Expected NotFoundException");
        } catch (NotFoundException ex) {

        }
    }

    @Test
    void test_updateEntry_otherId_exceptionThrown() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Entry existingEntry = new Entry(localDate, localTime, "entry", 100, user);
        existingEntry.setId(1L);
        given(entryRepository.findById(eq(1L))).willReturn(Optional.of(existingEntry));

        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(2L, localDate, localTime, "entry_updated", 500);
        try {
            entryService.updateEntry(1L, updateEntryRequest);
            fail("Expected ValidationException");
        } catch (ValidationException ex) {

        }
    }

    @Test
    void test_updateEntry_entryPresent_entryUpdated() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Entry existingEntry = new Entry(localDate, localTime, "entry", 100, user);
        existingEntry.setId(1L);
        given(entryRepository.findById(eq(1L))).willReturn(Optional.of(existingEntry));

        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(1L, localDate.plusDays(1), localTime.plusHours(1), "entry_updated", 500);
        EntryDto entryDto = entryService.updateEntry(1L, updateEntryRequest);

        assertThat(existingEntry.getDate()).isEqualTo(localDate.plusDays(1));
        assertThat(existingEntry.getTime()).isEqualTo(localTime.plusHours(1));
        assertThat(existingEntry.getText()).isEqualTo("entry_updated");
        assertThat(existingEntry.getCalories()).isEqualTo(500);
        assertThat(entryDto).isEqualTo(entryMapper.toEntryDtoWithUserDto(existingEntry));
    }

    @Test
    void test_updateEntryForCurrentUser_entryNotFound_exceptionThrown() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        given(entryRepository.getEntryByIdForCurrentUser(any())).willReturn(Optional.empty());

        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(1L, localDate, localTime, "entry_updated", 500);
        try {
            entryService.updateEntryForCurrentUser(1L, updateEntryRequest);
            fail("Expected NotFoundException");
        } catch (NotFoundException ex) {

        }
    }

    @Test
    void test_updateEntryForCurrentUser_otherId_exceptionThrown() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Entry existingEntry = new Entry(localDate, localTime, "entry", 100, user);
        existingEntry.setId(1L);
        given(entryRepository.getEntryByIdForCurrentUser(eq(1L))).willReturn(Optional.of(existingEntry));

        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(2L, localDate, localTime, "entry_updated", 500);
        try {
            entryService.updateEntryForCurrentUser(1L, updateEntryRequest);
            fail("Expected ValidationException");
        } catch (ValidationException ex) {

        }
    }

    @Test
    void test_updateEntryForCurrentUser_entryPresent_entryUpdated() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Entry existingEntry = new Entry(localDate, localTime, "entry", 100, user);
        existingEntry.setId(1L);
        SettingDto settingDto = SettingDto.builder().name(Settings.CALORIES_DAILY_TARGET).value("2500").build();
        given(entryRepository.getEntryByIdForCurrentUser(eq(1L))).willReturn(Optional.of(existingEntry));
        given(entryRepository.getTotalForDayForCurrentUser(localDate.plusDays(1))).willReturn(Optional.of(3100));
        given(settingService.getSettingForUser(eq(Settings.CALORIES_DAILY_TARGET))).willReturn(Optional.of(settingDto));

        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(1L, localDate.plusDays(1), localTime.plusHours(1), "entry_updated", 500);
        EntryDto entryDto = entryService.updateEntryForCurrentUser(1L, updateEntryRequest);

        assertThat(existingEntry.getDate()).isEqualTo(localDate.plusDays(1));
        assertThat(existingEntry.getTime()).isEqualTo(localTime.plusHours(1));
        assertThat(existingEntry.getText()).isEqualTo("entry_updated");
        assertThat(existingEntry.getCalories()).isEqualTo(500);
        assertThat(entryDto).isEqualTo(entryMapper.toEntryDto(existingEntry).withUnderBudget(false));
    }

    @Test
    void test_deleteEntry_entryFound_repositoryCalled() {
        LocalDate localDate = LocalDate.of(2021, 6, 28);
        LocalTime localTime = LocalTime.of(12, 0);
        User user = new User(1L, "test", "test", Set.of(Role.ROLE_USER), true);
        Entry existingEntry = new Entry(localDate, localTime, "entry", 100, user);
        existingEntry.setId(1L);
        given(entryRepository.findById(eq(1L))).willReturn(Optional.of(existingEntry));

        entryService.deleteEntry(1L);

        ArgumentCaptor<Entry> entryArgumentCaptor = ArgumentCaptor.forClass(Entry.class);
        verify(entryRepository).delete(entryArgumentCaptor.capture());
        Entry foundEntry = entryArgumentCaptor.getValue();
        assertThat(foundEntry).isEqualTo(existingEntry);
    }

    @Test
    void test_deleteEntryForCurrentUser_entryFound_repositoryCalled() {
        entryService.deleteEntryForCurrentUser(1L);

        verify(entryRepository).deleteByIdForCurrentUser(eq(1L));
    }
}