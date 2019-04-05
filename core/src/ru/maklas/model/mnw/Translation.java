package ru.maklas.model.mnw;

import com.badlogic.gdx.utils.ObjectMap;

public class Translation {

    public static StringResource en;
    public static StringResource ru;

    public static boolean initialized() {
        return en != null && ru != null;
    }

    public static void initialize() {
        en = new StringResource();
        ru = new StringResource();

        //МЕНЮШКИ
        string("Key", "Value", "Значение");
    }

    private static void string(String key, String eng, String rus){
        en.strings.put(key, eng);
        ru.strings.put(key, rus != null ? rus : eng);
    }

    private static String translation(String text, ObjectMap<Enum, String> enumTranslations){
        if (enumTranslations == null) return text;
        for (ObjectMap.Entry<Enum, String> e : enumTranslations.entries()) {
            String key = enumLiteral(e.key);
            int i = text.indexOf(key);
            if (i != -1){
                StringBuilder sb = new StringBuilder();
                sb.append(text, 0, i);
                sb.append(e.value);
                sb.append(text, i + key.length(), text.length());
                text = sb.toString();
            }
        }

        return text;
    }

    private static String enumLiteral(Enum e){
        return e.getClass().getSimpleName() + "." + e.name();
    }
}
