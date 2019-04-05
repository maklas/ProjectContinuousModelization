package ru.maklas.model.functions;

public class TriangleWaveFunction implements GraphFunction {

    public double amp;
    public double freq;
    public double offset;
    public double phase;

    public TriangleWaveFunction(double amp, double freq, double offset, double phase) {
        this.amp = amp;
        this.freq = freq;
        this.offset = offset;
        this.phase = phase;
    }

    @Override
    public double f(double x) {
        return Math.abs(GraphFunction.mod(2*freq*x + phase, 2) - 1) * amp - offset;
    }

}
