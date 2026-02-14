package com.app.parser.modifiers;

import com.app.parser.ExplosionCondition;

/**
 * Representa todos os tipos de explosão.
 */
public final class ExplosionModifier implements DiceModifier {

    private final boolean compound;
    private final boolean penetrating;
    private final ExplosionCondition condition;
    private final Integer limit;
    private final Integer penalty;

    public ExplosionModifier(boolean compound, boolean penetrating, ExplosionCondition condition, Integer limit, Integer penalty) {
        this.compound = compound;
        this.penetrating = penetrating;
        this.condition = condition;
        this.limit = limit;
        this.penalty = penalty;
    }

    public boolean compound() { return compound; }
    public boolean penetrating() { return penetrating; }
    public ExplosionCondition condition() { return condition; }
    public Integer limit() { return limit; }
    public Integer penalty() { return penalty; }

}
