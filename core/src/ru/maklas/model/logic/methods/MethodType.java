package ru.maklas.model.logic.methods;

import com.badlogic.gdx.utils.Array;
import ru.maklas.model.utils.NoCaseException;

public enum MethodType {


    EULER(Array.with("eu", "euler")),
    RK4(Array.with("runge-kutta4", "rk4")),
    RKF(Array.with("runge-kutta-fehlberg", "rkf", "rk45")),
    ;

    private final Array<String> codes;

    MethodType(Array<String> codes) {
        this.codes = codes;
    }

    public String getFullName(){
        switch (this){
            case EULER:
                return "Euler";
            case RK4:
                return "Runge-Kutta 4";
            case RKF:
                return "Runge–Kutta–Fehlberg";
            default:
                throw new NoCaseException(this);
        }
    }

    public String getLocalizedName(){
        switch (this){
            case EULER:
                return "Эйлер";
            case RK4:
                return "Рунге-Кутта 4";
            case RKF:
                return "Рунге–Кутта–Фельберга";
            default:
                return getFullName();
        }
    }

    public Array<String> getCodes() {
        return codes;
    }

    public static MethodType get(String code){
        if (code == null) return null;
        for (MethodType type : values()) {
            if (type.codes.contains(code.toLowerCase(), false)){
                return type;
            }
        }
        return null;
    }
}
