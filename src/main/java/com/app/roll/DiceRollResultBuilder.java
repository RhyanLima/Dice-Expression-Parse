package com.app.roll;

import java.util.ArrayList;
import java.util.List;

public final class DiceRollResultBuilder {

    private String expression;
    private final List<IndividualDiceRoll> rolls;
    private final List<String> modifiers;
    private Integer finalTotal;


    private DiceRollResultBuilder() {
        this.rolls = new ArrayList<>();
        this.modifiers = new ArrayList<>();
    }

    public static DiceRollResultBuilder create() {
        return new DiceRollResultBuilder();
    }

    public DiceRollResultBuilder withExpression(String expression) {
        this.expression = expression;
        return this;
    }

    public DiceRollResultBuilder addRoll(IndividualDiceRoll roll) {
        this.rolls.add(roll);
        return this;
    }

    public DiceRollResultBuilder addRolls(List<IndividualDiceRoll> rolls) {
        this.rolls.addAll(rolls);
        return this;
    }

    public DiceRollResultBuilder addModifier(String modifier) {
        this.modifiers.add(modifier);
        return this;
    }

    public DiceRollResultBuilder withFinalTotal(int total) {
        this.finalTotal = total;
        return this;
    }

    /**
     * Marca quais rolagens foram mantidas após aplicação de modificadores
     */
    public DiceRollResultBuilder markKeptRolls(List<Integer> keptIndices) {
        for (int i = 0; i < rolls.size(); i++) {
            boolean shouldKeep = keptIndices.contains(i);
            rolls.set(i, rolls.get(i).withKeptStatus(shouldKeep));
        }
        return this;
    }

    public DiceRollResult build() {
        validateState();
        
        String modifiersString = modifiers.isEmpty() 
            ? "none" 
            : String.join(", ", modifiers);

        return DiceRollResult.create(
            expression,
            new ArrayList<>(rolls),
            finalTotal,
            modifiersString
        );
    }

    private void validateState() {
        if (expression == null || expression.isBlank()) {
            throw new IllegalStateException("Expression must be set before building");
        }
        if (finalTotal == null) {
            throw new IllegalStateException("Final total must be set before building");
        }
        if (rolls.isEmpty()) {
            throw new IllegalStateException("At least one roll must be added");
        }
    }

}
