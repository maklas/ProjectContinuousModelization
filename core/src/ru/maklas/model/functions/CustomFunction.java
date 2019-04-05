package ru.maklas.model.functions;

public class CustomFunction implements GraphFunction {


    @Override
    public double f(double x) {
        return squigly(x);
    }

    private static double threeWay(double x){
        return  1.0 / (1.0 - Math.pow(x, 2));
    }

    private static double squigly(double x){
        return  Math.cos(x) / x;
    }

}
