package ru.maklas.model.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Sort;
import org.jetbrains.annotations.NotNull;
import ru.maklas.mengine.Component;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Locale;

public class StringUtils {


    public static String ff(float f, int numbersAfterComma){
        return String.format(Locale.ENGLISH, "%.0"+ numbersAfterComma + "f", f);
    }

    public static String ff(float f){
        return ff(f, 2);
    }

    public static String df(double d, int numbersAfterComma){
        return String.format(Locale.ENGLISH, "%.0"+ numbersAfterComma + "f", d);
    }

    /**
     * If double is more than zero, uses default digit after point.
     * If does is less than 0, gets to significant points specified.
     */
    public static String dfSigDigits(double d, int defaultDigitsAfterPoint, int significantDigits){
        int logFloor = d == 0d ? 0 : (int) Math.floor(Math.log10(d));
        if (logFloor >= 0) return df(d, defaultDigitsAfterPoint);
        return StringUtils.df(d, -(logFloor - (significantDigits - 1)));
    }

    public static String df(double d){
        return df(d, 2);
    }

    public static String bytesFormatted(long bytes, int numbersAfterComma){
        if (bytes < 1024){
            return bytes + " B";
        }

        if (bytes < 1048576){
            return ff(bytes / (float)1024, numbersAfterComma) + " kB";
        }

        if (bytes < 1073741824){
            return ff(bytes / (float)1048576, numbersAfterComma) + " mB";
        }

        return ff((float) (bytes / (double) 1073741824), numbersAfterComma) + " gB";
    }

    public static String priceFormatted(long price, char splitter){
        return priceFormatted(Long.toString(price), splitter);
    }

    public static String priceFormatted(@NotNull String price, char splitter){
        if (price.length() <= 3){
            return price;
        }

        StringBuilder builder = new StringBuilder();
        int counter = 0;
        for (int i = price.length() - 1; i >= 0; i--) {
            builder.append(price.charAt(i));
            counter++;
            if (counter == 3 && i != 0){
                builder.append(splitter);
                counter = 0;
            }
        }

        return builder.reverse().toString();
    }

    public static boolean isEmpty(String s){
        return s == null || "".equals(s);
    }

    public static String vec(Vector2 vec, int numbersAfterComma){
        return "(" + ff(vec.x, numbersAfterComma) + ", " + ff(vec.y, numbersAfterComma) + ")";
    }

    public static String addSpacesRight(String s, int minSize){
        int size = s.length();
        if (size >= minSize){
            return s;
        }
        StringBuilder builder = new StringBuilder(s);
        for (int i = 0; i < minSize - size; i++) {
            builder.append(" ");
        }
        return builder.toString();
    }

    public static String addSpacesLeft(String s, int minSize){
        int size = s.length();
        if (size >= minSize){
            return s;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < minSize - size; i++) {
            builder.append(" ");
        }
        builder.append(s);
        return builder.toString();
    }

    public static String componentToString(Component c){
        if (implementsToString(c)){
            return c.toString();
        } else {
            return c.getClass().getSimpleName().replace("Component", "") + StringUtils.objectToStringNoClassName(c);
        }
    }

    public static boolean implementsToString(Component c){
        Class<?> toStringImplClass;
        try {
            toStringImplClass = c.getClass().getMethod("toString").getDeclaringClass();
        } catch (Throwable e) {
            return false;
        }
        return toStringImplClass != Object.class;
    }

    public static String objectToStringNoClassName(Object o){
        Class<?> aClass = o.getClass();
        StringBuilder builder = new StringBuilder();
        builder.append('{');
        boolean first = true;

        try {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                String name = field.getName();
                field.setAccessible(true);
                Object val = field.get(o);
                String valAsString = val.getClass().isArray() ? Arrays.toString(((Object[]) val)) : val.toString();
                if (first) {
                    builder.append(name).append("=").append(valAsString);
                } else {
                    builder.append(", ").append(name).append("=").append(valAsString);
                }
                first = false;
            }

        } catch (Exception ignore) {}
        builder.append('}');
        return builder.toString();
    }
    public static String objectToString(Object o){
       return o.getClass().getSimpleName() + objectToStringNoClassName(o);
    }

    /**
     * Делит строку на линии
     * @param maxCharactersPerLine Максимальное количество символов в строке
     */
    public static String wrapString(String s, int maxCharactersPerLine){
        if (s == null) return null;
        if (s.length() < maxCharactersPerLine){
            return s;
        }

        String[] split = s.split(" ");
        if (split.length < 2){
            return s;
        }
        StringBuilder builder = new StringBuilder();
        int currentLineSize;

        builder.append(split[0]);
        currentLineSize = split[0].length();
        int i = 1;
        while (i < split.length){
            String word = split[i];

            if (currentLineSize == 0){
                builder.append(word);
                currentLineSize = word.length();
                i++;
                continue;
            }


            if (currentLineSize + word.length() > maxCharactersPerLine){
                builder.append('\n');
                currentLineSize = 0;
            } else {
                builder.append(" ").append(word);
                currentLineSize += word.length() + 1;
                i++;
            }


        }

        return builder.toString();

    }

    public static String defaultString(String s, String def){
        return s != null ? s : def;
    }

    public static String[] splitByLines(String s){
        return s.split("\\r?\\n");
    }

    public static int countChars(String s, char c){
        int amount = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c){
                amount++;
            }
        }
        return amount;
    }

    public static String entityToString(Entity e){
        return "Entity = {" +
                "id=" + e.id +
                ", type=" + e.type +
                ", x=" + e.x +
                ", y=" + e.y +
                ", angle=" + e.getAngle() +
                ", layer=" + e.layer +
                ", inEngine=" + e.isInEngine() +
                ", components=" + e.getComponents().map(StringUtils::componentToString) +
                '}';
    }

    public static String allEntitiesToString(Engine engine){
        StringBuilder builder = new StringBuilder("-----Entities------").append('\n');
        Entity[] entities = engine.getEntities().toArray(Entity.class);
        Sort.instance().sort(entities, Utils.comparingInt(e -> e.type));
        int counter = 0;
        for (Entity entity : entities) {
            builder.append("#").append(counter++).append(entityToString(entity)).append('\n');
        }

        builder.append("-----Entities------");
        return builder.toString();
    }


    public static String getTimeFormatted(int seconds){
        StringBuilder builder = new StringBuilder();
        int hours = getHours(seconds);
        int minutes = getMinutes(seconds);
        int secs = seconds % 60;

        if (hours > 0){
            builder.append(hours).append("h ");
        }

        if ((minutes > 0)){
            builder.append(minutes % 60).append("m ");
        }

        builder.append(secs).append("s");

        return builder.toString();
    }



    public static String getTimeFormattedSign(int seconds, String sign){
        StringBuilder builder = new StringBuilder();
        int hours = getHours(seconds);
        int minutes = getMinutes(seconds);
        int secs = seconds % 60;

        builder.append(addZeros(hours, 2)).append(sign);
        builder.append(addZeros(minutes % 60, 2)).append(sign);
        builder.append(addZeros(secs, 2));

        return builder.toString();
    }

    public static String getTimeShort(int seconds){
        int hours = getHours(seconds);
        int minutes = getMinutes(seconds) % 60;
        int secs = seconds % 60;

        if (hours > 0){
            return hours + " hrs";
        }

        if (minutes > 0){
            return minutes + " min";
        }

        return secs + " sec";
    }

    private static String addZeros(long number, int size){
        String numberAsString = Long.toString(number);
        if (numberAsString.length() >= size){
            return numberAsString;
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < size - numberAsString.length(); i++) {
            builder.append("0");
        }
        builder.append(numberAsString);
        return builder.toString();
    }

    private static int getMinutes(int seconds){
        return seconds/60;
    }

    private static int getHours(int seconds){
        return seconds/3600;
    }

    public static String wrap(String s, String wrapper){
        return wrapper + s + wrapper;
    }

    public static String addressToString(InetAddress address, int port) {
        if (address == null){
            return "NULL:" + port;
        }
        return address.getHostAddress() + ":" + port;
    }
}
