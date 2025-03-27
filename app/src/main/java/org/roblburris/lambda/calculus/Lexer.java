package org.roblburris.lambda.calculus;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final static String LAMBDA = "λ";
    private final static String LPAREN = "(";
    private final static String RPAREN = ")";
    private final static String DOT = ".";

    public static List<Tokens.Token> tokenizer(String input) {
        var initialTokens = input.split("\\p{Zs}+");
        List<Tokens.Token> result = new ArrayList<>();

        for (String initialToken : initialTokens) {
            var token = switch (initialToken) {
                case LAMBDA -> new Tokens.Lambda();
                case LPAREN -> new Tokens.LParen();
                case RPAREN -> new Tokens.RParen();
                case DOT -> new Tokens.Dot();
                default -> {
                    if (containsIllegalCharacters(initialToken)) {
                        System.err.println(STR."Illegal Identifier: \{initialToken}. Identifiers must consist only of alphabetical characters.");
                        System.exit(1);
                    }
                    yield new Tokens.Identifier(initialToken);
                }
            };

            result.add(token);
        }
        return result;
    }

    private static boolean containsIllegalCharacters(String identifierCandidate) {
        for (int i = 0; i < identifierCandidate.length(); i++) {
            if (!Character.isLetter(identifierCandidate.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
