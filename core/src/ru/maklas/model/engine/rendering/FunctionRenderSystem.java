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

public class FunctionRenderSystem extends RenderEntitySystem {

    private ShapeRenderer sr;
    private OrthographicCamera cam;
    private ImmutableArray<Entity> formulas;
    private Batch batch;
    private BitmapFont font;

    private boolean drawAxis;
    private boolean drawPortions;
    private boolean drawFunctions;
    private boolean fillNet;
    private boolean drawNumbers;
    private Color axisColor = Color.WHITE;
    private Color fillColor = Color.GRAY;
    private Color numberColor = Color.WHITE;

    private String yAxisName;
    private String xAxisName;

    public FunctionRenderSystem() {
        this(true, true, true, true, true);
    }

    public FunctionRenderSystem(boolean drawAxis, boolean drawPortions, boolean drawFunctions, boolean drawNumbers, boolean fillNet) {
        this.drawAxis = drawAxis;
        this.drawPortions = drawPortions;
        this.drawFunctions = drawFunctions;
        this.drawNumbers = drawNumbers;
        this.fillNet = fillNet;
    }

    @Override
    public void onAddedToEngine(Engine engine) {
        super.onAddedToEngine(engine);
        sr = engine.getBundler().get(B.sr);
        cam = engine.getBundler().get(B.cam);
        batch = engine.getBundler().get(B.batch);
        font = A.images.font;
        formulas = entitiesFor(FunctionComponent.class);
    }

    @Override
    public void render() {
        Gdx.gl.glLineWidth(1);
        sr.begin(ShapeRenderer.ShapeType.Line);

        if (drawAxis){
            float leftX = Utils.camLeftX(cam);
            float rightX = Utils.camRightX(cam);
            float botY = Utils.camBotY(cam);
            float topY = Utils.camTopY(cam);

            if (fillNet){
                sr.setColor(fillColor);
                float minDelta = Math.min(rightX - leftX, topY - botY);
                double log = Math.log10(minDelta);
                int logFloor = (int) (log > 0 ? Math.floor(log) : Math.ceil(log));
                double portionStep = Math.pow(10, logFloor - 1) * (log - logFloor > 0.5f ? 1 : 0.5f);

                double xStart = (Math.ceil(leftX / portionStep) * portionStep);
                while (xStart < rightX){
                    float x = (float) xStart;
                    sr.line(x, botY, x, topY);
                    xStart += portionStep;
                }

                double yStart = (Math.ceil(botY / portionStep) * portionStep);
                while (yStart < topY){
                    float y = (float) yStart;
                    sr.line(leftX, y, rightX, y);
                    yStart += portionStep;
                }

            } else
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
                    double yStart = (Math.ceil(botY / portionStep) * portionStep);
                    while (yStart < topY){
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
                    double yStart = (Math.ceil(botY / portionStep) * portionStep);
                    while (yStart < topY) {
                        float y = (float) yStart;
                        if (!MathUtils.isEqual(y, 0)) {
                            String number = log > 0.5d ? Long.toString(Math.round(yStart)) : StringUtils.df(yStart, -(logFloor - 1));
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

    /** @deprecated Uses while loop, which is not safe **/
    @Deprecated
    private void drawWhile(ShapeRenderer sr, GraphFunction fun, double precision) {
        double min = Utils.camLeftX(cam);
        double max = Utils.camRightX(cam);
        double step = cam.zoom * precision;

        double previousX = min;
        double previousY = fun.f(min);
        double x = previousX;
        double y = previousY;

        while (x < max) {
            x += step;
            y = fun.f(x);
            sr.line(((float) previousX), ((float) previousY), ((float) x), ((float) y));
            previousX = x;
            previousY = y;
        }
    }

    private void draw(ShapeRenderer sr, GraphFunction fun, double precision) {
        double min = Utils.camLeftX(cam);
        double max = Utils.camRightX(cam);
        double step = cam.zoom * precision;

        double fullLength = max - min;
        double totalSteps = fullLength / step;


        double previousX = min;
        double previousY = fun.f(min);
        double x = previousX;
        double y = previousY;

        for (int i = 1; i < totalSteps; i++) {
            x = ((i / totalSteps) * fullLength) + min;
            y = fun.f(x);
            sr.line(((float) previousX), ((float) previousY), ((float) x), ((float) y));
            previousX = x;
            previousY = y;
        }
    }

    /** Рисовать систему ординат **/
    public FunctionRenderSystem setDrawAxis(boolean draw){
        this.drawAxis = draw;
        return this;
    }

    /** Рисовать пометки на осях (чёрточки) **/
    public FunctionRenderSystem setDrawAxisPortions(boolean draw){
        this.drawPortions = draw;
        return this;
    }

    /** Рисовать функции**/
    public FunctionRenderSystem setDrawFunctions(boolean draw){
        this.drawFunctions = draw;
        return this;
    }

    /** Рисовать сетку по всему экрану **/
    public FunctionRenderSystem setDrawNet(boolean draw){
        this.fillNet = draw;
        return this;
    }

    /** Цвет осей и пометок**/
    public FunctionRenderSystem setAxisColor(Color color){
        this.axisColor = color;
        return this;
    }

    /** Цвет сетки **/
    public FunctionRenderSystem setNetColor(Color color){
        this.fillColor = color;
        return this;
    }

    /** Цвет цифр у пометок **/
    public FunctionRenderSystem setNumberColor(Color color){
        this.numberColor = color;
        return this;
    }

    /** Дать названия осям. null - не показывать **/
    public FunctionRenderSystem setAxisNames(String xAxisName, String yAxisName){
        this.xAxisName = xAxisName;
        this.yAxisName = yAxisName;
        return this;
    }
}
