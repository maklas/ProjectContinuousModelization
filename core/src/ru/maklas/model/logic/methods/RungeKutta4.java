package ru.maklas.model.logic.methods;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.mariuszgromada.math.mxparser.Argument;
import ru.maklas.model.logic.model.Model;

public class RungeKutta4 extends BaseMethod {

    @Override
    protected void iterate(Argument xArg, double from, double to, double step, ObjectMap<String, Argument> environment, Array<Function> functions, Model model) {
        double x = from;
        xArg.setArgumentValue(x);
        int iterations = (int) Math.ceil((to - from) / step);
        for (int i = 0; i < iterations; i++) {
            x += step;
            for (Function function : functions) {
                Argument fArg = environment.get(function.name);
                xArg.setArgumentValue(x);
                double k1 = function.expression.calculate();
                xArg.setArgumentValue(x + step * (2f/ 3f));
                fArg.setArgumentValue(fArg.getArgumentValue() + (2d / 3d) * step * k1);
                double k2 = function.expression.calculate();
                fArg.setArgumentValue(function.lastY);

                Argument val = environment.get(function.name);
                double result = val.getArgumentValue() + step * (k1 * 0.25 + k2 * 0.75);
                val.setArgumentValue(result);
                function.add(x, result);
            }
        }
    }
}
