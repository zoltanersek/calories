package com.zoltan.calories.nutritionix;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class NutritionixServiceTest {

    @MockBean
    private RestTemplate restTemplate;

    private NutritionixService nutritionixService;

    @BeforeEach
    void setUp() {
        nutritionixService = new NutritionixService(restTemplate);
    }

    @Test
    void test_tryGetCaloriesForItem_exception_returnsZero() {
        given(restTemplate.postForEntity(eq("/natural/nutrients"), any(NutritionixRequest.class), eq(NutritionixResponse.class)))
                .willThrow(new RestClientException("test error"));

        Integer result = nutritionixService.tryGetCaloriesForItem("test");

        assertThat(result).isEqualTo(0);
    }

    @Test
    void test_tryGetCaloriesForItem_noException_returnsResponse() {
        NutritionixFood food1 = new NutritionixFood(120.0);
        NutritionixFood food2 = new NutritionixFood(140.0);
        NutritionixResponse nutritionixResponse = new NutritionixResponse(List.of(food1, food2));
        ArgumentCaptor<NutritionixRequest> nutritionixRequestArgumentCaptor = ArgumentCaptor.forClass(NutritionixRequest.class);
        given(restTemplate.postForEntity(eq("/natural/nutrients"), nutritionixRequestArgumentCaptor.capture(), eq(NutritionixResponse.class)))
                .willReturn(ResponseEntity.ok(nutritionixResponse));

        Integer result = nutritionixService.tryGetCaloriesForItem("test");

        assertThat(result).isEqualTo(260);
        NutritionixRequest request = nutritionixRequestArgumentCaptor.getValue();
        assertThat(request.getQuery()).isEqualTo("test");
    }
}