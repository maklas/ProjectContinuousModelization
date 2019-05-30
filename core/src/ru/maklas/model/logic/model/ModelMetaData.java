package ru.maklas.model.logic.model;

public class ModelMetaData {

    public long compilationTimeNano;
    public long methodExecutionTimeNano;



    public double compilationTimeUs(){
        return compilationTimeNano / 1_000.0;
    }

    public double compilationTimeMillis(){
        return compilationTimeNano / 1_000_000.0;
    }

    public double methodExecutionTimeMillis(){
        return methodExecutionTimeNano / 1_000_000.0;
    }

    @Override
    public String toString() {
        return "{" +
                "compilationTime=" + compilationTimeUs() + " us" +
                ", methodExecutionTime=" + methodExecutionTimeMillis() + " ms" +
                '}';
    }
}
