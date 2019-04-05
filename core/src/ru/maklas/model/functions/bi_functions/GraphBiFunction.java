package ru.maklas.model.functions.bi_functions;

import ru.maklas.model.functions.GraphFunction;

public abstract class GraphBiFunction {

    public abstract double f(double x1, double x2);

    public abstract double g(double x1, double x2);

    public final double absF(double x1, double x2){
        return Math.abs(f(x1, x2));
    }

    public final GraphFunction lockX1(double x1){
        return x -> GraphBiFunction.this.f(x1, x);
    }

    public final GraphFunction lockX2(double x2){
        return x -> GraphBiFunction.this.f(x, x2);
    }

    public String name(){
        return null;
    }

}
