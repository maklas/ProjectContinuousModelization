package ru.maklas.model.logic.methods;

public interface MethodCallback {

    /**
     * @param progress Прогресс выполнения метода 0..1
     */
    void progressChanged(double progress);

}
