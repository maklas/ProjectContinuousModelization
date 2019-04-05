package ru.maklas.model.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import ru.maklas.model.ProjectGenetics;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.utils.Log;

public class DesktopLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 640;
        config.width = 640;
        config.resizable = true;
        config.samples = 4;
        config.title = MNW.GAME_NAME;
        Log.logger = new FileLogger();
        new LwjglApplication(new ProjectGenetics(), config);
    }
}
