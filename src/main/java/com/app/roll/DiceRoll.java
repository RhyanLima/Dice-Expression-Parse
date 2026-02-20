package com.app.roll;

import java.util.random.RandomGenerator;
import java.util.random.RandomGeneratorFactory;

/**
 * Value Object responsável pela mecânica de rolagem de dados.
 */
public final class DiceRoll {

    private final RandomGenerator rng;
    
    /** Construtor permite injetar RNG (permite seed determinística). */
    public DiceRoll(RandomGenerator rng) {
        this.rng = rng;
    }

    
    /** Factory padrão usando L64X128MixRandom. */
    public static DiceRoll defaultRNG() {
        return new DiceRoll(
            RandomGeneratorFactory.of("L64X128MixRandom").create()
        );
    }

    /** Rola um único dado com N lados. (dM) */
    public int dM(int sides) {
        if (sides <= 0) {
            throw new IllegalArgumentException("A quantidade de lados precisa ser > 0");
        }
        return rng.nextInt(1, sides + 1);
    }

    /** Rola múltiplos dados. (NdM) */
    public int ndM(int quantity, int sides) {
        if (quantity <= 0 || sides <= 0) {
            throw new IllegalArgumentException("A quantidade de dados e lados precisa ser > 0");
        }

        int total = 0;
        for (int i = 0; i < quantity; i++) {
            total += dM(sides);
        }
        return total;
    }
}
