package ru.maklas.model.functions;

public interface GraphFunction {

    double f(double x);


    static double mod(double a, double b) {
        double result = a % b;
        return result < 0? result + b : result;
    }

}
