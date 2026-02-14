package com.app.parser.nodes;

import java.util.List;

import com.app.parser.modifiers.DiceModifier;


/**
 * Representa uma rolagem de dados com possíveis modificadores. 
 * <br>
 * Exemplo:
 * 4d6kh3!!
 */
public final class DiceNode implements Node {

    private final int quantity;
    private final int sides;
    private final List<DiceModifier> modifiers;

    public DiceNode(int quantity, int sides, List<DiceModifier> modifiers) {
        this.quantity = quantity;
        this.sides = sides;
        this.modifiers = modifiers;
    }

    public int quantity() { return quantity; }
    public int sides() { return sides; }
    public List<DiceModifier> modifiers() { return modifiers; }

}
