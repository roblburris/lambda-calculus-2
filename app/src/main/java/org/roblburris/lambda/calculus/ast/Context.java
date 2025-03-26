package org.roblburris.lambda.calculus.ast;

import java.util.List;

// TODO(roblburris): add bindings for types
public record Context(List<String> vars) {}