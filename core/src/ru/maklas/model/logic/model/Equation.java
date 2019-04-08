package ru.maklas.model.logic.model;

import com.badlogic.gdx.utils.Array;
import ru.maklas.model.logic.Token;

public class Equation {

    Token name;
    Array<Token> expression;

    public Equation(Token name, Array<Token> expression) {
        this.name = name;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "{" + name + "=" + expression.toString(" ") + '}';
    }

    public String getFullEquation(String source){
        if (expression == null || expression.size == 0) return "";
        return source.substring(expression.first().getStart(), expression.last().getEnd());
    }
}
