package ru.maklas.model.utils;

/** Конфигурация игры. Данные переменные использутеся для условной компиляции. **/
public class Config {

    //Logging
    public static final boolean LOG = true;
    public static final boolean LOG_DRAWCALLS = true;           //Логи дроуколов после движка
    public static final boolean LOG_STATES = true;              //Логирование переходов между "экранами" (MainMenu, Game)
    public static final boolean LOG_ASSETS = true;              //Логи Загрузки и выгрузка ассетов + время за которое выполняется.
    public static final boolean PRETTY_PRINT_SAVES = true;      //Сохранять json в читаемом виде или в одну строчку.
    public static final boolean DEBUG_ENGINE = true;            //Использовать ли движок который профайлит, EventDispatcher с логированием неправильных ивентов.
    public static final boolean CHECK_STRINGS = true;           //Проверять ли наличие перевода в Translation.java. Внимание. Если выключить, могут быть NPE

    public static final String adMobAppId = "ca-app-pub-3610828690646342~6368058535";
    public static final String adMobInterstitialId = "ca-app-pub-3610828690646342/9800747525";
    public static final String adMobFakeInterstitialId = "ca-app-pub-3940256099942544/1033173712";
    public static final boolean useFakeAds = true;

    public static String gameSaveFile = "save.mnw";

}
