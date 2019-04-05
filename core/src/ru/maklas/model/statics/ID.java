package ru.maklas.model.statics;

import ru.maklas.libs.Counter;

public class ID {

    public static final int background = 1;
    public static final int camera = 2;

    public static Counter counterForChromosomes(){
        return new Counter(1_000_000, 100_000_000);
    }

    public static Counter generationCounter(){
        return new Counter(1_000, 1_000_000);
    }

}
