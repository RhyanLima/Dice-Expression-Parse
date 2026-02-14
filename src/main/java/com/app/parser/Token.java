package com.app.parser;

/**
 * Representa um token gerado pelo Lexer. <br>
 * <br>
 * type   → tipo do token <br>
 * lexeme → texto original da expressão
 */
public class Token {


    private final TokenType type;
    private final String lexeme;

    public Token(TokenType type, String lexeme) {
        this.type = type;
        this.lexeme = lexeme;
    }

    public TokenType type() {
        return type;
    }

    public String lexeme() {
        return lexeme;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                '}';
    }

}
