package com.zoltan.calories;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private List<String> errors;

    public ErrorResponse(String error) {
        this.error = error;
    }

    public ErrorResponse(List<String> errors) {
        this.errors = errors;
    }
}
