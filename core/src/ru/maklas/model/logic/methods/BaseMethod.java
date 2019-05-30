package ru.maklas.model.logic.methods;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.jetbrains.annotations.Nullable;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Var;

public abstract class BaseMethod implements Method {

    @Nullable
    private MethodCallback callback;
    private double oldProgress;

    @Override
    public Array<Array<Vector2>> solve(Model model, @Nullable MethodCallback callback) throws Exception {
        this.callback = callback;
        oldProgress = 0;
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
        Argument xArg = new Argument(hasX ? "t" : "x");
        environment.put(hasX ? "t" : "x", xArg);

        Argument[] arguments = environment.values().toArray().toArray(Argument.class);
        Array<Function> functions = model.getEquations().map(e -> createFunction(e.getPureEquationName(), e.getCompiledExpression()));
        for (Function function : functions) {
            function.expression.removeAllArguments();
            function.expression.addArguments(arguments);
        }

        double start = model.getSpanStart().getAsDouble();
        double end = model.getSpanEnd().getAsDouble();

        //Заполняем первые точки дефолтными значениями.
        for (Function function : functions) {
            function.add(start, environment.get(function.name).getArgumentValue());
        }

        iterate(xArg, start, end, model.getStep().getAsDouble(), environment, functions, model);

        return functions.map(e -> e.points);
    }

    protected Function createFunction(String name, Expression expression){
        return new Function(name, expression);
    }


    /**
     * К этому моменту все атрибуты уже были заполнены,
     * самые первые точки проставлены, определён xArg и всё что остаётся - понапихать точек.
     */
    protected abstract void iterate(Argument xArg, double from, double to, double step, ObjectMap<String, Argument> environment, Array<Function> functions, Model model) throws InterruptedException;

    protected final void notifyCallback(double progress) throws InterruptedException {
        if (Thread.interrupted()){
            throw new InterruptedException();
        }
        if (callback != null && Math.abs(progress - oldProgress) > 0.001) {
            callback.progressChanged(progress);
            oldProgress = progress;
        }
    }

    public static class Function {
        Array<Vector2> points = new Array<>();
        double lastY;
        String name;
        Expression expression;

        public Function(String name, Expression expression) {
            this.name = name;
            this.expression = expression;
        }

        void add(double x, double y){
            points.add(new Vector2((float) x, (float) y));
            lastY = y;
        }
    }
}
