package ru.maklas.model.functions;

public class FunctionUtils {

    /**
     * Возвращает точку X в диапозоне minX..maxX в которой фунция имеет минимальное значение.
     * Точность значения определяется шагом интеграции и количеством итерацией.
     */
    public static double findMinimalPoint(GraphFunction fun, double minX, double maxX, double step, int iterations){
        if (minX > maxX) throw new RuntimeException("minX > maxX");
        if (step <= 0) throw new RuntimeException("Step <= 0");
        if (iterations <= 0) throw new RuntimeException("Iterations >= 0");
        return _findMinimalPoint(fun, minX, maxX, step, iterations);
    }

    private static double _findMinimalPoint(GraphFunction fun, double minX, double maxX, double step, int iterations){
        double x = minX;
        double lowestX = x;
        double lowestY = fun.f(x);
        x += step;
        while (x <= maxX){
            double y = fun.f(x);
            if (y < lowestY){
                lowestY = y;
                lowestX = x;
            }
            x += step;
        }

        if (iterations == 1) return lowestX;
        return _findMinimalPoint(fun, Math.max(minX, lowestX - step), Math.min(maxX, lowestX + step), step / (maxX - minX), iterations - 1);
    }




}
