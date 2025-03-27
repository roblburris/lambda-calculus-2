package org.roblburris.lambda.calculus;

import com.google.common.collect.ImmutableList;
import org.roblburris.lambda.calculus.ast.*;
import org.roblburris.lambda.calculus.util.Pair;
import org.roblburris.lambda.calculus.util.Utils;

import java.util.List;
import java.util.function.Function;


public final class Evaluation {
    private final static String LAMBDA = "λ";

    private Evaluation() {}

    public static Term evaluate(Term term) {
        return switch(term) {
            case Application(Abstraction abs, Abstraction v2) -> betaReduce(v2, abs.m());
            case Application(Abstraction v1, Term t2) -> new Application(v1, evaluate(t2));
            case Application(Term t1, Term t2) -> new Application(evaluate(t1), t2);
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
            List<String> newVars = Utils.prepend(ctx.vars(), x);
            return new Pair<>(new Context(newVars), x);
        }
    }
}