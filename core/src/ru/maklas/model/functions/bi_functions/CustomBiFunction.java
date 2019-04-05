package ru.maklas.model.functions.bi_functions;

import com.badlogic.gdx.math.MathUtils;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class CustomBiFunction extends GraphBiFunction {


    @Override
    public double f(double x1, double x2) {
        return lemniscate(x1, x2);
    }

    public double g(double x1, double x2){
        return 0;
    }

    protected double woah(double x1, double x2) {
        return sin(sin(x1) + cos(x2)) - cos(sin(x1 * x2) + cos(x1));
    }

    protected double lemniscate(double x1, double x2) {
        return MathUtils.cos((float) (x1 * x1 * x2 * x2 * x2)) - MathUtils.sin((float) (x1 * x1 * x1 * x2 * x2));
        //return cos(x1 * x1 * x2 * x2 * x2) - sin(x1 * x1 * x1 * x2 * x2);
    }
}
