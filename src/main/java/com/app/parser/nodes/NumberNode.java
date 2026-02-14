package com.app.parser.nodes;

/** 
 * Representa um número literal. 
 */
public final class NumberNode implements Node {

    private final int value;

    public NumberNode(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }

}
