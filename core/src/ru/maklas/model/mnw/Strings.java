package ru.maklas.model.mnw;

import ru.maklas.model.utils.Log;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Strings {

    static final Language defaultLanguage = Language.ENGLISH;

    Map<Language, StringResource> langToResMap = new HashMap<>();
    StringResource currentResource;
    Language currentLanguage;


    public Strings() {
        this(defaultLanguage);
    }

    public Strings(Language language){
        this.currentLanguage = language;
        this.currentResource = getLanguageFor(language);
    }

    public String getString(String key){
        String s = currentResource.get(key);
        if (s == null){
            Log.error("No string for '" + key + "' in " + currentLanguage);
        }
        return s;
    }

    public String getString(String key, String defaultValue){
        return currentResource.get(key, defaultValue);
    }

    public Language getCurrentLanguage(){
        return currentLanguage;
    }

    public StringResource getResource() {
        return currentResource;
    }

    public void setCurrent(Language loc){
        this.currentLanguage = loc;
        this.currentResource = getLanguageFor(loc);
    }

    private StringResource getLanguageFor(Language lang){

        final StringResource cached = langToResMap.get(lang);
        if (cached != null){
            return cached;
        }

        if (!Translation.initialized()){
            Translation.initialize();
        }

        StringResource ret;
        switch (lang){
            case ENGLISH:
                ret = Translation.en;
                break;
            case RUSSIAN:
                ret = Translation.ru;
                break;
            default:
                throw new RuntimeException("Unknown language '" + lang + "'");
        }
        langToResMap.put(lang, ret);
        return ret;
    }

    public static Language identifyDeviceLocale(Language defaultValue){
        Language lang = defaultValue;
        try {
            String deviceLanguage = Locale.getDefault().getLanguage();
            if (equalsOneOf(deviceLanguage, "ru", "be")){
                lang = Language.RUSSIAN;
            }
        } catch (Exception e) {
            Log.error("Locale identification", e);
            MNW.crash.report(e);
        }

        return lang;
    }

    private static boolean equalsOneOf(String language, String... languages){
        for (String lang : languages) {
            if (new Locale(lang).getLanguage().equals(language)){
                return true;
            }
        }
        return false;
    }
}
