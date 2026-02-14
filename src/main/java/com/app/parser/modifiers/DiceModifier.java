package com.app.parser.modifiers;

/**
 * Representa qualquer modificador de dado.
 */
public sealed interface DiceModifier permits KeepHighestModifier, KeepLowestModifier, ExplosionModifier {

}
