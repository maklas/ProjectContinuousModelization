package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import ru.maklas.model.assets.A;
import ru.maklas.model.utils.gsm_lib.State;

public class LoadingState extends State {

    float time = 0;
    OrthographicCamera cam;

    @Override
    protected void onCreate() {
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void update(float dt) {
        time += dt;
    }

    @Override
    protected void render(Batch batch) {
        batch.setProjectionMatrix(cam.combined);
        float angle = (time * 360f) % 360f;
        batch.begin();
        batch.draw(A.images.circle, 0, 0, A.images.circle.getRegionWidth() / 2f, A.images.circle.getRegionHeight() / 2f, A.images.circle.getRegionWidth(), A.images.circle.getRegionHeight(), 1, 1, angle);
        batch.end();
    }

    @Override
    protected void dispose() {

    }
}
