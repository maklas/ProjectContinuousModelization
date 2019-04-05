package ru.maklas.model.functions;

import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.expression.Compiler;
import ru.maklas.expression.Expression;
import ru.maklas.expression.ExpressionEvaluationException;

public class CustomExpressionFunction implements GraphFunction {

    private final ObjectMap<String, Double> params = new ObjectMap<>();
    private Expression expression;

    public CustomExpressionFunction() {
        this("");
    }

    public CustomExpressionFunction(Expression expression) {
        this.expression = expression;
    }

    public CustomExpressionFunction(String exp) {
        try {
            expression = Compiler.compile(exp);
        } catch (ExpressionEvaluationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double f(double x) {
        if (expression != null) {
            params.put("x", x);
            try {
                return expression.evaluate(params);
            } catch (Exception ignored) {
                if (!(ignored instanceof ExpressionEvaluationException)) {
                    System.err.println("Bad Expression: " + expression);
                }

            }
        }
        return 0;
    }

    public void setExpression(Expression expression) throws ExpressionEvaluationException {
        if (expression != null && (expression.variables().size >= 2 || (expression.variables().size == 1) && !expression.variables().contains("x", false))){
            throw new ExpressionEvaluationException("Can only have 1 variable X");
        }
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }
}
