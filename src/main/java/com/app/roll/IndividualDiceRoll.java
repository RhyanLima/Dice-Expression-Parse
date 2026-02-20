package com.app.roll;

import java.util.Objects;

public final class IndividualDiceRoll {

    private final int sides;
    private final int value;
    private final boolean wasExploded;
    private final boolean wasKept;
    private final boolean wasPenetrating;
    private final Integer explosionIteration;

    private IndividualDiceRoll(
            int sides,
            int value,
            boolean wasExploded,
            boolean wasKept,
            boolean wasPenetrating,
            Integer explosionIteration
    ) {
        this.sides = validateSides(sides);
        this.value = validateValue(value);
        this.wasExploded = wasExploded;
        this.wasKept = wasKept;
        this.wasPenetrating = wasPenetrating;
        this.explosionIteration = explosionIteration;
    }


    /**
     * Cria uma rolagem normal sem explosão
     */
    public static IndividualDiceRoll normal(int sides, int value) {
        return new IndividualDiceRoll(sides, value, false, true, false, null);
    }

    /**
     * Cria uma rolagem que explodiu
     */
    public static IndividualDiceRoll exploded(
        int sides,
        int value,
        int iteration,
        boolean penetrating
    ) {
        return new IndividualDiceRoll(sides, value, true, true, penetrating, iteration);
    }

    /**
     * Marca se esta rolagem foi mantida após aplicação de modificadores (kh/kl)
     */
    public IndividualDiceRoll withKeptStatus(boolean kept) {
        return new IndividualDiceRoll(
            this.sides,
            this.value,
            this.wasExploded,
            kept,
            this.wasPenetrating,
            this.explosionIteration
        );
    }


    private int validateSides(int sides) {
        if (sides <= 0) {
            throw new IllegalArgumentException(
                    "Dice sides must be positive, got: " + sides
            );
        }
        return sides;
    }

    private int validateValue(int value) {
        if (value <= 0) {
            throw new IllegalArgumentException(
                    "Dice value must be positive, got: " + value
            );
        }
        return value;
    }

    // Getters
    public int sides() { return sides; }
    public int value() { return value; }
    public boolean wasExploded() { return wasExploded; }
    public boolean wasKept() { return wasKept; }
    public boolean wasPenetrating() { return wasPenetrating; }
    public Integer explosionIteration() { return explosionIteration; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndividualDiceRoll that = (IndividualDiceRoll) o;
        return sides == that.sides &&
                value == that.value &&
                wasExploded == that.wasExploded &&
                wasKept == that.wasKept &&
                wasPenetrating == that.wasPenetrating &&
                Objects.equals(explosionIteration, that.explosionIteration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sides, value, wasExploded, wasKept, wasPenetrating, explosionIteration);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("d").append(sides).append("=").append(value);
        
        if (wasExploded) {
            sb.append(" (exploded");
            if (explosionIteration != null) {
                sb.append(" #").append(explosionIteration);
            }
            if (wasPenetrating) {
                sb.append(", penetrating");
            }
            sb.append(")");
        }
        
        if (!wasKept) {
            sb.append(" [discarded]");
        }
        
        return sb.toString();
    }

}
