package ru.maklas.model;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import ru.maklas.model.mnw.PCDevice;
import ru.maklas.model.utils.Log;

import java.lang.ref.WeakReference;

public class AndroidDevice extends PCDevice {

    double diameterCache = 0;
    boolean diameterChecked = false;
    private WeakReference<Activity> activity;

    public AndroidDevice(Activity activity) {
        this.activity = new WeakReference<>(activity);
    }

    @Override
    public double getDeviceScreenDiagonal() {
        if (!diameterChecked){
            diameterChecked = true;
            diameterCache = getScreenInches();
        }
        return diameterCache;
    }

    private double getScreenInches(){
        try {
            DisplayMetrics dm = new DisplayMetrics();
            activity.get().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            int height = dm.heightPixels;

            float x = width / dm.xdpi; //Ширина экрана в дюймах
            float y = height / dm.ydpi; //Высота экрана в дюймах

            return Math.sqrt(x * x + y * y);
        } catch (Exception e) {
            return 4.8f;
        }
    }

    @Override
    public void setAllowScreenDim(boolean allow) {
        Activity activity = this.activity.get();
        if (activity == null) return;
        activity.runOnUiThread(() -> {

            try {
                if (allow) {
                    activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                } else {
                    activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                }
            } catch (Exception e) {
                Log.error("Screen dim", e);
            }
        });
    }

    @Override
    public void openGooglePlay() {
        Activity activity = this.activity.get();
        if (activity == null) return;

        final String appPackageName = activity.getPackageName();
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    @Override
    public void openEmail(String address, String defaultTheme, String defaultText) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("vnd.android.cursor.item/email");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {address});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, defaultTheme);
        emailIntent.putExtra(Intent.EXTRA_TEXT, defaultText);
        activity.get().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }


    @Override
    public void openLink(String linkAddress) {
        activity.get().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkAddress)));
    }
}
