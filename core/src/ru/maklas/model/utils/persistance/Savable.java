package ru.maklas.model.utils.persistance;

import com.badlogic.gdx.utils.Array;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class Savable {

    protected void write(SaveWriter writer){
        writeReflection(writer);
    }

    protected void read(SaveReader reader){
        readReflection(reader);
    }

    private void writeReflection(SaveWriter writer){
        Field[] declaredFields = getClass().getDeclaredFields();

        try {

            for (Field declaredField : declaredFields) {
                if (declaredField.getAnnotation(Exclude.class) != null) continue;

                if (!declaredField.isAccessible())
                    declaredField.setAccessible(true);

                String name = declaredField.getName();
                Object value = declaredField.get(this);
                if (value == null) continue;
                Class<?> type = declaredField.getType();

                if (type.isPrimitive()){
                    if (value instanceof Integer){
                        writer.write(name, ((Integer) value));
                    } else if (value instanceof Boolean){
                        writer.write(name, ((Boolean) value));
                    } else if (value instanceof Float){
                        writer.write(name, ((Float) value));
                    } else if (value instanceof Long){
                        writer.write(name, ((Long) value));
                    } else if (value instanceof Double){
                        writer.write(name, ((Double) value));
                    }
                } else if (value instanceof String){
                    writer.write(name, ((String) value));
                } else if (value instanceof Savable){
                    writer.write(name, ((Savable) value));
                } else if (value instanceof Enum){
                    writer.write(name, ((Enum) value));
                } else if (value instanceof Array){
                    writer.write(name, ((Array) value));
                }

                else if (value instanceof Integer){
                    writer.write(name, ((Integer) value));
                } else if (value instanceof Float){
                    writer.write(name, ((Float) value));
                } else if (value instanceof Long){
                    writer.write(name, ((Long) value));
                } else if (value instanceof Double){
                    writer.write(name, ((Double) value));
                } else if (value instanceof Boolean){
                    writer.write(name, ((Boolean) value));
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    private void readReflection(SaveReader reader){
        Field[] declaredFields = getClass().getDeclaredFields();

        try {

            for (Field field : declaredFields) {
                if (field.getAnnotation(Exclude.class) != null || Modifier.isFinal(field.getModifiers())) continue;

                if (!field.isAccessible())
                    field.setAccessible(true);

                String name = field.getName();
                Class type = field.getType();
                Object val = field.get(this);

                if (type.isPrimitive()){
                    if (type.equals(int.class)){
                        field.set(this, reader.getInt(name, ((int) val)));
                    } else if (type.equals(boolean.class)){
                        field.set(this, reader.getBool(name, ((boolean) val)));
                    } else if (type.equals(float.class)){
                        field.set(this, reader.getFloat(name, ((float) val)));
                    } else if (type.equals(long.class)){
                        field.set(this, reader.getLong(name, ((long) val)));
                    } else if (type.equals(double.class)){
                        field.set(this, reader.getDouble(name, ((double) val)));
                    }
                } else if (type.equals(String.class)){
                    field.set(this, reader.getString(name, ((String) val)));
                } else if (Savable.class.isAssignableFrom(type)){
                    if (val != null)
                        field.set(this, reader.getSavable(name, ((Savable) val)));
                    else
                        field.set(this, reader.getSavable(name, type));
                } else if (type.isEnum()){
                    field.set(this, reader.getEnum(name, type, ((Enum) val)));
                } else if (type.equals(Array.class)){
                    ClassType arrayClassTypeAnnotation = field.getAnnotation(ClassType.class);
                    if (arrayClassTypeAnnotation == null) throw new RuntimeException("Array<> " + name + " must have @ClassType annotation for reflection persistence to work");
                    field.set(this, reader.getArray(name, ((Array) val), arrayClassTypeAnnotation.clazz()));
                }


                else if (type.equals(Integer.class)){
                    Integer intVal = (Integer) val;
                    field.set(this, reader.getInt(name, intVal == null ? 0 : intVal));
                } else if (type.equals(Float.class)){
                    Float floatVal = (Float) val;
                    field.set(this, reader.getFloat(name, floatVal == null ? 0 : floatVal));
                } else if (type.equals(Long.class)){
                    Long longVal = (Long) val;
                    field.set(this, reader.getLong(name, longVal == null ? 0 : longVal));
                } else if (type.equals(Double.class)){
                    Double doubleVal = (Double) val;
                    field.set(this, reader.getDouble(name, doubleVal == null ? 0 : doubleVal));
                } else if (type.equals(Boolean.class)){
                    Boolean boolVal = (Boolean) val;
                    field.set(this, reader.getBool(name, boolVal == null ? false : boolVal));
                } else {
                    System.err.println("Unknown type of variable for " + name);
                }
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
