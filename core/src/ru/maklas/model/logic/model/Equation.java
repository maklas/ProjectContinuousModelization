package ru.maklas.model.logic.model;

import com.badlogic.gdx.utils.Array;
import org.mariuszgromada.math.mxparser.Expression;
import ru.maklas.model.logic.Token;
import ru.maklas.model.logic.TokenType;

public class Equation {

    Token name;
    Array<Token> expression;
    Expression compiledExpression;

    public Equation(Token name, Array<Token> expression) {
        this.name = name;
        this.expression = expression;
    }


    public Token getName() {
        return name;
    }

    public String getPureEquationName(){
        return name.getTextValue().substring(0, name.getTextValue().length() - 1);
    }

    public Array<Token> getExpression() {
        return expression;
    }

    public Token getExpressionAsToken() {
        return new Token(TokenType.expression, name.getLine(), name.getLineNumber(), expression.first().getStart(), expression.last().getEnd());
    }

    public Expression getCompiledExpression() {
        return compiledExpression;
    }

    public void setCompiledExpression(Expression compiledExpression) {
        this.compiledExpression = compiledExpression;
    }

    @Override
    public String toString() {
        return "{" + name + "=" + expression.toString(" ") + '}';
    }

    public String getFullEquation(){
        if (expression == null || expression.size == 0) return "";
        return name.getLine().substring(expression.first().getStart(), expression.last().getEnd());
    }
}
