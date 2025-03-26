package org.roblburris.lambda.calculus.ast;

// de Bruijn index
public record Variable(int index, int contextLength) implements Term {}