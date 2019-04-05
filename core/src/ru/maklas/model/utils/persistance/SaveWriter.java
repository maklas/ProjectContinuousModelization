package ru.maklas.model.utils.persistance;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.MapFunction;
import com.badlogic.gdx.utils.Predicate;

public class SaveWriter {

    Json json;
    private final boolean enumByOrdinal;

    public SaveWriter(Json json, boolean enumByOrdinal) {
        this.json = json;
        this.enumByOrdinal = enumByOrdinal;
    }

    public void write(String name, Savable val){
        if (val == null) return;
        json.writeObjectStart(name);
        val.write(this);
        json.writeObjectEnd();
    }

    public void write(String name, Array<? extends Savable> val){
        json.writeArrayStart(name);
        for (Savable savable : val) {
            json.writeObjectStart();
            savable.write(this);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    public <T extends Savable> void write(String name, Array<T> arr, Predicate<T> predicate){
        json.writeArrayStart(name);
        for (T t : arr) {
            if (predicate.evaluate(t)) {
                json.writeObjectStart();
                t.write(this);
                json.writeObjectEnd();
            }
        }
        json.writeArrayEnd();
    }

    public <T> void write(String name, Array<T> arr, BiConsumer<SaveWriter, T> consumer){
        json.writeArrayStart(name);
        for (T t : arr) {
            json.writeObjectStart();
            consumer.consume(this, t);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    public <T extends Savable> void write(String name, Array<T> arr, String typeFieldName, MapFunction<T, String> mapper){
        json.writeArrayStart(name);
        for (T t : arr) {
            json.writeObjectStart();
            json.writeValue(typeFieldName, mapper.map(t));
            t.write(this);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();
    }

    public void write(String name, String val){
        json.writeValue(name, val);
    }

    public void write(String name, boolean val){
        json.writeValue(name, val);
    }

    public void write(String name, int val){
        json.writeValue(name, val);
    }

    public <T extends Enum<T>> void write(String name, T val){
        json.writeValue(name, enumByOrdinal ? val.ordinal() : val.name());
    }

    public void write(String name, float val){
        json.writeValue(name, val);
    }

    public void write(String name, long val){
        json.writeValue(name, val);
    }

    public void write(String name, double val){
        json.writeValue(name, val);
    }
}
