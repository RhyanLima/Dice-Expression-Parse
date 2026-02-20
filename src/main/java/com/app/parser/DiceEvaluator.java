package com.app.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.app.Roll;
import com.app.parser.modifiers.DiceModifier;
import com.app.parser.modifiers.ExplosionModifier;
import com.app.parser.modifiers.KeepHighestModifier;
import com.app.parser.modifiers.KeepLowestModifier;
import com.app.parser.nodes.BinaryOperationNode;
import com.app.parser.nodes.DiceNode;
import com.app.parser.nodes.Node;
import com.app.parser.nodes.NumberNode;


/**
 * Responsável por executar a AST.
 */
public class DiceEvaluator {

    private final Roll roll;

    public DiceEvaluator(Roll roll) {
        this.roll = roll;
    }

    public int evaluate(Node node) {

        if (node instanceof NumberNode number) {
            return number.value();
        }

        if (node instanceof BinaryOperationNode bin) {
            int left = evaluate(bin.left());
            int right = evaluate(bin.right());

            return applyOperations(left, bin, right);
        }

        if (node instanceof DiceNode diceNode) {
            return evaluateDice(diceNode);
        }

        throw new IllegalStateException("node type desconhecido");
    }


    /** Executa a rolagem completa com modificadores. */
    private int evaluateDice(DiceNode node) {

        List<Integer> rolls = new ArrayList<>();

        for (int i = 0; i < node.quantity(); i++) {
            rolls.add(handleExplosion(node.sides(), node.modifiers()));
        }

        rolls = applyKeepModifiers(rolls, node.modifiers());

        return rolls.stream().mapToInt(Integer::intValue).sum();
    }


    /** Aplica explosões. */
    private int handleExplosion(int sides, List<DiceModifier> modifiers) {

        int total = 0;
        int roll = this.roll.dM(sides);
        total += roll;

        for (DiceModifier mod : modifiers) {
            if (mod instanceof ExplosionModifier exp) {

                int explosionCount = 0;

                while (shouldExplode(roll, sides, exp)) {

                    if (exp.limit() != null &&
                            explosionCount >= exp.limit()) {
                        break;
                    }

                    roll = this.roll.dM(sides);

                    if (exp.penetrating() && exp.penalty() != null) {
                        roll -= exp.penalty();
                    }

                    if (exp.compound()) {
                        total += roll;
                    } else {
                        total += roll;
                    }

                    explosionCount++;
                }
            }
        }

        return total;
    }


    private boolean shouldExplode(int roll, int sides, ExplosionModifier mod) {

        if (mod.condition() != null) {
            return mod.condition().test(roll);
        }

        // padrão explode no máximo
        return roll == sides;
    }

    /** Aplica keep highest/lowest. */
    private List<Integer> applyKeepModifiers(List<Integer> rolls, List<DiceModifier> modifiers) {

        for (DiceModifier mod : modifiers) {

            if (mod instanceof KeepHighestModifier kh) {
                return rolls.stream()
                        .sorted(Comparator.reverseOrder())
                        .limit(kh.count())
                        .toList();
            }

            if (mod instanceof KeepLowestModifier kl) {
                return rolls.stream()
                        .sorted()
                        .limit(kl.count())
                        .toList();
            }
        }

        return rolls;
    }

    /** Aplica operações */
    private int applyOperations(int left, BinaryOperationNode bin, int right) {

        if (bin.operator() == TokenType.PLUS) {
            return left + right;
        }
        if (bin.operator() == TokenType.MINUS) {
            return left - right;
        }
        if (bin.operator() == TokenType.MULTIPLY) {
            return left * right;
        }
        if (bin.operator() == TokenType.DIVIDE) {
            return left / right;
        }
        throw new IllegalArgumentException("Operação inválida!");
    }
}
