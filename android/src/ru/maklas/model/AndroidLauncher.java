package ru.maklas.model;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.utils.Config;
import ru.maklas.model.utils.Log;

public class AndroidLauncher extends AndroidApplication {

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isTaskRoot()) {
            finish();
            return;
        }

        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();


        MNW.device = new AndroidDevice(this);
        MNW.statistics = new AndroidStatistics();

        loadWithAds(config);
        //initialize(new ProjectBird(new MainMenuState()), config);
    }


    private ApplicationListener getLauncher() {
        return new ProjectGenetics();
    }

    private void loadWithAds(AndroidApplicationConfiguration config){
        //Windows features
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);

        //Main layout
        //RelativeLayout mainLayout = new RelativeLayout(getApplicationContext());

        //LibGDX layout
        View libgdxView = initializeForView(getLauncher(), config);

        //Combinig layouts
        //RelativeLayout.LayoutParams adParams =
        //        new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
        //                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        //adParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        //mainLayout.addView(libgdxView, 0);

        setContentView(libgdxView);
    }

}
