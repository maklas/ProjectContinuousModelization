package ru.maklas.model.functions;

import com.badlogic.gdx.math.MathUtils;

public class DeBroglieFunction implements GraphFunction {

    public double A = 1;
    public double c = 8;
    public double n0 = 10;
    public double n = 12;
    public double v = 1;
    public double t = 0;
    public double r = 0;
    public double a = 0.33;
    public double s = 0.33;
    public double u = 0;
    public double dk = 1.0;

    @Override
    public double f(double x) {
        double k = n0;
        double sum = 0;
        while (k <= n){
            double firstPart = A / (Math.abs(k - c) + 1);
            double secondPart = Math.cos((k * MathUtils.PI2 * x - v * t));
            double thirdPart = Math.cos(r * MathUtils.PI * x);

            sum += firstPart * secondPart * thirdPart + a;
            k += dk;
        }


        double forthPart = 1.0 / (Math.sqrt(MathUtils.PI2 * s));
        double fifthPart = -((x - u) * (x - u)) / (2 * s * s);

        return sum * forthPart * Math.pow(MathUtils.E, fifthPart);

    }
}
