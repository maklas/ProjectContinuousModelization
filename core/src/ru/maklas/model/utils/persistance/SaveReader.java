package ru.maklas.model.utils.persistance;

import com.badlogic.gdx.utils.*;

import java.lang.reflect.Constructor;

public class SaveReader {

    JsonValue data;
    private final boolean enumByOrdinal;

    public SaveReader(JsonValue value, boolean enumByOrdinal) {
        this.data = value;
        this.enumByOrdinal = enumByOrdinal;
    }


    public String getString(String name){
        JsonValue val = data.get(name);
        return val != null ? val.asString() : null;
    }

    public String getString(String name, String defaultValue){
        JsonValue val = data.get(name);
        return val != null ? val.asString() : defaultValue;
    }

    public boolean getBool(String name, boolean defaultValue){
        JsonValue val = data.get(name);
        return val != null ? val.asBoolean() : defaultValue;
    }

    public void iterate(Predicate<JsonValue> p){
        for (JsonValue entry = data.child; entry != null; entry = entry.next)
            p.evaluate(entry);
    }

    public int size(){
        return data.size;
    }

    public boolean has(String name){
        return data.has(name);
    }

    public <T extends Enum<T>> T getEnum(String name, Class<T> clazz) {
        return getEnum(name, clazz, null);
    }

    public <T extends Enum<T>> T getEnum(String name, Class<T> clazz, T defaultValue) {
        JsonValue val = data.get(name);
        T retValue = null;
        if (val != null) {
            if (enumByOrdinal) {
                int i = val.asInt();
                T[] enumConstants = clazz.getEnumConstants();
                if (i < enumConstants.length) {
                    retValue = enumConstants[i];
                }
            } else {
                try {
                    retValue = Enum.valueOf(clazz, val.asString());
                } catch (Exception e) {}
            }
        }
        return retValue != null ? retValue : defaultValue;
    }

    public int getInt(String name, int defaultValue){
        JsonValue val = data.get(name);
        return val != null ? val.asInt() : defaultValue;
    }

    public int getInt(String name){
        JsonValue val = data.get(name);
        return val != null ? val.asInt() : 0;
    }

    public float getFloat(String name){
        JsonValue val = data.get(name);
        return val != null ? val.asFloat() : 0;
    }

    public float getFloat(String name, float defaultValue){
        JsonValue val = data.get(name);
        return val != null ? val.asFloat() : defaultValue;
    }

    public long getLong(String name, long defaultValue){
        JsonValue val = data.get(name);
        return val != null ? val.asLong() : defaultValue;
    }

    public double getDouble(String name, double defaultValue){
        JsonValue val = data.get(name);
        return val != null ? val.asDouble() : defaultValue;
    }

    /**
     * @return false if there was no savable
     */
    public boolean getSavable(String name, Savable savable){
        JsonValue oldData = this.data;
        JsonValue obj = oldData.get(name);
        if (obj == null) return false;

        this.data = obj;
        savable.read(this);
        this.data = oldData;
        return true;
    }

    /**
     * @return false if there was no savable
     */
    public <T extends Savable> T getSavable(String name, Class<T> clazz){
        JsonValue oldData = this.data;
        JsonValue obj = oldData.get(name);
        if (obj == null) return null;

        T instance;
        try {
            instance = build(clazz);
        } catch (Exception e) {
            throw new RuntimeException("Never!", e);
        }

        this.data = obj;
        instance.read(this);
        this.data = oldData;
        return instance;
    }

    public <T extends Savable> T getSavable(String name, MapFunction<SaveReader, T> readFunction){
        JsonValue oldData = this.data;
        JsonValue obj = oldData.get(name);
        if (obj == null) return null;

        this.data = obj;
        T instance = readFunction.map(this);
        this.data = oldData;
        return instance;
    }

    public <T extends Savable> Array<T> getArray(String name, Array<T> arr, Class<T> type){
        if (arr == null){
            arr = new Array<>();
        }
        JsonValue oldData = this.data;

        JsonValue jsonArr = oldData.get(name);
        if (jsonArr == null){
            arr.clear();
            return arr;
        }
        int i = 0;
        for (JsonValue value = jsonArr.child; value != null; value = value.next, i++) {
            this.data = value;
            if (arr.size > i){
                arr.get(i).read(this);
            } else {
                T instance;
                try {
                    instance = build(type);
                } catch (Exception e) {
                    throw new RuntimeException("Never!", e);
                }
                instance.read(this);
                arr.add(instance);
            }
        }

        if (arr.size > i){
            arr.setSize(i);
        }

        this.data = oldData;
        return arr;
    }

    public <T extends Savable> Array<T> getArray(String name, String typeFieldName, MapFunction<String, Class<T>> mapper){
        Array<T> arr = new Array<>();
        JsonValue oldData = this.data;

        JsonValue jsonArr = oldData.get(name);
        if (jsonArr == null){
            return arr;
        }

        for (JsonValue value = jsonArr.child; value != null; value = value.next) {
            JsonValue typeVal = value.get(typeFieldName);
            if (typeVal == null) continue;
            Class<T> type = mapper.map(typeVal.asString());

            this.data = value;
            T instance;
            try {
                instance = build(type);
            } catch (Exception e) {
                throw new RuntimeException("Never!", e);
            }
            instance.read(this);
            arr.add(instance);
        }

        this.data = oldData;
        return arr;
    }

    public void iterateOnArray(String name, Consumer<SaveReader> readerConsumer){
        JsonValue oldData = this.data;

        JsonValue jsonArr = oldData.get(name);
        if (jsonArr == null){
            return;
        }

        for (JsonValue value = jsonArr.child; value != null; value = value.next) {
            this.data = value;
            readerConsumer.accept(this);
        }
        this.data = oldData;
    }

    private <T extends Savable> T build(Class<T> type) throws Exception {
        Constructor<T> constructor = type.getConstructor();
        return constructor.newInstance();
    }
}
