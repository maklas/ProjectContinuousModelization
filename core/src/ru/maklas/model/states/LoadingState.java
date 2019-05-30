package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import ru.maklas.model.assets.A;
import ru.maklas.model.engine.rendering.TextureUnit;
import ru.maklas.model.utils.Log;
import ru.maklas.model.utils.RadialSprite;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.gsm_lib.State;

public class LoadingState extends State {

    private OrthographicCamera cam;
    private RadialSprite radialSprite;
    private TextureRegion image;

    @Override
    protected void onCreate() {
        cam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        image = A.images.loading[A.images.loading.length - 1];
        radialSprite = new RadialSprite(image);
        radialSprite.setOrigin(image.getRegionWidth() / 2f, image.getRegionHeight() / 2f);
        radialSprite.setAngle(360);
    }

    @Override
    protected void update(float dt) {
    }

    @Override
    protected void render(Batch batch) {
        radialSprite.setColor(Color.SKY);
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        radialSprite.draw(batch, -image.getRegionWidth() / 2f, -image.getRegionHeight() / 2f, image.getRegionWidth(), -image.getRegionHeight());
        batch.end();
    }

    public void setProgress(double progress){
        if (radialSprite == null) return;
        progress = MathUtils.clamp(progress, 0, 1);
        radialSprite.setAngle((float) ((1 - progress) * 360));
    }

    @Override
    protected void dispose() {

    }
}
