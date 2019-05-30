package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.maklas.model.assets.A;
import ru.maklas.model.assets.Asset;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.utils.Utils;
import ru.maklas.model.utils.gsm_lib.State;

public class MainMenuState extends State {

    private String text = "";
    private OrthographicCamera cam;

    @Override
    protected void onCreate() {
        A.all().foreach(Asset::load);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        MNW.backgroundColor.set(0.98f, 0.98f, 0.98f, 1);

    }

    @Override
    protected void update(float dt) {

    }

    @Override
    protected InputProcessor getInput() {
        return null;
    }

    @Override
    public void resize(int width, int height) {
        cam.setToOrtho(width, height);
    }

    @Override
    protected void render(Batch batch) {
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        A.images.font.draw(batch, text, Utils.camLeftX(cam) + 3, Utils.camTopY(cam) - 15);
        batch.end();
    }

    @Override
    protected void dispose() {

    }
}
