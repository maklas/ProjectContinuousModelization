package ru.maklas.model.functions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

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


    private static final Array<Color> goodColors = Array.with(Color.BLUE, Color.RED, Color.FOREST, Color.BROWN, Color.VIOLET, Color.ORANGE);
    public static Color goodFunctionColor(int id){
        id = id >= 0 ? id : -id;
        id = id % goodColors.size;
        return goodColors.get(id);
    }

    public static void renderPoints(ShapeRenderer sr, Array<Vector2> points){
        for (int i = 1; i < points.size; i++) {
            Vector2 a = points.get(i - 1);
            Vector2 b = points.get(i);
            sr.line(a, b);
        }
    }

    public static Array<Vector2> euler(GraphFunction f, float rangeStart, float rangeEnd, float step, float y0){
        float x = rangeStart;
        float y = y0;

        Array<Vector2> points = new Array<>();
        int iterations = MathUtils.ceil((rangeEnd - rangeStart) / step);
        points.add(new Vector2(x, y));
        for (int i = 0; i < iterations; i++) {
            x += step;
            float t = (float) f.f(x);
            y += t * step;
            points.add(new Vector2(x, y));
        }

        return points;
    }



}
