package ru.maklas.model.functions;

public class ParabolaFunction implements GraphFunction {

    public double a;
    public double b;
    public double c;

    public ParabolaFunction(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public double f(double x) {
        return a * (x * x) + (b * x) + c;
    }
}
