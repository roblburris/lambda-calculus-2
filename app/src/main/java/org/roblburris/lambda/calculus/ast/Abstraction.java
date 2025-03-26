package org.roblburris.lambda.calculus.ast;

import org.roblburris.lambda.calculus.ast.Term;

// \lambda m
public record Abstraction(String name, Term m) implements Term {}