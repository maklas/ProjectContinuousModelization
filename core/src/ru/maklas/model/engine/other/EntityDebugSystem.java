package ru.maklas.model.engine.other;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.*;
import ru.maklas.model.assets.ImageAssets;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.M;
import ru.maklas.model.engine.input.ScrollEvent;
import ru.maklas.model.engine.rendering.CameraComponent;
import ru.maklas.model.engine.rendering.CameraMode;
import ru.maklas.model.engine.rendering.CameraSystem;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.statics.EntityType;
import ru.maklas.model.utils.FloatAverager;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.TimeSlower;
import ru.maklas.model.utils.Utils;

/**
 * <li><b>Requires:</b> cam, batch
 * <li><b>Subscribes:</b> none
 * <li><b>Emits:</b> none
 * <li><b>Description:</b> Позволяет смотреть и анализировать Entity на экране.
 * Управлять камерой, паузить системы и прочее.
 */
public class EntityDebugSystem extends RenderEntitySystem {

    private ImmutableArray<Entity> entities;
    private BitmapFont font;
    private Batch batch;
    private OrthographicCamera cam;
    private static final float range = 12;
    private static final float minCamZoom = 0.0009765625f;
    private static final float maxCamZoom = 128f;
    private TextureRegion entityCircle;
    boolean paused = false;
    boolean highlightEntities = false;
    boolean help = false;
    boolean drawFramePercent = true;
    boolean zoomAtMouse = true;
    float defaultZoom;
    float zoomBeforePause = 1;
    Color color = Color.WHITE;
    Array<EntitySystem> pausedSystems = new Array<>();
    boolean wasUsingRuler = false;
    boolean isUsingRuler = false;
    boolean drawTextInfo = true;
    Vector2 rulerStart = new Vector2();
    Vector2 rulerEnd = new Vector2();
    FloatAverager framePercentAverager = new FloatAverager(60);
    float framePercentVal = 0;

    Array<String[]> helps = Array.with(
            new String[]{"H", "Help"},
            new String[]{"P", "Pause/Unpause"},
            new String[]{"K", "Enable/Disable Entity highlight"},
            new String[]{"M", "Change camera mode"},
            new String[]{"I", "Slow time"},
            new String[]{"O", "TimeScale = 1"},
            new String[]{"L", "Enable/Disable physics debug"},
            new String[]{"Z", "Zoom in"},
            new String[]{"X", "Zoom out"},
            new String[]{"C", "Revert zoom"}
    );

    @Override
    public void onAddedToEngine(Engine engine) {
        entities = engine.getEntities();
        font = addDisposable(new BitmapFont());
        font.setUseIntegerPositions(false);
        cam = engine.getBundler().get(B.cam);
        batch = engine.getBundler().get(B.batch);
        subscribe(ScrollEvent.class, this::onScroll);

        int intRange = (int) range;
        entityCircle = ImageAssets.createCircleImageNoFill(intRange, Color.CYAN);
        defaultZoom = cam.zoom;
    }

    public EntityDebugSystem setColor(Color color) {
        this.color = color;
        return this;
    }

    public EntityDebugSystem setTextInfoEnabled(boolean enabled) {
        this.drawTextInfo = enabled;
        return this;
    }

    public EntityDebugSystem addHelp(String button, String desc){
        helps.add(new String[]{button, desc});
        return this;
    }

    public EntityDebugSystem setZoomAtMouse(boolean enabled){
        zoomAtMouse = enabled;
        return this;
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(cam.combined);
        batch.begin();

        font.setColor(color);

        updateCamera();
        updateTimeline();
        updateHelp();
        updateRuler();
        updateZoom();
        updateEntities();
        updateFramePercent();
        if (Gdx.app.getType() != Application.ApplicationType.Desktop) return;

        if (highlightEntities){
            drawCirclesOnEntities();
        }

        if (drawTextInfo) {
            font.setColor(Color.WHITE);
            try {
                Vector2 mouse = Utils.toScreen(Gdx.input.getX(), Gdx.input.getY(), cam);
                float rangeSquared = range * range;
                for (Entity entity : entities) {
                    if (Vector2.dst2(mouse.x, mouse.y, entity.x, entity.y) < rangeSquared) {
                        printEntity(entity);
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (help) drawHelp();
        if (isUsingRuler) drawRuler();
        if (drawFramePercent) drawFramePercent();
        batch.end();
    }

    private void updateFramePercent() {
        framePercentAverager.addFloat(MNW.gsm.getLastFrameMillis());
        if (framePercentAverager.madeCircle()){
            framePercentVal = MathUtils.clamp(framePercentAverager.getAvg() / 16f, 0, 1f);
        }
    }

    private void drawFramePercent() {
        Color color;
        if (framePercentVal > 0.95){
            color = Color.RED;
        } else if (framePercentVal > 0.7f){
            color = Color.YELLOW;
        } else {
            color = Color.GREEN;
        }
        String s = StringUtils.addSpacesLeft(Math.round(framePercentVal * 100) + "%", 8);
        float x = Utils.camRightX(cam) - (62f * cam.zoom);
        float y = Utils.camTopY(cam) - (5 * cam.zoom);
        font.setColor(color);
        font.getData().setScale(cam.zoom);
        font.draw(batch, s, x, y, 10, Align.left, false);
        y -= 16 * cam.zoom;
        s = StringUtils.addSpacesLeft("x" + StringUtils.ff(cam.zoom, cam.zoom > 1 ? 1 : -(int) Math.floor(Math.log10(cam.zoom))), 8);
        font.draw(batch, s, x, y, 10, Align.left, false);
    }

    private void updateEntities() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.K)){
            highlightEntities = !highlightEntities;
        }
    }

    private void updateRuler() {
        isUsingRuler = Gdx.input.isButtonPressed(Input.Buttons.MIDDLE);
        if (!wasUsingRuler && isUsingRuler){
            rulerStart.set(Utils.getMouse(cam));
            rulerEnd.set(rulerStart);
        } else if (wasUsingRuler && isUsingRuler){
            rulerEnd.set(Utils.getMouse(cam));
        }

        wasUsingRuler = isUsingRuler;
    }


    private void onScroll(ScrollEvent e) {
        if (e.zoomIn()){
            zoomIn();
        } else {
            zoomOut();
        }
    }

    private void updateZoom() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Z)){
            zoomIn();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.X)){
            zoomOut();
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.C)){
            cam.zoom = defaultZoom;
        }
    }

    private void zoomIn(){
        float oldZoom = cam.zoom;
        float newZoom = cam.zoom * 0.5f;
        if (newZoom < minCamZoom) newZoom = minCamZoom;
        if (MathUtils.isEqual(oldZoom, newZoom)) return;

        ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);

        if (zoomAtMouse && (cameras.size() == 0 || cameras.get(0).get(M.camera).mode == CameraMode.BUTTON_CONTROLLED || cameras.get(0).get(M.camera).mode == CameraMode.DRAGGABLE)){

            Vector2 mouse = Utils.getMouse(cam);
            Vector2 distanceFromCenterToMouse = new Vector2(mouse.x - cam.position.x, mouse.y - cam.position.y);
            Vector2 positionAdjustment = new Vector2(distanceFromCenterToMouse).scl(newZoom / oldZoom);
            moveCamera(positionAdjustment.x, positionAdjustment.y);
        }

        cam.zoom = newZoom;
    }

    private void zoomOut(){
        float oldZoom = cam.zoom;
        float newZoom = cam.zoom * 2f;
        if (newZoom > maxCamZoom) newZoom = maxCamZoom;
        if (MathUtils.isEqual(oldZoom, newZoom)) return;

        ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);

        if (zoomAtMouse && (cameras.size() == 0 || cameras.get(0).get(M.camera).mode == CameraMode.BUTTON_CONTROLLED || cameras.get(0).get(M.camera).mode == CameraMode.DRAGGABLE)){

            Vector2 mouse = Utils.getMouse(cam);
            Vector2 dst = new Vector2(cam.position.x - mouse.x, cam.position.y - mouse.y);
            Vector2 dstMulBypartOfScreen = new Vector2(dst).scl(0.5f);
            Vector2 positionAdjustment = new Vector2(dstMulBypartOfScreen).scl(newZoom / oldZoom);
            moveCamera(positionAdjustment.x, positionAdjustment.y);
        }

        cam.zoom = newZoom;
    }

    private void moveCamera(float dx, float dy){
        ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);
        if (cameras.size() == 0){
            cam.translate(dx, dy);
        } else {
            cameras.get(0).x += dx;
            cameras.get(0).y += dy;
        }
    }

    private void drawHelp() {
        float scale = cam.zoom;

        float x = cam.position.x - (cam.viewportWidth/2) * cam.zoom + 10 * cam.zoom;
        float y = cam.position.y + (cam.viewportHeight/2) * cam.zoom - 10 * cam.zoom;
        float dy = 16 * scale;

        font.getData().setScale(scale);
        font.setColor(Color.BLACK);


        for (int i = 0; i < helps.size; i++) {
            String[] line = helps.get(i);
            font.draw(batch, line[0] + " - " + line[1], x, y, 10, Align.left, false);
            y -= dy;
        }
    }

    private void updateHelp() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)){
            help = !help;
        }
    }

    private void updateTimeline() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)){
            pauseUnpause();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.I)){
            TimeSlower timeSlower = engine.getBundler().get(B.timeSlower);
            if (timeSlower != null){
                timeSlower.setTargetScale(timeSlower.getTargetScale() / 2f);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.O)){
            TimeSlower timeSlower = engine.getBundler().get(B.timeSlower);
            if (timeSlower != null){
                timeSlower.setTargetScale(1);
            }
        }
    }

    public EntityDebugSystem pauseUnpause(){
        if (!paused){
            pausedSystems.clear();
            pausedSystems.addAll(engine.getSystemManager().getEntitySystems());
            pausedSystems.filter(s -> s.isEnabled() && !(s instanceof RenderEntitySystem) && !(s instanceof CameraSystem))
                    .foreach(s -> s.setEnabled(false));
            zoomBeforePause = cam.zoom;
        } else {
            pausedSystems.callAndClear(s -> s.setEnabled(true));
            cam.zoom = zoomBeforePause;
        }
        paused = !paused;
        return this;
    }

    private void updateCamera() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.M)){
            CameraComponent cc = getCc();
            if (cc != null) {
                cc.mode = Utils.next(cc.mode);
                MNW.gsm.print(cc.mode, 1f, Color.RED);
            }
        }
    }

    private CameraComponent getCc(){
        ImmutableArray<Entity> cameras = engine.entitiesFor(CameraComponent.class);
        if (cameras.size() == 0) return null;
        Entity camEntity = cameras.get(0);
        return camEntity.get(M.camera);
    }

    private void printEntity(Entity e) {
        Array<Component> components = e.getComponents();

        float scale = 1f * getSafeCamZoom();

        float x = Utils.camLeftX(cam) + (5 * cam.zoom);
        float y = Utils.camTopY(cam) - (5 * cam.zoom);
        float dy = 16 * scale;

        font.getData().setScale(scale);

        font.draw(batch, "id: "  + e.id + ", type: " + EntityType.typeToString(e.type) + ", x: " + (int) e.x + ", y: " + (int) e.y + ", ang: " + (int) e.getAngle(), x, y, 10, Align.left, false);
        for (Component c : components) {
            y -= dy;
            font.draw(batch, StringUtils.componentToString(c), x, y,10, Align.left, false);
        }
    }

    private void drawCirclesOnEntities(){
        ImmutableArray<Entity> entities = engine.getEntities();
        for (Entity entity : entities) {
            ImageAssets.draw(batch, entityCircle, entity.x, entity.y, 0.5f, 0.5f, cam.zoom, cam.zoom, 0);
        }
    }

    private void drawRuler() {
        float zoom = getSafeCamZoom();
        batch.setColor(Color.BLUE);
        batch.draw(entityCircle, rulerStart.x - range * zoom, rulerStart.y - range * zoom, entityCircle.getRegionWidth() * zoom, entityCircle.getRegionHeight() * zoom);
        batch.draw(entityCircle, rulerEnd.x - range * zoom, rulerEnd.y - range * zoom, entityCircle.getRegionWidth() * zoom, entityCircle.getRegionHeight() * zoom);
        batch.setColor(Color.WHITE);

        float scale = 2f * zoom;
        float dy = 16 * scale;
        font.getData().setScale(scale);
        font.setColor(Color.RED);

        Vector2 vec = Utils.vec1.set(rulerEnd).sub(rulerStart);
        font.draw(batch, StringUtils.ff(Math.abs(vec.x), 1), rulerEnd.x + 20 * scale, rulerEnd.y, 10, Align.left, false);
        font.draw(batch, StringUtils.ff(Math.abs(vec.y), 1), rulerEnd.x + 20 * scale, rulerEnd.y - dy, 10, Align.left, false);
        font.draw(batch, StringUtils.ff(vec.len(), 1), rulerEnd.x + 20 * scale, rulerEnd.y - dy - dy, 10, Align.left, false);
    }

    private float getSafeCamZoom(){
        return Math.max(minCamZoom, cam.zoom);
    }

}
