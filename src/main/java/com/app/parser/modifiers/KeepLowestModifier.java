package com.app.parser.modifiers;

/**
 * Mantém apenas os X menores valores.
 */
public final class KeepLowestModifier implements DiceModifier {

    private final int count;

    public KeepLowestModifier(int count) {
        this.count = count;
    }

    public int count() {
        return count;
    }

}
