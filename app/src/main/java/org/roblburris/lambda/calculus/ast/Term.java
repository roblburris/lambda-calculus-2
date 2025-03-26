package org.roblburris.lambda.calculus.ast;


import java.util.Optional;

/**
 * Term := Variable | Abstraction | Application
 */
public sealed interface Term permits Abstraction, Application, Variable {
    static boolean isValue(Term term) {
        return switch (term) {
            case Abstraction _ -> true;
            default -> false;
        };
    }

    static Optional<Abstraction> getMaybeAbstraction(Term term) {
        return switch (term) {
            case Abstraction abs -> Optional.of(abs);
            default -> Optional.empty();
        };
    }
}