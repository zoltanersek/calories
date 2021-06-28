package com.zoltan.calories.search;

import com.zoltan.calories.entry.Entry;
import com.zoltan.calories.entry.EntrySpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Deque;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
class SearchParserTest {

    private SearchParser searchParser;

    @BeforeEach
    void setUp() {
        searchParser = new SearchParser();
    }

    @Test
    void test_parse_dateAndCalories_noException() {
        Specification<Entry> spec = searchParser.parse("(date eq 2016-05-01  ) AND ((calories gt 20) OR (calories lt 10))", EntrySpecification::new);
        assertThat(spec).isNotNull();
    }

    @Test
    void test_parse_dateAndCalories_posixOkay() {
        Deque<?> parsed = searchParser.parse("(date eq 2016-05-01  ) AND ((calories gt 20)          OR (calories lt 10))");
        assertThat(parsed.size()).isEqualTo(5);
        assertThat(parsed.pop()).isEqualTo("and");
        assertThat(parsed.pop()).isEqualTo("or");
        assertThat(parsed.pop()).isEqualTo(new BasicOperation("calories", "lt", "10"));
        assertThat(parsed.pop()).isEqualTo(new BasicOperation("calories", "gt", "20"));
        assertThat(parsed.pop()).isEqualTo(new BasicOperation("date", "eq", "2016-05-01"));
    }

    @Test
    void test_parse_unbalanced_exceptionThrown() {
        try {
            searchParser.parse("(()");
            fail("Expected SearchParserException");
        } catch (SearchParserException ex) {
            assertThat(ex.getMessage()).isEqualTo("invalid search (() parentheses not balanced");
        }
    }

    @Test
    void test_parse_invalidToken_exceptionThrown() {
        try {
            searchParser.parse("(calories gt 10) lt (calories gt 20)");
            fail("Expected SearchParserException");
        } catch (SearchParserException ex) {
            assertThat(ex.getMessage()).isEqualTo("invalid search (calories gt 10) lt (calories gt 20) lt is not in the right place");
        }
    }

    @Test
    void test_parse_invalidOperator_exceptionThrown() {
        try {
            searchParser.parse("calories gta 10");
            fail("Expected SearchParserException");
        } catch (SearchParserException ex) {
            assertThat(ex.getMessage()).isEqualTo("invalid search calories gta 10 operator gta not found");
        }
    }

    @Test
    void test_parse_invalidSearch_exceptionThrown() {
        try {
            searchParser.parse("calories gt 10 20");
            fail("Expected SearchParserException");
        } catch (SearchParserException ex) {
            assertThat(ex.getMessage()).isEqualTo("invalid search calories gt 10 20");
        }
    }

    @Test
    void test_parse_unbalancedLong_exceptionThrown() {
        try {
            searchParser.parse("(date eq 2016-05-01  ) AND ((calories gt 20) OR calories lt 10))");
            fail("Expected SearchParserException");
        } catch (SearchParserException ex) {
            assertThat(ex.getMessage()).isEqualTo("invalid search (date eq 2016-05-01  ) AND ((calories gt 20) OR calories lt 10)) parentheses not balanced");
        }
    }
}