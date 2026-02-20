package com.app.roll;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class DiceRollResult {
    
    private final String rollId;
    private final String expression;
    private final List<IndividualDiceRoll> allRolls;
    private final int finalTotal;
    private final Instant timestamp;
    private final String appliedModifiers;

    private DiceRollResult(
        String rollId,
        String expression,
        List<IndividualDiceRoll> allRolls,
        int finalTotal,
        Instant timestamp,
        String appliedModifiers
    ) {
        this.rollId = Objects.requireNonNull(rollId, "Roll ID cannot be null");
        this.expression = validateExpression(expression);
        this.allRolls = Collections.unmodifiableList(new ArrayList<>(allRolls));
        this.finalTotal = finalTotal;
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.appliedModifiers = appliedModifiers != null ? appliedModifiers : "none";
    }


    /**
     * Factory method principal para criar resultado de rolagem
     */
    public static DiceRollResult create(
        String expression,
        List<IndividualDiceRoll> allRolls,
        int finalTotal,
        String appliedModifiers
    ) {
        return new DiceRollResult(
                generateRollId(),
                expression,
                allRolls,
                finalTotal,
                Instant.now(),
                appliedModifiers
        );
    }

    private static String generateRollId() {
        return "roll_" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }

    private String validateExpression(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Expression cannot be blank");
        }
        return expression.trim();
    }

    /**
     * Retorna apenas as rolagens que foram mantidas (kept)
     */
    public List<IndividualDiceRoll> keptRolls() {
        return allRolls.stream()
            .filter(IndividualDiceRoll::wasKept)
            .toList();
    }

    /**
     * Retorna apenas as rolagens que foram descartadas
     */
    public List<IndividualDiceRoll> discardedRolls() {
        return allRolls.stream()
            .filter(roll -> !roll.wasKept())
            .toList();
    }

    /**
     * Retorna os valores numéricos de todas as rolagens feitas
     */
    public List<Integer> allValues() {
        return allRolls.stream()
            .map(IndividualDiceRoll::value)
            .toList();
    }

    /**
     * Retorna os valores das rolagens mantidos
     */
    public List<Integer> keptValues() {
        return keptRolls().stream()
            .map(IndividualDiceRoll::value)
            .toList();
    }

    /**
     * Verifica se houve alguma explosão
     */
    public boolean hadExplosions() {
        return allRolls.stream().anyMatch(IndividualDiceRoll::wasExploded);
    }

    /**
     * Conta quantas explosões ocorreram
     */
    public long explosionCount() {
        return allRolls.stream()
            .filter(IndividualDiceRoll::wasExploded)
            .count();
    }

    /**
     * Retorna descrição legível
     * <br>
     * Exemplo: "5d20kh2 -> rolled [1, 2, 3, 4, 6], kept [4, 6], total: 10"
     */
    public String toHumanReadable() {
        StringBuilder sb = new StringBuilder();
        sb.append(expression).append(" -> ");
        
        sb.append("rolled ");
        sb.append(allValues());
        
        if (!keptRolls().equals(allRolls)) {
            sb.append(", kept ");
            sb.append(keptValues());
        }
        
        if (hadExplosions()) {
            sb.append(" (").append(explosionCount()).append(" explosion(s))");
        }
        
        sb.append(", total: ").append(finalTotal);
        
        return sb.toString();
    }

    /**
     * Retorna representação detalhada para logs e auditoria
     */
    public String toDetailedLog() {
        return String.format(
            "RollID[%s] Expression[%s] Timestamp[%s] Modifiers[%s]%n" +
            "All Rolls: %s%n" +
            "Kept Rolls: %s%n" +
            "Discarded: %s%n" +
            "Final Total: %d",
            rollId,
            expression,
            timestamp,
            appliedModifiers,
            formatRolls(allRolls),
            formatRolls(keptRolls()),
            formatRolls(discardedRolls()),
            finalTotal
        );
    }

    private String formatRolls(List<IndividualDiceRoll> rolls) {
        return rolls.stream()
            .map(IndividualDiceRoll::toString)
            .collect(Collectors.joining(", ", "[", "]"));
    }

    // Getters
    public String rollId() { return rollId; }
    public String expression() { return expression; }
    public List<IndividualDiceRoll> allRolls() { return allRolls; }
    public int finalTotal() { return finalTotal; }
    public Instant timestamp() { return timestamp; }
    public String appliedModifiers() { return appliedModifiers; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiceRollResult that = (DiceRollResult) o;
        return rollId.equals(that.rollId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rollId);
    }

    @Override
    public String toString() {
        return toHumanReadable();
    }
}
