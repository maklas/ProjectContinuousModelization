package ru.maklas.model.functions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
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

    public static Array<Vector2> findCrossPoints(Array<Vector2> f1, Array<Vector2> f2){
        Array<Vector2> points = new Array<>();
        Vector2 tempVec = new Vector2();

        for (int i = 1; i < f1.size; i++) {
            Vector2 x1 = f1.get(i - 1);
            Vector2 x2 = f1.get(i);
            for (int j = 1; j < f2.size; j++) {
                Vector2 y1 = f2.get(j - 1);
                Vector2 y2 = f2.get(j);
                if (Intersector.intersectSegments(x1, x2, y1, y2, tempVec)){
                    points.add(tempVec.cpy());
                }
            }
        }
        return points;
    }

    /**
     * @return [min, max]
     */
    public static Array<Vector2> findMinMax(Array<Vector2> f){
        Vector2 min = new Vector2(0, Float.MAX_VALUE);
        Vector2 max = new Vector2(0, Float.MIN_VALUE);
        for (Vector2 p : f) {
            if (p.y >= max.y){
                max.x = p.x;
                max.y = p.y;
            }
            if (p.y <= min.y){
                min.x = p.x;
                min.y = p.y;
            }
        }

        return Array.with(min, max);
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

    public static Array<Vector2> rk4(GraphFunction f, float rangeStart, float rangeEnd, float step, float y0){
        float x = rangeStart;
        float y = y0;

        Array<Vector2> points = new Array<>();
        int iterations = MathUtils.ceil((rangeEnd - rangeStart) / step);
        points.add(new Vector2(x, y));
        for (int i = 0; i < iterations; i++) {
            x += step;

            float k1 = (float) f.f(x);
            float k2 = (float) f.f(x + step/2);
            float k3 = (float) f.f(x + step);
            y += 0.25 * (k1 + 2 * k2 + k3);
            points.add(new Vector2(x, y));
        }

        return points;
    }



}
