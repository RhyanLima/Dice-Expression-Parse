package com.app;

import java.util.List;

import com.app.parser.DiceEvaluator;
import com.app.parser.DiceParser;
import com.app.parser.Lexer;
import com.app.parser.Token;
import com.app.parser.nodes.Node;
import com.app.roll.DiceRoll;
import com.app.roll.DiceRollResult;

public class Main {
    public static void main(String[] args) {
        // Teste Simples
        DiceRoll roll = DiceRoll.defaultRNG();
        DiceEvaluator evaluator = new DiceEvaluator(roll);
        String expression = "2d4";
        Lexer lexer = new Lexer(expression);
        List<Token> tokens = lexer.tokenize();
        DiceParser parser = new DiceParser(tokens);
        Node ast = parser.parse();
        DiceRollResult result = evaluator.evaluateWithDetails(expression, ast);
        System.out.println(result);
    }
}