package ru.maklas.model.logic;


public class EvaluationException extends Exception {

    Token token;

    public EvaluationException(Token token) {
        super("Ошибка на позиции " + token.position() + ".");
        this.token = token;
    }

    public EvaluationException(String message, Token token) {
        super("Ошибка на позиции " + token.position() + ". " + message);
        this.token = token;
    }

    public EvaluationException(String message) {
        super(message);
    }

    public Token getToken() {
        return token;
    }

    public static EvaluationException invalidTokenException(String value){
        return new EvaluationException("Не корректный токен '" + value + "'");
    }

    public static EvaluationException unexpected(Token token){
        return new EvaluationException("Неожиданный токен '" + token.getTextValue() + "'", token);
    }
}
