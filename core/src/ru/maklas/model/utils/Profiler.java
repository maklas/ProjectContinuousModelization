package ru.maklas.model.utils;

import com.badlogic.gdx.utils.ObjectMap;

public class Profiler {

    public static final double nanoToMillis = 1.0E-6;
    public static final double nanoToMicro = 1.0E-3;
    public static final double nanoToSeconds = 1.0E-9;

    private final int defaultCountTimes;
    private final ObjectMap<String, Long> starts = new ObjectMap<>();
    private final ObjectMap<String, FloatAverager> map = new ObjectMap<>();

    public Profiler(int defaultCountTimes) {
        this.defaultCountTimes = defaultCountTimes;
    }

    public FloatAverager register(String profilerName){
        return register(profilerName, defaultCountTimes);
    }

    public FloatAverager register(String profilerName, int countTimes){
        FloatAverager avg = new FloatAverager(countTimes);
        map.put(profilerName, avg);
        return avg;
    }

    public FloatAverager get(String name){
        return map.get(name);
    }

    public void start(String name){
        starts.put(name, System.nanoTime());
    }

    public void end(String name){
        long endTime = System.nanoTime();
        FloatAverager fa = map.get(name);
        if (fa == null){
            fa = register(name);
        }
        Long startTime = starts.get(name, endTime);
        fa.addFloat(endTime - startTime);
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("{");

        ObjectMap.Entries<String, FloatAverager> iterator = map.entries().iterator();

        double total = 0;
        for (ObjectMap.Entry<String, FloatAverager> e : iterator) {
            double avg = e.value.getAvg() * nanoToMicro;
            total += avg;
            sb.append(e.key).append("=").append(StringUtils.df(avg)).append(" us");
            sb.append(", ");
        }

        sb.append("total=").append(StringUtils.df(total)).append(" us");

        sb.append("}");
        return sb.toString();
    }

}
