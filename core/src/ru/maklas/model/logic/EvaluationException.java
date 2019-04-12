package ru.maklas.model.logic;

import ru.maklas.expression.ExpressionEvaluationException;

public class EvaluationException extends Exception {

    Token token;

    public EvaluationException(Token token) {
        super("Error at " + token.position() + ". ");
        this.token = token;
    }

    public EvaluationException(String message, Token token) {
        super("Error at " + token.position() + ". " + message);
        this.token = token;
    }

    public EvaluationException(String message) {
        super(message);
    }

    public EvaluationException(ExpressionEvaluationException e, Token expressionToken) {
        super("Failed to parse an expression", e);
        this.token = expressionToken;
    }

    public Token getToken() {
        return token;
    }

    public static EvaluationException invalidTokenException(String value){
        return new EvaluationException("Invalid token '" + value + "'");
    }

    public static EvaluationException unexpected(Token token){
        return new EvaluationException("Unexpected token '" + token.getTextValue() + "'", token);
    }
}
