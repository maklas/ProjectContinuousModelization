package ru.maklas.model.functions;

public class SinusoidalAMFunction implements GraphFunction {

    public SineWaveFunction signal;
    public SineWaveFunction carrier;

    public SinusoidalAMFunction(SineWaveFunction signal, SineWaveFunction carrier) {
        this.signal = signal;
        this.carrier = carrier;
    }

    @Override
    public double f(double x) {
        return carrier.f(x) * signal.f(x);
    }
}
