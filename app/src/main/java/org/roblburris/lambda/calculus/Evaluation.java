package org.roblburris.lambda.calculus;

import com.google.common.collect.ImmutableList;
import org.roblburris.lambda.calculus.ast.*;

import java.util.List;


public final class Evaluation {
    private final static String LAMBDA = "λ";

    private Evaluation() {}

    public static Term evaluate(Term term) {
        return switch (term) {
            case Application app -> switch (app.n()) {
                case Abstraction v2 -> switch (app.m()) {
                    case Abstraction abs -> betaReduce(v2, abs.m());
                    default -> app;
                };
                default -> switch (app.m()) {
                    case Abstraction v1 -> new Application(v1, evaluate(app.n()));
                    default -> new Application(evaluate(app.m()), app.n());
                };
            };
            default -> term;
        };
    }

    static Term shift(Term t, int dPlace, int cutoff) {
        return switch (t) {
            case Variable variable -> variable.index() < cutoff ? variable
                    : new Variable(variable.index() + dPlace, variable.contextLength());
            case Abstraction abs -> new Abstraction(abs.name(), shift(abs.m(),
                    dPlace, cutoff + 1));
            case Application apply -> new Application(shift(apply.m(), dPlace, cutoff),
                    shift(apply.n(), dPlace, cutoff));
        };
    }

    /**
     * [j -> s]t
     */
    static Term substitution(int j, Term s, Term t) {
        return switch (t) {
            case Variable variable -> variable.index() == j ? s
                    : t;
            case Abstraction abs -> new Abstraction(
                    abs.name(), substitution(j + 1, shift(s, 1, 0), abs.m()));
            case Application apply -> new Application(substitution(j, s, apply.m()), substitution(j, s, apply.n()));
        };
    }

    static Term betaReduce(Abstraction abs, Term v) {
        return shift(substitution(0, v, abs), -1, 0);
    }


    static void print(Context ctx, Term term) {
        switch (term) {
            case Abstraction abs -> printAbstraction(ctx, abs);
            case Application app -> printApplication(ctx, app);
            case Variable v -> printVariable(ctx, v);
        }
    }

    private record Pair<L, R>(L l, R r) {
    }

    private static void printAbstraction(Context ctx, Abstraction abs) {
        Pair<Context, String> freshName = pickFreshName(ctx,
                abs.name());
        System.out.print(STR."(\{LAMBDA + freshName.r()}.");
        print(freshName.l(), abs.m());
    }

    private static void printApplication(Context ctx, Application app) {
        System.out.print("(");
        print(ctx, app.m());
        System.out.print(" ");
        print(ctx, app.n());
        System.out.print(")");
    }

    private static void printVariable(Context ctx, Variable v) {
        if (ctx.vars().size() == v.contextLength()) {
            System.out.print(STR."\{ctx.vars().get(v.index())}");
        } else {
            System.out.print("[BAD INDEX]");
        }
    }

    private static Pair<Context, String> pickFreshName(Context ctx, String x) {
        boolean hasName = ctx.vars().stream().anyMatch(x::equals);
        if (hasName) {
            return pickFreshName(ctx, STR."\{x}'");
        } else {
            List<String> newVars = prepend(ctx.vars(), x);
            return new Pair<>(new Context(newVars), x);
        }
    }

    private static <T> List<T> prepend(List<T> l, T t) {
        return ImmutableList.<T>builder()
                .add(t)
                .addAll(l)
                .build();
    }
}