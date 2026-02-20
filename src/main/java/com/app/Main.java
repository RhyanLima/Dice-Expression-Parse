package com.app;

import java.util.List;

import com.app.parser.DiceEvaluator;
import com.app.parser.DiceParser;
import com.app.parser.Lexer;
import com.app.parser.Token;
import com.app.parser.nodes.Node;

public class Main {
    public static void main(String[] args) {
        // Teste Simples
        Roll roll = Roll.defaultRNG();
        DiceEvaluator evaluator = new DiceEvaluator(roll);
        Lexer lexer = new Lexer("d%");
        List<Token> tokens = lexer.tokenize();
        DiceParser parser = new DiceParser(tokens);
        Node ast = parser.parse();
        int result = evaluator.evaluate(ast);
        System.out.println(result);
    }
}