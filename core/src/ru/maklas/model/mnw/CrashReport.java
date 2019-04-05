package ru.maklas.model.mnw;

public interface CrashReport {

    void report(Exception e);

    void report(String error);

}
