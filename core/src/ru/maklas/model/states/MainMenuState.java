package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.utils.Array;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.maklas.model.assets.A;
import ru.maklas.model.assets.Asset;
import ru.maklas.model.logic.Compiler;
import ru.maklas.model.logic.EvaluationException;
import ru.maklas.model.logic.Token;
import ru.maklas.model.logic.TokenType;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.utils.Utils;
import ru.maklas.model.utils.gsm_lib.State;

public class MainMenuState extends State {

    private String text = "";
    private OrthographicCamera cam;

    @Override
    protected void onCreate() {
        A.getAllAssets().foreach(Asset::load);
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

    public void process(String text){
        try {
            Model model = Compiler.compile(text);
            this.text = String.valueOf(model);
        } catch (EvaluationException e) {
            this.text = ExceptionUtils.getStackTrace(e);
        }
        System.out.println(this.text);
    }
}
