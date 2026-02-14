package com.app.parser.modifiers;

/**
 * Mantém apenas os X maiores valores.
 */
public final class KeepHighestModifier implements DiceModifier {

    private final int count;

    public KeepHighestModifier(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }

}
