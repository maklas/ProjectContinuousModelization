package ru.maklas.model.logic.methods;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.mariuszgromada.math.mxparser.Argument;
import ru.maklas.model.logic.model.Model;

public class RungeKutta45 extends BaseMethod {

    @Override
    protected void iterate(Argument xArg, double from, double to, double step, ObjectMap<String, Argument> environment, Array<Function> functions, Model model) {
        double err = model.getError().getAsDouble();
        double x = from;
        xArg.setArgumentValue(x);

        while (x < to){
            x += step;
            for (Function function : functions) {
                xArg.setArgumentValue(x);
                Argument yArg = environment.get(function.name);
                double y = yArg.getArgumentValue();

                double k1 = function.expression.calculate();

                xArg.setArgumentValue(x + step / 4.0);
                yArg.setArgumentValue(y + k1 / 4.0);
                double k2 = function.expression.calculate();

                xArg.setArgumentValue(x + (3.0 * step) / 8.0);
                yArg.setArgumentValue(y + (3.0/32.0) * k1 + (9.0/32.0) * k2);
                double k3 = function.expression.calculate();

                xArg.setArgumentValue(x + (12.0/13.0) * step);
                yArg.setArgumentValue(y + (1932.0/2197.0) * k1 - (7200.0/2197.0) * k2 + (7296.0/2197.0) * k3);
                double k4 = function.expression.calculate();

                xArg.setArgumentValue(x + step);
                yArg.setArgumentValue(y + (439.0/216.0)*k1 - 8*k2 + (3680.0/513.0)*k3 - (845.0/4104.0)*k4);
                double k5 = function.expression.calculate();

                xArg.setArgumentValue(x + step / 2.0);
                yArg.setArgumentValue(y - (8.0/27.0)*k1 + 2*k2 - (3544.0/2565.0)*k3 + (1859.0/4104.0)*k4 - (11.0/40.0)*k5);
                double k6 = function.expression.calculate();

                double result4 = y + (25.0/216.0)*k1 + (1408.0/2565.0)*k3 + (2197.0/4101.0)*k4 - 0.2*k5;
                double result5 = y + (16.0/135.0)*k1 + (6656.0/12825.0)*k3 + (28561.0/56430.0)*k4 - (9.0/50.0)*k5 + (8.0/55.0)*k6;

                function.add(x, result5);
                yArg.setArgumentValue(result5);
                step = calculateNewStep2(step, err, result4, result5);
            }
        }
    }

    private static double calculateNewStep2(double oldStep, double err, double rk4, double rk5){
        double eMax = 0.5;
        double eMin = 0.05;
        double stepMin = 0.001;

        double e = Math.abs(rk4 - rk5);
        if (e > eMax && oldStep > stepMin){
            return oldStep / 2.0;
        } else if (e < eMin){
            return  2 * oldStep;
        } else {
            return oldStep;
        }
    }
    private static double calculateNewStep(double oldStep, double err, double rk4, double rk5){
        double s = Math.pow((err * oldStep) / (2 * Math.abs(rk5 - rk4)), 0.25);
        double newStep = oldStep * s;
        if (newStep < 0.01){
            newStep = 0.01;
        } else if (newStep > 3){
            newStep = 3;
        }
        return newStep;
    }
}
