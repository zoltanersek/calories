package com.zoltan.calories.search;

import java.util.List;

public class Operator {

    public static final String OP_EQ = "eq";
    public static final String OP_NE = "ne";
    public static final String OP_GT = "gt";
    public static final String OP_LT = "lt";
    public static final List<String> BASIC_OPERATORS = List.of(OP_EQ, OP_NE, OP_GT, OP_LT);

    public static final String OP_GROUP_START = "(";
    public static final String OP_GROUP_END = ")";
    public static final String OP_AND = "and";
    public static final String OP_OR = "or";
    public static final List<String> COMPLEX_OPERATORS = List.of(OP_GROUP_START, OP_GROUP_END, OP_AND, OP_OR);

}
