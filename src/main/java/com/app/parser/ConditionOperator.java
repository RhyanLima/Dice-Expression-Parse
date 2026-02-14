package com.app.parser;

/**
 * Operadores relacionais possíveis para explosão condicional.
 */
public enum ConditionOperator {

    GREATER(">"),
    GREATER_EQUAL(">="),
    LESS("<"),
    LESS_EQUAL("<="),
    EQUAL("="),
    NOT_EQUAL("!=");

    private final String symbol;

    ConditionOperator(String symbol) {
        this.symbol = symbol;
    }

    public String symbol() {
        return symbol;
    }

}
