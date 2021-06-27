package com.zoltan.calories.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicOperation {
    private String key;
    private String operation;
    private Object value;
}