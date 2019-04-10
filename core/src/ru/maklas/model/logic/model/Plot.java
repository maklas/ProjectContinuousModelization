package ru.maklas.model.logic.model;

import ru.maklas.model.logic.Token;

public class Plot {

    Token functionName;
    Token color;

    public Plot(Token functionName, Token color) {
        this.functionName = functionName;
        this.color = color;
    }

    public Token getFunctionName() {
        return functionName;
    }

    public Token getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "{" + functionName + (color == null ? "" : ", color=" + color) + '}';
    }
}
