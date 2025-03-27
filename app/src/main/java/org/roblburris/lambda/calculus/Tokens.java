package org.roblburris.lambda.calculus;

import java.util.Optional;

public final class Tokens {
    public sealed interface Token permits Identifier, Lambda, Dot, LParen, RParen {}

    public record Identifier(String id) implements Token {}
    public record Lambda() implements Token {}
    public record Dot() implements Token {}
    public record LParen() implements Token {}
    public record RParen() implements Token {}

    public static boolean isDot(Token t) {
        return switch (t) {
            case Dot _$ -> true;
            default -> false;
        };
    }

    public static boolean isRightParen(Token t) {
        return switch (t) {
            case RParen _$ -> true;
            default -> false;
        };
    }

    public static Optional<Identifier> getMaybeIdentifier(Token t) {
        return switch (t) {
            case Identifier id -> Optional.of(id);
            default -> Optional.empty();
        };
    }
}
