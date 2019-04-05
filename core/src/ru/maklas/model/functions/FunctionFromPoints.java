package ru.maklas.model.functions;

import com.badlogic.gdx.utils.FloatArray;

public class FunctionFromPoints implements GraphFunction {

    private FloatArray floats;

    public FunctionFromPoints() {
        this.floats = new FloatArray();
    }

    public FunctionFromPoints(FloatArray floats) {
        this.floats = floats;
    }

    @Override
    public double f(double x) {
        if (x < 0 || x > floats.size - 1) return 0;
        int prev = (int) Math.floor(x);
        int next = prev + 1;
        double portion = x % 1;
        if (next > floats.size - 1 || portion == 0d) return floats.get(prev);
        float prevVal = floats.get(prev);
        float nextVal = floats.get(next);
        return prevVal + ((nextVal - prevVal) * portion);
    }

    public void add(float data){
        this.floats.add(data);
    }

    public int size(){
        return floats.size;
    }

    public void clear(){
        floats.clear();
    }

}
