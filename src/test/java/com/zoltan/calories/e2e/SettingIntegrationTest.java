package com.zoltan.calories.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zoltan.calories.setting.SettingDto;
import com.zoltan.calories.setting.Settings;
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

import static org.hamcrest.CoreMatchers.is;

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
public class SettingIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void test() throws Exception{
        register();
        String token = login();
        createSetting(token);
        getAllSettings(token);
        getSettingByName(token, "3000");
        updateSetting(token);
        getSettingByName(token, "2500");
        deleteSetting(token);
        checkSettingNotExists(token);
    }

    private void checkSettingNotExists(String token) throws Exception {
        mockMvc.perform(get("/api/setting/" + Settings.CALORIES_DAILY_TARGET)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    private void deleteSetting(String token) throws Exception {
        mockMvc.perform(delete("/api/setting/" + Settings.CALORIES_DAILY_TARGET)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());
    }

    private void updateSetting(String token) throws Exception {
        SettingDto settingDto = new SettingDto(Settings.CALORIES_DAILY_TARGET, "2500", null, null);

        mockMvc.perform(put("/api/setting/" + Settings.CALORIES_DAILY_TARGET)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(settingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private void getSettingByName(String token, String expectedValue) throws Exception {
        mockMvc.perform(get("/api/setting/" + Settings.CALORIES_DAILY_TARGET)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is(Settings.CALORIES_DAILY_TARGET)))
                .andExpect(jsonPath("$.value", is(expectedValue)));
    }

    private void getAllSettings(String token) throws Exception {
        mockMvc.perform(get("/api/setting")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0].name", is(Settings.CALORIES_DAILY_TARGET)))
                .andExpect(jsonPath("$.content[0].value", is("3000")));
    }

    private void createSetting(String token) throws Exception {
        SettingDto settingDto = new SettingDto(Settings.CALORIES_DAILY_TARGET, "3000", null, null);

         mockMvc.perform(post("/api/setting")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .content(objectMapper.writeValueAsString(settingDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private void register() throws Exception {
        CreateUserRequest createUserRequest = new CreateUserRequest("setting_test_user", "test_user");

         mockMvc.perform(post("/api/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createUserRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    private String login() throws Exception {
        AuthRequest authRequest = new AuthRequest("setting_test_user", "test_user");

        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andReturn();

        return mvcResult.getResponse().getHeader(HttpHeaders.AUTHORIZATION);
    }

}
