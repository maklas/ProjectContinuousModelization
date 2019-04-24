package ru.maklas.model.logic.methods;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.mariuszgromada.math.mxparser.Argument;
import ru.maklas.model.logic.model.Model;

public class Euler extends BaseMethod {

    @Override
    protected void iterate(Argument xArg, double from, double to, double step, ObjectMap<String, Argument> environment, Array<Function> functions, Model model) {
        double x = from;
        xArg.setArgumentValue(x);
        int iterations = (int) Math.ceil((to - from) / step);
        for (int i = 0; i < iterations; i++) {
            x += step;
            xArg.setArgumentValue(x);
            for (Function function : functions) {
                double t = function.expression.calculate();
                Argument val = environment.get(function.name);
                val.setArgumentValue(val.getArgumentValue() + (t * step));
                function.add(x, val.getArgumentValue());
            }
        }
    }
}
