package ru.maklas.model.engine.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ImmutableArray;
import ru.maklas.mengine.Engine;
import ru.maklas.mengine.Entity;
import ru.maklas.mengine.RenderEntitySystem;
import ru.maklas.model.assets.A;
import ru.maklas.model.engine.B;
import ru.maklas.model.engine.M;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.functions.GraphFunction;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.Utils;

public class ScalableFunctionRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private OrthographicCamera subCam;
    private ImmutableArray<Entity> formulas;
    private Batch batch;
    private BitmapFont font;

    /** Рисовать оси **/
    private boolean drawAxis;
    /** Рисовать чёрточки **/
    private boolean drawPortions;
    /** Рисовать функции**/
    private boolean drawFunctions;
    /** Рисовать цифры на осях**/
    private boolean drawNumbers;
    /** Скейлинг по оси Y. X всегда соотсветствует экрану **/
    private double yScale = 1;

    private Color axisColor = Color.WHITE;
    private Color numberColor = Color.WHITE;

    private String yAxisName;
    private String xAxisName;

    public ScalableFunctionRenderSystem() {
        this(true, true, true, true);
    }

    public ScalableFunctionRenderSystem(boolean drawAxis, boolean drawPortions, boolean drawFunctions, boolean drawNumbers) {
        this.drawAxis = drawAxis;
        this.drawPortions = drawPortions;
        this.drawFunctions = drawFunctions;
        this.drawNumbers = drawNumbers;
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        sr = engine.getBundler().get(B.sr);
        cam = engine.getBundler().get(B.cam);
        batch = engine.getBundler().get(B.batch);
        font = A.images.font;
        formulas = entitiesFor(FunctionComponent.class);
        subCam = new OrthographicCamera(cam.viewportWidth, cam.viewportHeight);
    }

    @Override
    public void render() {
        subCam.position.set(cam.position);
        subCam.viewportWidth = cam.viewportWidth;
        subCam.viewportHeight = (float) (cam.viewportHeight * yScale);
        subCam.zoom = cam.zoom;

        Gdx.gl.glLineWidth(1);
        sr.begin(ShapeRenderer.ShapeType.Line);

        //TODO
        if (drawAxis){
            float leftX = Utils.camLeftX(cam);
            float rightX = Utils.camRightX(cam);
            float botY = Utils.camBotY(cam);
            float topY = Utils.camTopY(cam);
            float trueBotY = Utils.camBotY(subCam);
            float trueTopY = Utils.camTopY(subCam);

            if (drawPortions){
                sr.setColor(axisColor);
                float portionThickness = cam.zoom * 4;
                float minDelta = Math.min(rightX - leftX, topY - botY);
                double log = Math.log10(minDelta);
                int logFloor = (int) (log > 0 ? Math.floor(log) : Math.ceil(log));
                double portionStep = Math.pow(10, logFloor - 1) * (log - logFloor > 0.5f ? 1 : 0.5f);

                if (topY > -portionThickness && botY < portionThickness) {
                    double xStart = (Math.ceil(leftX / portionStep) * portionStep);
                    while (xStart < rightX){
                        float x = (float) xStart;
                        sr.line(x, -portionThickness, x, portionThickness);
                        xStart += portionStep;
                    }
                }

                if (rightX > -portionThickness && leftX < portionThickness) {
                    double yStart = (Math.ceil(trueBotY / portionStep) * portionStep);
                    while (yStart < trueTopY){
                        float y = (float) yStart;
                        sr.line(-portionThickness, y, portionThickness, y);
                        yStart += portionStep;
                    }
                }
            }

            sr.setColor(axisColor);
            sr.line(leftX, 0, rightX, 0);
            sr.line(0, botY, 0, topY);
        }

        float currentLineWidth = 1f;

        if (drawFunctions) {
            for (Entity formula : formulas) {
                FunctionComponent fc = formula.get(M.fun);
                if (!MathUtils.isEqual(currentLineWidth, fc.lineWidth)) {
                    sr.flush();
                    Gdx.gl.glLineWidth(fc.lineWidth);
                    currentLineWidth = fc.lineWidth;
                }
                sr.setColor(fc.color);
                draw(sr, fc.graphFunction, fc.precision);
            }
        }

        sr.end();

        if (!MathUtils.isEqual(currentLineWidth, 1)){
            Gdx.gl.glLineWidth(1f);
        }

        boolean rbp = Gdx.input.isButtonPressed(Input.Buttons.RIGHT);
        if (drawAxis && (drawNumbers || xAxisName != null || yAxisName != null || rbp)){
            float leftX = Utils.camLeftX(cam);
            float rightX = Utils.camRightX(cam);
            float botY = Utils.camBotY(cam);
            float topY = Utils.camTopY(cam);
            float trueBotY = Utils.camBotY(subCam);
            float trueTopY = Utils.camTopY(subCam);

            batch.begin();
            font.setColor(numberColor);

            if (drawNumbers) {
                font.getData().setScale(cam.zoom * 0.75f);

                float portionThickness = cam.zoom * 4;
                float minDelta = Math.min(rightX - leftX, topY - botY);
                double log = Math.log10(minDelta);
                int logFloor = (int) (log > 0 ? Math.floor(log) : Math.ceil(log));
                double portionStep = Math.pow(10, logFloor) * (log - logFloor > 0.5f ? 1 : 0.5f);

                if (topY > -portionThickness && botY < portionThickness) {
                    double xStart = (Math.ceil(leftX / portionStep) * portionStep);
                    while (xStart < rightX) {
                        float x = (float) xStart;

                        String number = log > 0.5d ? Long.toString(Math.round(xStart)) : StringUtils.df(xStart, -(logFloor - 1));
                        font.draw(batch, number, x + 2 * cam.zoom, 15 * cam.zoom, 10, Align.left, false);
                        xStart += portionStep;
                    }
                }

                if (rightX > -portionThickness && leftX < portionThickness) {
                    double yStart = (Math.ceil(trueBotY / portionStep) * portionStep);
                    while (yStart < trueTopY) {
                        float y = (float) yStart;
                        if (!MathUtils.isEqual(y, 0)) {
                            String number = log > 0.5d ? Long.toString(Math.round(yStart * yScale)) : StringUtils.df(yStart * yScale, -(logFloor - 1));
                            font.draw(batch, number, 5 * cam.zoom, y + 15 * cam.zoom, 10, Align.left, false);
                        }
                        yStart += portionStep;
                    }
                }

            }
            font.getData().setScale(1 * cam.zoom);
            if (xAxisName != null && botY < 0 && topY > 0){
                font.draw(batch, xAxisName, rightX - (15 * cam.zoom * xAxisName.length()), -5 * cam.zoom);
            }
            if (yAxisName != null && leftX < 0 && rightX > 0){
                font.draw(batch, yAxisName, -15 * cam.zoom, topY - (10 * cam.zoom));
            }
            if (rbp){
                Vector2 mouse = Utils.getMouse(cam);
                font.getData().setScale(0.75f * cam.zoom);
                font.draw(batch, mouse.toString(), mouse.x, mouse.y + 10 * cam.zoom);
            }

            batch.end();
        }
    }

    private void draw(ShapeRenderer sr, GraphFunction fun, double precision) {
        double min = Utils.camLeftX(cam);
        double max = Utils.camRightX(cam);
        double step = cam.zoom * precision;

        double fullLength = max - min;
        double totalSteps = fullLength / step;


        double previousX = min;
        double previousY = fun.f(min) / yScale;
        double x;
        double y;

        for (int i = 1; i < totalSteps; i++) {
            x = ((i / totalSteps) * fullLength) + min;
            y = fun.f(x) / yScale;
            sr.line(((float) previousX), ((float) previousY), ((float) x), ((float) y));
            previousX = x;
            previousY = y;
        }
    }

    /** Рисовать систему ординат **/
    public ScalableFunctionRenderSystem setDrawAxis(boolean draw){
        this.drawAxis = draw;
        return this;
    }

    /** Рисовать пометки на осях (чёрточки) **/
    public ScalableFunctionRenderSystem setDrawAxisPortions(boolean draw){
        this.drawPortions = draw;
        return this;
    }

    /** Рисовать функции**/
    public ScalableFunctionRenderSystem setDrawFunctions(boolean draw){
        this.drawFunctions = draw;
        return this;
    }

    /** Цвет осей и пометок**/
    public ScalableFunctionRenderSystem setAxisColor(Color color){
        this.axisColor = color;
        return this;
    }

    /** Цвет цифр у пометок **/
    public ScalableFunctionRenderSystem setNumberColor(Color color){
        this.numberColor = color;
        return this;
    }

    /** Дать названия осям. null - не показывать **/
    public ScalableFunctionRenderSystem setAxisNames(String xAxisName, String yAxisName){
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        return this;
    }

    /** Scale по оси Y **/
    public ScalableFunctionRenderSystem setYScale(double scale){
        this.yScale = scale;
        return this;
    }
}
