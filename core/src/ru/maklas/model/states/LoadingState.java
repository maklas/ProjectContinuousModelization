package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import ru.maklas.model.assets.A;
import ru.maklas.model.engine.rendering.RenderUnit;
import ru.maklas.model.engine.rendering.TextureUnit;
import ru.maklas.model.utils.Log;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.gsm_lib.State;

public class LoadingState extends State {

    OrthographicCamera cam;
    RenderUnit renderUnit;

    @Override
    protected void onCreate() {
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        renderUnit = new TextureUnit(A.images.loading[0]).pivot(0.5f, 0.5f).scale(0.5f);
    }

    @Override
    protected void update(float dt) {
    }

    @Override
    protected void render(Batch batch) {
        batch.setColor(Color.WHITE);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        renderUnit.draw(batch, 0, 0, 0);
        batch.end();
    }

    public void setProgress(double progress){
        if (renderUnit == null) return;
        Log.trace("Setting progress: " + StringUtils.df(progress));
        progress = MathUtils.clamp(progress, 0, 1);
        int frame = MathUtils.clamp((int) Math.round(progress * A.images.loading.length), 0, A.images.loading.length - 1);
        renderUnit.setRegion(A.images.loading[frame]);
    }

    @Override
    protected void dispose() {

    }
}
