package ru.maklas.model.logic.methods;

import ru.maklas.model.utils.NoCaseException;

public class MethodProvider {

    public static Method getMethod(MethodType type){
        switch (type){
            case EULER:
                return new Euler();
            case RK4:
                return new RungeKutta4();
            case RKF:
                return new RungeKuttaFehlberg();
            default:
                throw new NoCaseException(type);
        }
    }

}
