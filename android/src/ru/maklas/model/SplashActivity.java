package ru.maklas.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends Activity {

    private static final int delay = 1550;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        setContentView(R.layout.splash_layout);

        ImageView logoImage = findViewById(R.id.imageView);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_anim);
        animation.reset();
        animation.setFillAfter(true);
        logoImage.setAnimation(animation);
        super.onCreate(savedInstanceState);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(), AndroidLauncher.class);
            startActivity(intent);
            finish();
        }, delay);
    }
}
