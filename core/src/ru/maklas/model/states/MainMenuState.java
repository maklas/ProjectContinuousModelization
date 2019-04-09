package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.maklas.expression.Expression;
import ru.maklas.expression.ExpressionEvaluationException;
import ru.maklas.model.assets.A;
import ru.maklas.model.assets.Asset;
import ru.maklas.model.logic.Compiler;
import ru.maklas.model.logic.EvaluationException;
import ru.maklas.model.logic.model.Model;
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
        MNW.backgroundColor.set(0.95f, 0.95f, 0.95f, 1);

        try {
            Array<Vector2> points1 = new Array<>();
            Array<Vector2> points2 = new Array<>();
            Array<Vector2> points3 = new Array<>();
            Array<Vector2> points4 = new Array<>();
            Expression e1 = ru.maklas.expression.Compiler.compile("v * cos(th)");
            Expression e2 = ru.maklas.expression.Compiler.compile("v * sin(th)");
            Expression e3 = ru.maklas.expression.Compiler.compile("((-0.067725 * v^2) / 87.02) - (g * sin(v))");
            Expression e4 = ru.maklas.expression.Compiler.compile("(-1 * (g * cos(th))) / v");

            ObjectMap<String, Double> parameters = new ObjectMap<>();
            parameters.put("g", 9.81d);
            parameters.put("m", 43.51);

            double min = 0;
            double max = 40;
            double step = 0.0001;
            long iterations = (long) Math.ceil((max - min) / step);
            double v = 755;
            double th = 1;
            double _x = 0;
            double y = 0;

            double x = min;

            for (int i = 0; i <= iterations; i++) {
                parameters.put("v", v);
                parameters.put("th", th);
                points1.add(new Vector2((float) x, (float) _x));
                points2.add(new Vector2((float) x, (float) y));
                points3.add(new Vector2((float) x, (float) v));
                points4.add(new Vector2((float) x, (float) th));

                x += step;
                double t1 = e1.evaluate(parameters);
                _x += t1 * step;
                double t2 = e2.evaluate(parameters);
                y += t2 * step;
                double t3 = e3.evaluate(parameters);
                v += t3 * step;
                double t4 = e4.evaluate(parameters);
                th += t4 * step;
            }


            pushState(new FunctionGraphState(new Array<>(), Array.with(points1, points2, points3, points4)));
        } catch (ExpressionEvaluationException e1) {
            e1.printStackTrace();
        }


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
