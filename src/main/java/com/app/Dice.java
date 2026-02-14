package com.app;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Value Object responsável pela mecânica de rolagem de dados.
 */
public final class Dice {

    private final RandomGenerator rng;
    
    /** Construtor permite injetar RNG (permite seed determinística). */
    public Dice(RandomGenerator rng) {
        this.rng = rng;
    }

    
    /** Factory padrão usando L64X128MixRandom. */
    public static Dice defaultDice() {
        return new Dice(
            RandomGeneratorFactory.of("L64X128MixRandom").create()
        );
    }

    /** Rola um único dado com N lados. (dM) */
    public int roll(int sides) {
        if (sides <= 0) {
            throw new IllegalArgumentException("A quantidade de lados precisa ser > 0");
        }
        return rng.nextInt(1, sides + 1);
    }

    /** Rola múltiplos dados. (NdM) */
    public int roll(int quantity, int sides) {
        if (quantity <= 0 || sides <= 0) {
            throw new IllegalArgumentException("A quantidade de dados e lados precisa ser > 0");
        }

        int total = 0;
        for (int i = 0; i < quantity; i++) {
            total += roll(sides);
        }
        return total;
    }
}
