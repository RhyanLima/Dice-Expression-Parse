package com.app.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;

import com.app.parser.modifiers.DiceModifier;
import com.app.parser.modifiers.ExplosionModifier;
import com.app.parser.modifiers.KeepHighestModifier;
import com.app.parser.modifiers.KeepLowestModifier;
import com.app.parser.nodes.BinaryOperationNode;
import com.app.parser.nodes.DiceNode;
import com.app.parser.nodes.Node;
import com.app.parser.nodes.NumberNode;
import com.app.roll.DiceRoll;
import com.app.roll.DiceRollResult;
import com.app.roll.DiceRollResultBuilder;
import com.app.roll.IndividualDiceRoll;


/**
 * Responsável por executar a AST.
 */
public class DiceEvaluator {

    private final DiceRoll roll;
    private DiceRollResultBuilder resultBuilder;

    public DiceEvaluator(DiceRoll roll) {
        this.roll = roll;
    }

    /**
     * Avalia a expressão completa e retorna resultado detalhado
     */
    public DiceRollResult evaluateWithDetails(String expression, Node node) {
        this.resultBuilder = DiceRollResultBuilder.create()
                .withExpression(expression);
        
        int total = evaluate(node);
        
        return resultBuilder
                .withFinalTotal(total)
                .build();
    }

    /**
     * Retorna apenas o total
     */
    public int evaluate(Node node) {
        return evaluateNode(node);
    }

    private int evaluateNode(Node node) {

        if (node instanceof NumberNode number) {
            return number.value();
        }

        if (node instanceof BinaryOperationNode bin) {
            int left = evaluateNode(bin.left());
            int right = evaluateNode(bin.right());
            return applyOperation(left, bin, right);
        }

        if (node instanceof DiceNode diceNode) {
            return evaluateDice(diceNode);
        }

        throw new IllegalStateException("Unknown node type: " + node.getClass().getName());
    }

    /**
     * Executa a rolagem completa com modificadores e registro detalhado
     */
    private int evaluateDice(DiceNode node) {

        List<DiceRollEntry> rollEntries = new ArrayList<>();

        // Rola todos os dados
        for (int i = 0; i < node.quantity(); i++) {
            DiceRollEntry entry = rollWithExplosion(
                    node.sides(), 
                    node.modifiers()
            );
            rollEntries.add(entry);
        }

        // Aplica modificadores de keep
        rollEntries = applyKeepModifiers(rollEntries, node.modifiers());

        // Registra todas as rolagens
        if (resultBuilder != null) {
            rollEntries.forEach(entry -> 
                resultBuilder.addRolls(entry.rolls())
            );
            
            // Registra modificadores aplicados
            node.modifiers().forEach(mod -> 
                resultBuilder.addModifier(formatModifier(mod))
            );
        }

        // Calcula total
        return rollEntries.stream()
                .filter(DiceRollEntry::kept)
                .mapToInt(DiceRollEntry::total)
                .sum();
    }

    /**
     * Rola um dado com possíveis explosões
     */
    private DiceRollEntry rollWithExplosion(
            int sides, 
            List<DiceModifier> modifiers
    ) {
        List<IndividualDiceRoll> rolls = new ArrayList<>();
        
        // Primeira rolagem
        int value = roll.dM(sides);
        rolls.add(IndividualDiceRoll.normal(sides, value));
        
        int total = value;
        int lastRoll = value;

        // Verifica explosões
        for (DiceModifier mod : modifiers) {
            if (mod instanceof ExplosionModifier exp) {
                
                int iteration = 1;
                
                while (shouldExplode(lastRoll, sides, exp)) {
                    
                    if (exp.limit() != null && iteration > exp.limit()) {
                        break;
                    }

                    lastRoll = roll.dM(sides);
                    
                    // Aplica penalidade penetrante
                    if (exp.penetrating() && exp.penalty() != null) {
                        lastRoll = Math.max(1, lastRoll - exp.penalty());
                    }

                    rolls.add(IndividualDiceRoll.exploded(
                            sides, 
                            lastRoll, 
                            iteration,
                            exp.penetrating()
                    ));

                    if (!exp.compound()) {
                        total += lastRoll;
                    } else {
                        total += lastRoll;
                    }

                    iteration++;
                }
            }
        }

        return new DiceRollEntry(rolls, total, true);
    }

    private boolean shouldExplode(int roll, int sides, ExplosionModifier mod) {
        
        if (mod.condition() != null) {
            return mod.condition().test(roll);
        }

        // Padrão: explode no máximo
        return roll == sides;
    }

    /**
     * Aplica modificadores de keep highest/lowest
     */
    private List<DiceRollEntry> applyKeepModifiers(
            List<DiceRollEntry> entries,
            List<DiceModifier> modifiers
    ) {

        for (DiceModifier mod : modifiers) {

            if (mod instanceof KeepHighestModifier kh) {
                return applyKeepHighest(entries, kh.count());
            }

            if (mod instanceof KeepLowestModifier kl) {
                return applyKeepLowest(entries, kl.count());
            }
        }

        return entries;
    }

    private List<DiceRollEntry> applyKeepHighest(
            List<DiceRollEntry> entries, 
            int count
    ) {
        List<DiceRollEntry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparingInt(DiceRollEntry::total).reversed());

        return IntStream.range(0, sorted.size())
                .mapToObj(i -> sorted.get(i).withKept(i < count))
                .toList();
    }

    private List<DiceRollEntry> applyKeepLowest(
            List<DiceRollEntry> entries, 
            int count
    ) {
        List<DiceRollEntry> sorted = new ArrayList<>(entries);
        sorted.sort(Comparator.comparingInt(DiceRollEntry::total));

        return IntStream.range(0, sorted.size())
                .mapToObj(i -> sorted.get(i).withKept(i < count))
                .toList();
    }

    private int applyOperation(int left, BinaryOperationNode bin, int right) {

        return switch (bin.operator()) {
            case PLUS -> left + right;
            case MINUS -> left - right;
            case MULTIPLY -> left * right;
            case DIVIDE -> {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield left / right;
            }
            default -> throw new IllegalArgumentException(
                    "Invalid operator: " + bin.operator()
            );
        };
    }

    private String formatModifier(DiceModifier modifier) {
        if (modifier instanceof KeepHighestModifier kh) {
            return "kh" + kh.count();
        }
        if (modifier instanceof KeepLowestModifier kl) {
            return "kl" + kl.count();
        }
        if (modifier instanceof ExplosionModifier exp) {
            StringBuilder sb = new StringBuilder();
            if (exp.compound()) sb.append("!!");
            else sb.append("!");
            
            if (exp.penetrating()) sb.append("p");
            if (exp.limit() != null) sb.append(exp.limit());
            if (exp.penalty() != null) sb.append("(penalty:").append(exp.penalty()).append(")");
            
            return sb.toString();
        }
        return modifier.getClass().getSimpleName();
    }

    /**
     * Record interno para agrupar resultados durante processamento
     */
    private record DiceRollEntry(
            List<IndividualDiceRoll> rolls,
            int total,
            boolean kept
    ) {
        DiceRollEntry withKept(boolean newKept) {
            List<IndividualDiceRoll> updatedRolls = rolls.stream()
                .map(roll -> roll.withKeptStatus(newKept))
                .toList();
            return new DiceRollEntry(updatedRolls, total, newKept);
        }
    }
}
