package com.app.parser;

/**
 * Representa uma condição relacional para explosão.
 * Exemplo: >5, <=3, !=2
 */
public class ExplosionCondition {


    private final ConditionOperator operator;
    private final int value;

    public ExplosionCondition(ConditionOperator operator, int value) {
        this.operator = operator;
        this.value = value;
    }

    public ConditionOperator operator() {
        return operator;
    }

    public int value() {
        return value;
    }

    /**
     * Avalia se a condição é satisfeita.
     */
    public boolean test(int roll) {

        return switch (operator) {
            case GREATER -> roll > value;
            case GREATER_EQUAL -> roll >= value;
            case LESS -> roll < value;
            case LESS_EQUAL -> roll <= value;
            case EQUAL -> roll == value;
            case NOT_EQUAL -> roll != value;
        };
    }


}
