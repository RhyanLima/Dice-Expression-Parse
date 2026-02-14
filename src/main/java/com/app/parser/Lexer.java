package com.app.parser;

import java.util.ArrayList;
import java.util.List;

/**
 * Responsável por transformar a string da expressão
 * em uma lista de tokens.
 */
public class Lexer {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private int current = 0;

    public Lexer(String source) {
        this.source = source;
    }


    /**
     * Método principal.
     * Retorna todos os tokens encontrados.
     */
    public List<Token> tokenize() {

        while (!isAtEnd()) {
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    /**
     * Analisa o caractere atual e decide qual token criar.
     */
    private void scanToken() {

        char c = advance();

        switch (c) {

            // Espaços (ignorados)
            case ' ', '\r', '\t', '\n' -> { }

            // Operadores Matemáticos
            case '+' -> addToken(TokenType.PLUS);
            case '-' -> addToken(TokenType.MINUS);
            case '*' -> addToken(TokenType.MULTIPLY);
            case '/' -> addToken(TokenType.DIVIDE);

            // Parênteses
            case '(' -> addToken(TokenType.LEFT_PAREN);
            case ')' -> addToken(TokenType.RIGHT_PAREN);

            // Dice
            case 'd', 'D' -> addToken(TokenType.D);
            case '%' -> addToken(TokenType.PERCENT);

            // Keep (kh, kl)
            case 'k' -> scanKeep();

            // Explosões
            case '!' -> {
                if (match('!')) {
                    addToken(TokenType.DOUBLE_EXCLAMATION);
                } else if (match('=')) {
                    addToken(TokenType.NOT_EQUAL);
                } else {
                    addToken(TokenType.EXCLAMATION);
                }
            }

            case 'p' -> addToken(TokenType.P);

            // Operadores Relacionais
            case '>' -> {
                if (match('=')) {
                    addToken(TokenType.GREATER_EQUAL);
                } else {
                    addToken(TokenType.GREATER);
                }
            }

            case '<' -> {
                if (match('=')) {
                    addToken(TokenType.LESS_EQUAL);
                } else {
                    addToken(TokenType.LESS);
                }
            }

            case '=' -> addToken(TokenType.EQUAL);

            // Números
            default -> {
                if (isDigit(c)) {
                    number();
                } else {
                    throw new RuntimeException("Caractere inválido: " + c);
                }
            }
        }
    }

    // NÚMEROS
    private void number() {

        int start = current - 1;

        while (!isAtEnd() && isDigit(peek())) {
            advance();
        }

        String value = source.substring(start, current);
        tokens.add(new Token(TokenType.NUMBER, value));
    }

    // KEEP (kh / kl)
    private void scanKeep() {

        if (match('h')) {
            addToken(TokenType.KH);
        } else if (match('l')) {
            addToken(TokenType.KL);
        } else {
            throw new RuntimeException("Token inválido após 'k'");
        }
    }

    // UTILITÁRIOS
    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private boolean match(char expected) {

        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(type, ""));
    }

}
