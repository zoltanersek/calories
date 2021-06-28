package com.zoltan.calories.nutritionix;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

@Service
@AllArgsConstructor
@Log4j2
public class NutritionixService {
    private final RestTemplate restTemplate;

    public Integer tryGetCaloriesForItem(String description) {
        try {
            NutritionixRequest request = new NutritionixRequest(description);
            ResponseEntity<NutritionixResponse> responseResponseEntity =
                    restTemplate.postForEntity("/natural/nutrients", request, NutritionixResponse.class);
            if (responseResponseEntity.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(responseResponseEntity.getBody())
                        .map(NutritionixResponse::getFoods)
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(Objects::nonNull)
                        .map(NutritionixFood::getCalories)
                        .filter(Objects::nonNull)
                        .map(Math::round)
                        .mapToInt(Long::intValue)
                        .sum();
            }
        } catch (RestClientException ex) {
            log.warn("Could not get calories for {}", description, ex);
        }
        return 0;
    }
}
