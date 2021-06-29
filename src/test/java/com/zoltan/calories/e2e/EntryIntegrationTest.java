package com.zoltan.calories.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoltan.calories.entry.CreateEntryRequest;
import com.zoltan.calories.entry.EntryDto;
import com.zoltan.calories.entry.UpdateEntryRequest;
import com.zoltan.calories.user.AuthRequest;
import com.zoltan.calories.user.CreateUserRequest;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Tag("integration")
public class EntryIntegrationTest {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws Exception{
        register();
        String token = login();
        EntryDto entryDto = createEntry(token);
        getAllEntries(token, entryDto);
        getEntryByName(token, entryDto);
        entryDto = updateEntry(token, entryDto);
        getEntryByName(token, entryDto);
        deleteEntry(token, entryDto);
        checkEntryNotExists(token, entryDto);
    }

    private void checkEntryNotExists(String token, EntryDto entryDto) throws Exception {
        mockMvc.perform(get("/api/entry?search=text eq " + entryDto.getText())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", empty()));
    }

    private void deleteEntry(String token, EntryDto entryDto) throws Exception {
        mockMvc.perform(delete("/api/entry/" + entryDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    private EntryDto updateEntry(String token, EntryDto entryDto) throws Exception {
        UpdateEntryRequest updateEntryRequest = new UpdateEntryRequest(entryDto.getId(), entryDto.getDate(), entryDto.getTime(), "entry_updated", 300);

        String body = mockMvc.perform(put("/api/entry/" + entryDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(updateEntryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(body, EntryDto.class);
    }

    private void getEntryByName(String token, EntryDto entryDto) throws Exception {
        mockMvc.perform(get("/api/entry?search=text eq " + entryDto.getText())
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", is(entryDto.getId().intValue())))
                .andExpect(jsonPath("$.content[0].date", is(DATE_FORMATTER.format(entryDto.getDate()))))
                .andExpect(jsonPath("$.content[0].time", is(TIME_FORMATTER.format(entryDto.getTime()))))
                .andExpect(jsonPath("$.content[0].text", is(entryDto.getText())))
                .andExpect(jsonPath("$.content[0].calories", is(entryDto.getCalories())))
                .andExpect(jsonPath("$.content[0].underBudget", is(true)));
    }

    private void getAllEntries(String token, EntryDto entryDto) throws Exception {
        mockMvc.perform(get("/api/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].id", is(entryDto.getId().intValue())))
                .andExpect(jsonPath("$.content[0].text", is(entryDto.getText())));
    }

    private EntryDto createEntry(String token) throws Exception {
        CreateEntryRequest createEntryRequest = new CreateEntryRequest(LocalDate.now(), LocalTime.now(), "entry", 200);

        String body = mockMvc.perform(post("/api/entry")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(createEntryRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readValue(body, EntryDto.class);
    }

    private void register() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("entry_test_user", "test_user");

        mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private String login() throws Exception {
        AuthRequest authRequest = new AuthRequest("entry_test_user", "test_user");

        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        return mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

}
