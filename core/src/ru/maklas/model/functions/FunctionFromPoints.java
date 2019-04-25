package ru.maklas.model.functions;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class FunctionFromPoints implements GraphFunction {

    private Array<Vector2> points;

    public FunctionFromPoints(Array<Vector2> points) {
        setPoints(points);
    }

    @Override
    public double f(double x) {
        return fMem(x);
    }


    public double fPlain(double x){
        Array<Vector2> points = this.points;
        if (points.size < 2) return Double.NaN;
        float leftX = points.first().x;
        float rightX = points.last().x;
        if (x > rightX || x < leftX) return Double.NaN;
        Vector2 prev = points.first();
        for (int i = 1; i < points.size; i++) {
            Vector2 curr = points.get(i);
            if (x >= prev.x && x <= curr.x){
                double portion = (x - prev.x) / (curr.x - prev.x);
                return prev.y + ((curr.y - prev.y) * portion);
            }
            prev = curr;
        }
        return Double.NaN;
    }

    int lastPrevIndex;
    double lastX;
    public double fMem(double x){
        Array<Vector2> points = this.points;
        if (points.size < 2) return Double.NaN;
        float leftX = points.first().x;
        float rightX = points.last().x;
        if (x > rightX || x < leftX) return Double.NaN;
        Vector2 prev;
        int startIndex;
        if (x > lastX){
            prev = points.get(lastPrevIndex);
            startIndex = lastPrevIndex + 1;
        } else {
            prev = points.first();
            startIndex = 1;
        }

        for (int i = startIndex; i < points.size; i++) {
            Vector2 curr = points.get(i);
            if (x >= prev.x && x <= curr.x){
                double portion = (x - prev.x) / (curr.x - prev.x);
                lastX = x;
                lastPrevIndex = i  -1;
                return prev.y + ((curr.y - prev.y) * portion);
            }
            prev = curr;
        }
        return Double.NaN;
    }

    public void setPoints(Array<Vector2> points) {
        if (points.size > 2){
            float x = points.first().x;
            for (int i = 1; i < points.size; i++) {
                float nextX = points.get(i).x;
                if (nextX <= x){
                    throw new RuntimeException("Points are incorrect. Can't be converted in function at " + i);
                }
                x = nextX;
            }
        }
        this.points = points.cpy();
    }

    public Array<Vector2> getPoints() {
        return points;
    }
}
