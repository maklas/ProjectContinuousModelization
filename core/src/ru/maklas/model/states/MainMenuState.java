package ru.maklas.model.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import ru.maklas.model.assets.A;
import ru.maklas.model.assets.Asset;
import ru.maklas.model.logic.Compiler;
import ru.maklas.model.logic.EvaluationException;
import ru.maklas.model.logic.methods.Euler;
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
            Expression e1 = new Expression("v * cos(th)");
            Expression e2 = new Expression("v * sin(th)");
            Expression e3 = new Expression("((-0.067725 * v^2) / 87.02) - (g * sin(v))");
            Expression e4 = new Expression("(-1 * (g * cos(th))) / v");

            Argument g = new Argument("g", 9.81d);
            Argument m = new Argument("m", 43.51);
            Argument v = new Argument("v", 755);
            Argument th = new Argument("th", 7);
            e1.addArguments(g, m, v, th);
            e2.addArguments(g, m, v, th);
            e3.addArguments(g, m, v, th);
            e4.addArguments(g, m, v, th);

            double min = 0;
            double max = 40;
            double step = 0.01;
            long iterations = (long) Math.ceil((max - min) / step);
            double _x = 0;
            double y = 0;

            double x = min;

            for (int i = 0; i <= iterations; i++) {
                points1.add(new Vector2((float) x, (float) _x));
                points2.add(new Vector2((float) x, (float) y));
                points3.add(new Vector2((float) x, (float) v.getArgumentValue()));
                points4.add(new Vector2((float) x, (float) th.getArgumentValue()));

                x += step;
                double t1 = e1.calculate();
                _x += t1 * step;
                double t2 = e2.calculate();
                y += t2 * step;
                double t3 = e3.calculate();
                v.setArgumentValue(v.getArgumentValue() + t3 * step);
                double t4 = e4.calculate();
                th.setArgumentValue(th.getArgumentValue() + t4 * step);
            }

            pushState(new FunctionGraphState(new Array<>(), Array.with(points1, points2, points3, points4)));
        } catch (Exception e) {
            e.printStackTrace();
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
            try {
                Array<Array<Vector2>> points = new Euler().solve(model);
                pushState(new FunctionGraphState(new Array<>(), points));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (EvaluationException e) {
            this.text = ExceptionUtils.getStackTrace(e);
        }
        System.out.println(this.text);
    }
}
