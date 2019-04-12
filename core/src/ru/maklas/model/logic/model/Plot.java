package ru.maklas.model.logic.model;

import com.badlogic.gdx.graphics.Color;
import ru.maklas.model.logic.Token;

public class Plot {

    Token functionName;
    Token colorToken;
    Color color;

    public Plot(Token functionName, Token colorToken) {
        this.functionName = functionName;
        this.colorToken = colorToken;
    }

    public Token getFunctionName() {
        return functionName;
    }

    public Token getColorToken() {
        return colorToken;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "{" + functionName + (colorToken == null ? "" : ", color=" + colorToken) + '}';
    }
}
