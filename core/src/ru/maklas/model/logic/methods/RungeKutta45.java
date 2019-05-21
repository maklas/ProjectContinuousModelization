package ru.maklas.model.logic.methods;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.mariuszgromada.math.mxparser.Argument;
import ru.maklas.model.logic.model.Model;

public class RungeKutta45 extends BaseMethod {

    @Override
    protected void iterate(Argument xArg, final double from, final double to, final double step, ObjectMap<String, Argument> environment, Array<Function> functions, Model model) {
        double err = model.getError().getAsDouble();


        double x = from;
        double h = step;
        while (x < to){
            x += h;
            double newHMin = Double.MAX_VALUE;

            for (Function function : functions) {
                Argument yArg = environment.get(function.name);
                xArg.setArgumentValue(x);
                final double y = yArg.getArgumentValue();

                double k1 = function.expression.calculate() * h;

                xArg.setArgumentValue(x + h / 4.0);
                yArg.setArgumentValue(y + k1 / 4.0);
                double k2 = function.expression.calculate() * h;

                xArg.setArgumentValue(x + h * (3.0/8.0));
                yArg.setArgumentValue(y + (3.0/32.0) * k1 + (9.0/32.0) * k2);
                double k3 = function.expression.calculate() * h;

                xArg.setArgumentValue(x + (12.0/13.0) * h);
                yArg.setArgumentValue(y + (1932.0/2197.0)*k1 - (7200.0/2197.0)*k2 + (7296.0/2197.0)*k3);
                double k4 = function.expression.calculate() * h;

                xArg.setArgumentValue(x + h);
                yArg.setArgumentValue(y + (439.0/216.0)*k1 - 8*k2 + (3680.0/513.0)*k3 - (845.0/4104.0)*k4);
                double k5 = function.expression.calculate() * h;

                xArg.setArgumentValue(x + h / 2.0);
                yArg.setArgumentValue(y - (8.0/27.0)*k1 + 2*k2 - (3544.0/2565.0)*k3 + (1859.0/4104.0)*k4 - (11.0/40.0)*k5);
                double k6 = function.expression.calculate() * h;

                double result4 = y + (25.0/216.0)*k1 + (1408.0/2565.0)*k3 + (2197.0/4101.0)*k4 - 0.2*k5;
                double result5 = y + (16.0/135.0)*k1 + (6656.0/12825.0)*k3 + (28561.0/56430.0)*k4 - (9.0/50.0)*k5 + (2.0/55.0)*k6;

                function.add(x, result4);
                yArg.setArgumentValue(result4);
                double newH = calculateNewStep(h, err, result4, result5, step / 1024.0, step * 1024.0);
                if (newH < newHMin){
                    newHMin = newH;
                }
            }
            h = newHMin;
            if (h > to - x){
                h = to - x;
            }
        }
    }

    private static double calculateNewStep(double oldStep, double err, double rk4, double rk5, double min, double max){
        double s = Math.pow((err * oldStep) / (2 * Math.abs(rk5 - rk4)), 0.25);
        double newStep = oldStep * s;
        if (newStep < min){
            newStep = min;
        } else if (newStep > max){
            newStep = max;
        }
        return newStep;
    }
}
