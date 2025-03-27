package org.roblburris.lambda.calculus;

import org.roblburris.lambda.calculus.ast.Context;
import org.roblburris.lambda.calculus.ast.Term;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public final class Interpreter {
    public static void main(String[] args) throws IOException {
        String input = Files.readString(Path.of("./lambda.txt"), StandardCharsets.UTF_8);
        var tokens = Lexer.tokenizer(input);
        Term ast = Parser.parse(tokens);


        Term prev = null;
        while (!ast.equals(prev)) {
            prev = ast;
            ast = Evaluation.evaluate(ast);
            Evaluation.print(new Context(List.of()), ast);
        }
    }
}
