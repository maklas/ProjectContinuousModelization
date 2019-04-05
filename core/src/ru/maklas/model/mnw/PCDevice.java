package ru.maklas.model.mnw;

import java.util.UUID;

public class PCDevice implements Device {

    @Override
    public String getDeviceName() {
        String property = System.getProperty("os.name");
        return property == null ? "unknown" : property;
    }

    @Override
    public boolean isDebug() {
        return true;
    }

    @Override
    public double getDeviceScreenDiagonal() {
        return 4.3f;
    }

    @Override
    public void setAllowScreenDim(boolean allow) {

    }

    @Override
    public long generateRandomID() {
        return System.nanoTime() * UUID.randomUUID().getMostSignificantBits();
    }

    @Override
    public void openGooglePlay() {

    }

    @Override
    public void openEmail(String address, String defaultTheme, String defaultText) {

    }

    @Override
    public void openLink(String linkAddress) {

    }
}
