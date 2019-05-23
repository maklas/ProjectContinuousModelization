package ru.maklas.model.logic;


import org.jetbrains.annotations.Nullable;

public class EvaluationException extends Exception {

    private Token token;

    public EvaluationException(Token token) {
        this.token = token;
    }

    public EvaluationException(String message, Token token) {
        super(message);
        this.token = token;
    }

    public EvaluationException(String message) {
        super(message);
    }

    @Nullable
    public Token getToken() {
        return token;
    }

    @Override
    public String getMessage() {
        if (false){
            return (token == null ? "" : "Строка " + token.getLineNumber() + ", столбец " + token.getStart() + ". ") + super.getMessage();
        }
        return super.getMessage();
    }

    public static EvaluationException invalidTokenException(String value){
        return new EvaluationException("Не корректный токен '" + value + "'");
    }

    public static EvaluationException unexpected(Token token){
        switch (token.getLength()){
            case 0:
                return new EvaluationException("Неожиданный токен '" + token.getTextValue() + "'", token);
            case 1:
                return new EvaluationException("Неожиданный символ '" + token.getTextValue() + "'", token);
            default:
                return new EvaluationException("Неожиданное слово '" + token.getTextValue() + "'", token);
        }
    }
}
