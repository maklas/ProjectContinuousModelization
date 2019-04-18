package ru.maklas.model.functions;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.maklas.model.utils.StringUtils;

import java.util.Arrays;

public class CustomExpressionFunction implements GraphFunction {

    private Expression expression;
    private Argument xArg = new Argument("x", 0);

    public CustomExpressionFunction() {
        this("");
    }

    public CustomExpressionFunction(String exp) {
        this(new Expression(exp));
    }

    public CustomExpressionFunction(Expression expression) {
        setExpression(expression);
    }

    @Override
    public double f(double x) {
        if (expression != null) {
            xArg.setArgumentValue(x);
            return expression.calculate();
        }
        return 0;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
        this.expression.addArguments(xArg);
        expression.checkSyntax();
        if (!StringUtils.isEmpty(expression.getErrorMessage())){
            System.err.println(expression.getErrorMessage());
        }
        if (expression.getMissingUserDefinedArguments().length > 0){
            System.err.println("Missing argumets: " + Arrays.toString(expression.getMissingUserDefinedArguments()));
        }
    }

    public Expression getExpression() {
        return expression;
    }
}
