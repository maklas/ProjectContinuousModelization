package ru.maklas.model.mnw;

public interface Device {

    String getDeviceName();

    boolean isDebug();

    double getDeviceScreenDiagonal();

    void setAllowScreenDim(boolean allow);

    long generateRandomID();

    void openGooglePlay();

    void openEmail(String address, String defaultTheme, String defaultText);

    void openLink(String linkAddress);

}
