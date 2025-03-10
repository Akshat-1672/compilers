package Compiler1.lox;

import java.util.List;

import static Compiler1.lox.TokenType.*;

public class Parser {

    private static class ParseError extends RuntimeException {
    }

    private final List<Token> tokens;

    private int current = 0;

    Parser(List<Token> tokens) {

        this.tokens = tokens;

    }

    // Helper Functions

    private boolean match(TokenType... tokenTypes) {

        for (TokenType type : tokenTypes) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;

    }

    private boolean check(TokenType type) {

        if (isAtEnd())
            return false;

        return peek().type == type;

    }

    private Token advance() {

        if (!isAtEnd())
            current++;

        return previous();

    }

    private boolean isAtEnd() {

        return peek().type == EOF;

    }

    private Token peek() {

        return tokens.get(current);

    }

    private Token previous() {

        return tokens.get(current - 1);

    }

    private Token consume(TokenType type, String message) {

        if (check(type))
            return advance();

        throw error(peek(), message);

    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if (previous().type == SEMICOLON)
                return;

            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    return;
            }

            advance();
        }
    }

    // Error Handling

    private ParseError error(Token token, String message) {

        Lox.error(token, message);

        return new ParseError();

    }

    // Grammar Rules

    Expr parse() {
        try {
            return expression();
        } catch (ParseError error) {
            return null;
        }
    }

    private Expr expression() {

        return equality();

    }

    private Expr equality() {

        Expr left = comparison();

        while (match(NOT_EQUAL, EQUAL_EQUAL)) {

            Token operator = previous();

            Expr right = comparison();

            left = new Expr.Binary(left, operator, right);

        }

        return left;

    }

    private Expr comparison() {

        Expr left = term();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {

            Token operator = previous();

            Expr right = term();

            left = new Expr.Binary(left, operator, right);

        }

        return left;

    }

    private Expr term() {

        Expr left = factor();

        while (match(MINUS, PLUS)) {

            Token operator = previous();

            Expr right = factor();

            left = new Expr.Binary(left, operator, right);

        }

        return left;

    }

    private Expr factor() {

        Expr left = unary();

        while (match(SLASH, STAR)) {

            Token operator = previous();

            Expr right = unary();

            left = new Expr.Binary(left, operator, right);

        }

        return left;

    }

    private Expr unary() {

        if (match(NOT, MINUS)) {

            Token operator = previous();

            Expr right = unary();

            return new Expr.Unary(operator, right);

        }

        return primary();

    }

    private Expr primary() {

        if (match(FALSE))
            return new Expr.Literal(false);

        if (match(TRUE))
            return new Expr.Literal(true);

        if (match(NIL))
            return new Expr.Literal(null);

        if (match(NUMBER, STRING)) {

            return new Expr.Literal(previous().literal);

        }

        if (match(LEFT_PAREN)) {

            Expr expr = expression();

            consume(RIGHT_PAREN, "Expect ')' after expression.");

            return new Expr.Grouping(expr);

        }

        throw error(peek(), "Expect expression.");

    }

}
