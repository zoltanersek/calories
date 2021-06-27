package com.zoltan.calories.search;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zoltan.calories.search.Operator.*;

@Component
public class SearchParser {

    public <U> Specification<U> parse(String search, Function<BasicOperation, Specification<U>> converter) {

        Deque<?> postFixedExprStack = parse(search);

        Deque<Specification<U>> specStack = new LinkedList<>();

        while (!postFixedExprStack.isEmpty()) {
            Object mayBeOperand = postFixedExprStack.pollLast();

            if (!(mayBeOperand instanceof String)) {
                specStack.push(converter.apply((BasicOperation) mayBeOperand));
            } else {
                Specification<U> operand1 = specStack.pop();
                Specification<U> operand2 = specStack.pop();
                if (mayBeOperand.equals(OP_AND)) {
                    specStack.push(Specification.where(operand1)
                            .and(operand2));
                } else if (mayBeOperand.equals(OP_OR)) {
                    specStack.push(Specification.where(operand1)
                            .or(operand2));
                }
            }
        }
        return specStack.pop();
    }

    private Deque<?> parse(String search) {
        Deque<Object> output = new LinkedList<>();
        Deque<String> stack = new LinkedList<>();

        List<String> tokens = extractTokens(search);

        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            if (token.equalsIgnoreCase(OP_AND) || token.equalsIgnoreCase(OP_OR)) {
                while (!stack.isEmpty() && isHigherPrecedenceOperator(token, stack.peek())) {
                    output.push(stack.pop().equalsIgnoreCase(OP_OR) ? OP_OR : OP_AND);
                }
                stack.push(token.equalsIgnoreCase(OP_OR) ? OP_OR : OP_AND);

            } else if (token.equals(OP_GROUP_START)) {
                stack.push(OP_GROUP_START);
            } else if (token.equals(OP_GROUP_END)) {
                while (stack.size() != 0 && !stack.peek().equals(OP_GROUP_START)) {
                    output.push(stack.pop());
                }
                if (stack.size() == 0) {
                    throw new SearchParserException("invalid search " + search + " parentheses not balanced");
                }
                stack.pop();
            } else if (COMPLEX_OPERATORS.contains(token) || BASIC_OPERATORS.contains(token)) {
                throw new SearchParserException("invalid search " + search + " " + token + " is not in the right place");
            } else {
                if (i + 2 >= tokens.size()) {
                    throw new SearchParserException("invalid search " + search);
                }
                String operator = tokens.get(i + 1);
                if (!BASIC_OPERATORS.contains(operator)) {
                    throw new SearchParserException("invalid search " + search + " operator" + operator + " not found");
                }
                output.push(new BasicOperation(token, operator, tokens.get(i + 2)));
                i = i + 2;
            }
        }

        while (!stack.isEmpty()) {
            String element = stack.pop();
            if (element.equals(OP_GROUP_START)) {
                throw new SearchParserException("invalid search " + search + " parentheses not balanced");
            }
            output.push(element);
        }

        if (!output.stream().allMatch(object -> (object instanceof BasicOperation) || object.equals(OP_AND) || (object.equals(OP_OR)))) {
            throw new SearchParserException("invalid search " + search);
        }

        return output;
    }

    private List<String> extractTokens(String search) {
        List<String> tokens = new ArrayList<>();
        search = search.strip().replaceAll("\\s+", " ");
        Matcher m = Pattern.compile("([^']\\S*|'.+?')\\s*").matcher(search);
        while (m.find()) {
            String split = m.group(1).replace("'", "");
            while (split.startsWith(OP_GROUP_START)) {
                tokens.add(OP_GROUP_START);
                split = split.substring(1);
            }
            List<String> endTokens = new ArrayList<>();
            while (split.endsWith(OP_GROUP_END)) {
                endTokens.add(OP_GROUP_END);
                split = split.substring(0, split.length() - 1);
            }
            if (!split.isEmpty()) {
                tokens.add(split);
            }
            tokens.addAll(endTokens);
        }
        return tokens;
    }

    private boolean isHigherPrecedenceOperator(String currOp, String prevOp) {
        if (prevOp.equalsIgnoreCase("or") && currOp.equalsIgnoreCase("or")) return true;
        if (prevOp.equalsIgnoreCase("and") && currOp.equalsIgnoreCase("op")) return true;
        if (prevOp.equalsIgnoreCase("and") && currOp.equalsIgnoreCase("and")) return true;
        return false;
    }
}
