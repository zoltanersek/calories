package com.zoltan.calories.user;

import com.zoltan.calories.search.BasicOperation;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ValidationException;

import static com.zoltan.calories.search.Operator.OP_EQ;
import static com.zoltan.calories.search.Operator.OP_GT;
import static com.zoltan.calories.search.Operator.OP_LT;
import static com.zoltan.calories.search.Operator.OP_NE;

@AllArgsConstructor
@Data
public class UserSpecification implements Specification<User> {
    private BasicOperation basicOperation;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
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
