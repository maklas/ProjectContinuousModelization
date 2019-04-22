package ru.maklas.model.logic.methods;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Var;

public class Euler implements Method {

    @Override
    public Array<Array<Vector2>> solve(Model model) throws Exception {
        ObjectMap<String, Argument> environment = new ObjectMap<>();
        //1. Saving static vars
        for (Var var : model.getVars()) {
            environment.put(var.getName().getTextValue(), new Argument(var.getName().getTextValue(), var.getValue().getAsDouble()));
        }
        //2. Saving Defauts as vars
        for (int i = 0; i < model.getEquations().size; i++) {
            String name = model.getEquations().get(i).getPureEquationName();
            environment.put(name, new Argument(name, model.getDefaults().get(i).getAsDouble()));
        }

        boolean hasX = environment.keys().toArray().contains("x", false);
        Argument xArg;
        if (hasX){
            xArg = new Argument("t", 0);
            environment.put("t", xArg);
        } else {
            xArg = new Argument("x", 0);
            environment.put("x", xArg);
        }

        Argument[] arguments = environment.values().toArray().toArray(Argument.class);
        Array<EulerFun> eulers = model.getEquations().map(e -> new EulerFun(e.getPureEquationName(), e.getCompiledExpression()));
        for (EulerFun euler : eulers) {
            euler.expression.removeAllArguments();
            euler.expression.addArguments(arguments);
        }

        double x = model.getSpanStart().getAsDouble();
        for (EulerFun euler : eulers) {
            euler.add(x, environment.get(euler.name).getArgumentValue());
        }


        xArg.setArgumentValue(x);
        double step = model.getStep().getAsDouble();
        int iterations = (int) Math.ceil((model.getSpanEnd().getAsDouble() - model.getSpanStart().getAsDouble()) / step);
        for (int i = 0; i < iterations; i++) {
            x += step;
            xArg.setArgumentValue(x);
            for (EulerFun euler : eulers) {
                double t = euler.expression.calculate();
                Argument val = environment.get(euler.name);
                val.setArgumentValue(val.getArgumentValue() + (t * step));
                euler.add(x, val.getArgumentValue());
            }
        }


        return eulers.map(e -> e.points);
    }




    private static class EulerFun {
        Array<Vector2> points = new Array<>();
        double lastY;
        String name;
        Expression expression;

        public EulerFun(String name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }

        void add(double x, double y){
            points.add(new Vector2((float) x, (float) y));
            lastY = y;
        }
    }
}
