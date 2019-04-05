package ru.maklas.model.utils;

public class NoCaseException extends RuntimeException {

    public NoCaseException() {
    }

    public NoCaseException(Enum e) {
        super("There was no case found for enum: " + e.getClass().getSimpleName() + '.' + e.name());
    }

    public NoCaseException(Class<? extends Enum> enumClass) {
        super("There was no case found for enum class: " + enumClass.getSimpleName());
    }
}
