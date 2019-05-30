package ru.maklas.model.logic.methods;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.mariuszgromada.math.mxparser.Argument;
import ru.maklas.model.logic.model.Model;

public class RungeKutta4 extends BaseMethod {

    @Override
    protected void iterate(Argument xArg, double from, double to, double step, ObjectMap<String, Argument> environment, Array<Function> functions, Model model) throws InterruptedException {
        double x = from;
        xArg.setArgumentValue(x);
        int iterations = (int) Math.ceil((to - from) / step);
        for (int i = 0; i < iterations; i++) {
            x += step;
            for (Function function : functions) {
                xArg.setArgumentValue(x);
                Argument yArg = environment.get(function.name);
                double y = yArg.getArgumentValue();

                double k1 = function.expression.calculate();
                xArg.setArgumentValue(x + step / 2.0);
                yArg.setArgumentValue(y + (step * k1) / 2.0);
                double k2 = function.expression.calculate();
                xArg.setArgumentValue(x + step / 2.0);
                yArg.setArgumentValue(y + (step * k2) / 2.0);
                double k3 = function.expression.calculate();
                xArg.setArgumentValue(x + step);
                yArg.setArgumentValue(y + step * k3);
                double k4 = function.expression.calculate();

                double result = y + (step  / 6.0) * (k1 + 2 * k2 + 2 * k3 + k4);
                function.add(x, result);
                yArg.setArgumentValue(result);
            }
            notifyCallback((i + 1.0) / iterations);
        }
    }
}
