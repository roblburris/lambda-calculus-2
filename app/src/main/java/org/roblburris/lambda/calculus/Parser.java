package org.roblburris.lambda.calculus;

import org.roblburris.lambda.calculus.ast.*;
import org.roblburris.lambda.calculus.util.Pair;
import org.roblburris.lambda.calculus.util.Utils;

import java.util.List;
import java.util.Optional;

public final class Parser {
    sealed interface LambdaExp permits Var, FuncAbstraction, Appl {}

    record Var(String id) implements LambdaExp {}
    record FuncAbstraction(Var v, LambdaExp exp) implements LambdaExp {}
    record Appl(LambdaExp exp1, LambdaExp exp2) implements LambdaExp {}


    public static Term parse(List<Tokens.Token> tokens) {
        // Parse to an intermediate AST.
        var lambdaExp = toLambdaExp(tokens, 0);

        // Convert intermediate AST to final/evaluation tree with de-Bruijn indices.
        return toTerm(lambdaExp.l(), new Context(List.of()));
    }

    /**
     * Exp ::= Exp Exp
     * Exp ::= \lambda Var . Exp
     * Exp ::= ( Exp )
     *
     */
    private static Pair<LambdaExp, Integer> toLambdaExp(List<Tokens.Token> tokens, int i) {
        var token = getMaybeToken(tokens, i).orElseThrow();
        return switch (token) {
            case Tokens.LParen _ -> {
                var exp1 = toLambdaExp(tokens, i + 1);
                if (Tokens.isRightParen(getMaybeToken(tokens, exp1.r() + 1).orElseThrow())) {
                    if (exp1.r() + 2 == tokens.size()) {
                        yield new Pair<>(exp1.l(), exp1.r() + 2);
                    }
                    var exp2 = toLambdaExp(tokens, exp1.r() + 2);
                    yield new Pair<>(new Appl(exp1.l(), exp2.l()), exp2.r() + 1);
                }
                var exp2 = toLambdaExp(tokens, exp1.r() + 1);
                yield new Pair<>(new Appl(exp1.l(), exp2.l()), exp2.r() + 1);
            }
            case Tokens.Lambda _ -> {
                var variableCandidate = Tokens.getMaybeIdentifier(getMaybeToken(tokens, i + 1).orElseThrow());
                if (variableCandidate.isEmpty()) {
                    System.err.println("Expected IDENTIFER after LAMBDA.");
                    System.exit(1);
                }

                if (!Tokens.isDot(getMaybeToken(tokens, i + 2).orElseThrow())) {
                    System.err.println("Expected DOT after LAMBDA IDENTIFER.");
                    System.exit(1);
                }
                var lambdaExp = toLambdaExp(tokens, i + 3);
                yield new Pair<>(new FuncAbstraction(new Var(variableCandidate.get().id()), lambdaExp.l()), lambdaExp.r());
            }
            case Tokens.Identifier id -> new Pair<>(new Var(id.id()), i);
            default -> throw new IllegalStateException(STR."Unexpected value: \{token}");
        };
    }

    private static Term toTerm(LambdaExp lambdaExp, Context context) {
        return switch (lambdaExp) {
            case Appl appl -> new Application(toTerm(appl.exp1(), context), toTerm(appl.exp2(), context));
            case Var variable -> {
                int index = context.vars().indexOf(variable.id());
                if (index == -1) {
                    System.err.println(STR."Unbound variable: \{variable.id()}");
                    System.exit(1);
                }
                yield new Variable(index, context.vars().size());
            }
            case FuncAbstraction abs -> {
                Context newContext = new Context( Utils.prepend(context.vars(), abs.v.id()));
                yield new Abstraction(abs.v.id(), toTerm(abs.exp(), newContext));
            }
        };
    }

    private static Optional<Tokens.Token> getMaybeToken(List<Tokens.Token> tokens, int i) {
        if (i >= tokens.size()) return Optional.empty();
        return Optional.of( tokens.get(i));
    }
}
