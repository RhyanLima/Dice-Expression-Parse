package com.app.parser;

import java.util.ArrayList;
import java.util.List;

import com.app.parser.modifiers.DiceModifier;
import com.app.parser.modifiers.ExplosionModifier;
import com.app.parser.modifiers.KeepHighestModifier;
import com.app.parser.modifiers.KeepLowestModifier;
import com.app.parser.nodes.BinaryOperationNode;
import com.app.parser.nodes.DiceNode;
import com.app.parser.nodes.Node;
import com.app.parser.nodes.NumberNode;


/**
 * Parser recursivo responsável por:
 * <br>
 * 1. Consumir tokens do Lexer <br>
 * 2. Construir a AST <br>
 * 3. Garantir precedência correta de operadores <br>
 */
public class DiceParser {

    private final List<Token> tokens;
    private int current = 0;

    public DiceParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Ponto de entrada do parser.
     * Retorna a raiz da AST.
     */
    public Node parse() {
        Node node = expression();
        if (!isAtEnd()) {
            throw error("Tokens inesperados após expressão.");
        }
        return node;
    }

    /**
     * expression → term ((+ | -) term)*
     * <br>
     * Trata soma e subtração.
     */
    private Node expression() {
        Node node = term();

        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            Node right = term();
            node = new BinaryOperationNode(node, operator.type(), right);
        }

        return node;
    }

    /**
     * term → factor ((* | /) factor)*
     * <br>
     * Trata multiplicação e divisão.
     */
    private Node term() {
        Node node = factor();

        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            Node right = factor();
            node = new BinaryOperationNode(node, operator.type(), right);
        }

        return node;
    }

    /**
     * factor →
     *    NUMBER
     *  | dice
     *  | '(' expression ')'
     */
    private Node factor() {

        // Caso comece com número e depois venha D → é Dice
        if (check(TokenType.NUMBER) && checkNext(TokenType.D)) {
            return parseDice();
        }

        // Caso comece diretamente com D → ex: d6
        if (match(TokenType.D)) {
            return parseDiceWithImplicitQuantity();
        }

        // Número literal
        if (match(TokenType.NUMBER)) {
            return new NumberNode(
                Integer.parseInt(previous().lexeme())
            );
        }

        // Parênteses
        if (match(TokenType.LEFT_PAREN)) {
            Node expr = expression();
            consume(TokenType.RIGHT_PAREN, "Esperado ')'");
            return expr;
        }

        throw error("Expressão inválida.");
    }

    /* 
        ========== PARSE DICE ==========
    */

    /**  Parse completo de NdM */
    private Node parseDice() {

        // Quantidade
        int quantity = Integer.parseInt(consume(TokenType.NUMBER,
                "Esperado número antes do 'd'").lexeme());

        consume(TokenType.D, "Esperado 'd'");

        int sides = parseSides();

        List<DiceModifier> modifiers = parseModifiers();

        return new DiceNode(quantity, sides, modifiers);
    }

    /** Caso seja d6 (sem quantidade) */
    private Node parseDiceWithImplicitQuantity() {

        int quantity = 1; // padrão
        int sides = parseSides();

        List<DiceModifier> modifiers = parseModifiers();

        return new DiceNode(quantity, sides, modifiers);
    }

    
    /**
     * Lados do dado: <br>
     * - número normal <br>
     * - '%' que equivale a 100
     */
    private int parseSides() {

        if (match(TokenType.PERCENT)) {
            return 100;
        }

        return Integer.parseInt(
                consume(TokenType.NUMBER,
                "Esperado número de lados").lexeme()
        );
    }


    /** Loop que coleta todos os modificadores */
    private List<DiceModifier> parseModifiers() {

        List<DiceModifier> modifiers = new ArrayList<>();

        while (true) {

            // keep highest
            if (match(TokenType.KH)) {
                int count = Integer.parseInt(
                        consume(TokenType.NUMBER,
                        "Esperado número após kh").lexeme()
                );
                modifiers.add(new KeepHighestModifier(count));
                continue;
            }

            // keep lowest
            if (match(TokenType.KL)) {
                int count = Integer.parseInt(
                        consume(TokenType.NUMBER,
                        "Esperado número após kl").lexeme()
                );
                modifiers.add(new KeepLowestModifier(count));
                continue;
            }

            // explosões
            if (match(TokenType.EXCLAMATION)) {
                modifiers.add(parseExplosion(false));
                continue;
            }

            if (match(TokenType.DOUBLE_EXCLAMATION)) {
                modifiers.add(parseExplosion(true));
                continue;
            }

            break;
        }

        return modifiers;
    }


    /**
     * Parse das explosões: <br>
     * <br>
     * !       → explode normal. <br>
     * !cX     → explode condicional. <br>
     * !!      → explode composto. <br>
     * !!X     → explode composto com limite. <br>
     * !p      → explode penetrante. <br>
     * !pX     → penetrante com penalidade
     */
    private DiceModifier parseExplosion(boolean compound) {

        boolean penetrating = false;
        ExplosionCondition condition = null;
        Integer limit = null;
        Integer penalty = null;

        // Penetrating !p
        if (match(TokenType.P)) {

            penetrating = true;

            if (match(TokenType.NUMBER)) {
                penalty = Integer.parseInt(previous().lexeme());
            }

            return new ExplosionModifier(compound, true, null, null, penalty);
        }

        // Condição relacional
        if (isRelationalOperator(peek().type())) {

            ConditionOperator operator = parseConditionOperator();
            int value = Integer.parseInt(
                    consume(TokenType.NUMBER,
                    "Esperado número após operador relacional").lexeme()
            );

            condition = new ExplosionCondition(operator, value);

            return new ExplosionModifier(compound, false, condition, null, null);
        }

        // Limite para !!X
        if (compound && match(TokenType.NUMBER)) {

            limit = Integer.parseInt(previous().lexeme());

            return new ExplosionModifier(true, false, null, limit, null);
        }

        // Explosão padrão (explode no máximo do dado)
        return new ExplosionModifier(compound, false, null, null, null);
    }

    /* 
        ========== ULTILITÁRIOS ==========
    */

    private ConditionOperator parseConditionOperator() {

        if (match(TokenType.GREATER)) return ConditionOperator.GREATER;
        if (match(TokenType.GREATER_EQUAL)) return ConditionOperator.GREATER_EQUAL;
        if (match(TokenType.LESS)) return ConditionOperator.LESS;
        if (match(TokenType.LESS_EQUAL)) return ConditionOperator.LESS_EQUAL;
        if (match(TokenType.EQUAL)) return ConditionOperator.EQUAL;
        if (match(TokenType.NOT_EQUAL)) return ConditionOperator.NOT_EQUAL;

        throw error("Operador relacional esperado.");
    }

    private boolean isRelationalOperator(TokenType type) {

        return type == TokenType.GREATER ||
            type == TokenType.GREATER_EQUAL ||
            type == TokenType.LESS ||
            type == TokenType.LESS_EQUAL ||
            type == TokenType.EQUAL ||
            type == TokenType.NOT_EQUAL;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private boolean checkNext(TokenType type) {
        if (current + 1 >= tokens.size()) return false;
        return tokens.get(current + 1).type() == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw error(message);
    }

    private RuntimeException error(String message) {
        return new RuntimeException(
                message + " no token: " + peek().lexeme()
        );
    }

}
