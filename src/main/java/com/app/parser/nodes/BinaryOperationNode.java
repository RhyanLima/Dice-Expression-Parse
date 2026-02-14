package com.app.parser.nodes;

import com.app.parser.TokenType;

public final class BinaryOperationNode implements Node {

    private final Node left;
    private final Node right;
    private final TokenType operator;

    public BinaryOperationNode(Node left, TokenType operator, Node right) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    public Node left() { return left; }
    public Node right() { return right; }
    public TokenType operator() { return operator; }

}
