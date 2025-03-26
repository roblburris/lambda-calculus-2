package org.roblburris.lambda.calculus.ast;

import org.roblburris.lambda.calculus.ast.Term;

// m n, i.e. m is applied to n
public record Application(Term m, Term n) implements Term {}