package com.app.parser;

/** Enumerador para os deferentes tipos tokens */
public enum TokenType {

    NUMBER,          // 123

    PLUS,            // +
    MINUS,           // -
    MULTIPLY,        // *
    DIVIDE,          // /

    LEFT_PAREN,      // (
    RIGHT_PAREN,     // )

    D,               // d
    PERCENT,         // %

    KH,              // kh
    KL,              // kl

    EXCLAMATION,         // !
    DOUBLE_EXCLAMATION,  // !!

    P,               // p (penetrating)

    GREATER,             // >
    GREATER_EQUAL,       // >=
    LESS,                // <
    LESS_EQUAL,          // <=
    EQUAL,               // =
    NOT_EQUAL,           // !=

    EOF // Fim de Arquivo
}
