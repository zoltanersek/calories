package com.zoltan.calories.entry;

import com.zoltan.calories.search.BasicOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ValidationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static com.zoltan.calories.search.Operator.OP_EQ;
import static com.zoltan.calories.search.Operator.OP_GT;
import static com.zoltan.calories.search.Operator.OP_LT;
import static com.zoltan.calories.search.Operator.OP_NE;

@AllArgsConstructor
@Data
public class EntrySpecification implements Specification<Entry> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private BasicOperation basicOperation;

    @Override
    public Predicate toPredicate(Root<Entry> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        if (basicOperation.getKey().equals("date")) {
            basicOperation.setValue(LocalDate.parse(basicOperation.getValue().toString()));
        }
        if (basicOperation.getKey().equals("time")) {
            basicOperation.setValue(LocalTime.parse(basicOperation.getValue().toString(), TIME_FORMATTER));
        }
        try {
            switch (basicOperation.getOperation()) {
                case OP_EQ:
                    return criteriaBuilder.equal(root.get(basicOperation.getKey()), basicOperation.getValue());
                case OP_NE:
                    return criteriaBuilder.notEqual(root.get(basicOperation.getKey()), basicOperation.getValue());
                case OP_GT:
                    return criteriaBuilder.greaterThan(root.get(basicOperation.getKey()), basicOperation.getValue().toString());
                case OP_LT:
                    return criteriaBuilder.lessThan(root.get(basicOperation.getKey()), basicOperation.getValue().toString());
                default:
                    return null;
            }
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Attribute " + basicOperation.getKey() + " not found");
        }
    }
}
