package com.app.parser.nodes;

/**
 * Interface base da AST.
 * Representa qualquer nó da expressão.
 */
public sealed interface Node permits NumberNode, DiceNode, BinaryOperationNode {

}
