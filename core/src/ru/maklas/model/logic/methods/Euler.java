package ru.maklas.model.logic.methods;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import ru.maklas.expression.Expression;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Var;

public class Euler implements Method {

    @Override
    public Array<Array<Vector2>> solve(Model model) throws Exception {
        ObjectMap<String, Double> environment = new ObjectMap<>();
        //1. Saving static vars
        for (Var var : model.getVars()) {
            environment.put(var.getName().getTextValue(), var.getValue().getAsDouble());
        }
        //2. Saving Defauts as vars
        for (int i = 0; i < model.getEquations().size; i++) {
            environment.put(model.getEquations().get(i).getName().getTextValue(), model.getDefaults().get(i).getAsDouble());
        }


        Array<EulerFun> eulers = model.getEquations().map(e -> new EulerFun(e.getName().getTextValue(), e.getCompiledExpression()));

        double x = model.getSpanStart().getAsDouble();
        double step = model.getStep().getAsDouble();
        int iterations = (int) Math.ceil((model.getSpanEnd().getAsDouble() - model.getSpanStart().getAsDouble()) / step);
        for (int i = 0; i < iterations; i++) {
            x += step;
            for (EulerFun euler : eulers) {
                double t = euler.expression.evaluate(environment);
                Double val = environment.get(euler.name);
                val += t * step;
                euler.add(x, val);
            }
            for (EulerFun euler : eulers) {
                environment.put(euler.name, euler.lastY);
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
