package ru.maklas.model.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.apache.commons.lang3.exception.ExceptionUtils;
import ru.maklas.mengine.Entity;
import ru.maklas.model.ProjectContinuousModelization;
import ru.maklas.model.engine.M;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.engine.rendering.PointComponent;
import ru.maklas.model.functions.FunctionFromPoints;
import ru.maklas.model.functions.FunctionUtils;
import ru.maklas.model.logic.Compiler;
import ru.maklas.model.logic.EvaluationException;
import ru.maklas.model.logic.Token;
import ru.maklas.model.logic.methods.Euler;
import ru.maklas.model.logic.methods.Method;
import ru.maklas.model.logic.methods.RungeKutta4;
import ru.maklas.model.logic.methods.RungeKutta45;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Plot;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.states.FunctionGraphState;
import ru.maklas.model.utils.Log;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.gsm_lib.GSMClearAndSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SwingLauncher extends JFrame {

    public SwingLauncher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        TextInputComponent inputComponent = new TextInputComponent();
        JTextArea errorTextArea = new JTextArea();
        errorTextArea.setEnabled(false);
        errorTextArea.setDisabledTextColor(Color.RED);
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        leftSplit.setTopComponent(inputComponent);
        leftSplit.setBottomComponent(errorTextArea);

        Container libgdxComponent = createLibgdxComponent();
        split.setLeftComponent(leftSplit);
        split.setRightComponent(libgdxComponent);
        setContentPane(split);

        JMenuBar menu = new JMenuBar();
        JMenuItem item = new JMenuItem("Launch");
        menu.add(item);
        setJMenuBar(menu);
        item.addActionListener((e) -> {
            inputComponent.clearErrors();
            errorTextArea.setText("");
            String text = inputComponent.getText();
            Model model;
            try {
                long start = System.nanoTime();
                model = Compiler.compile(text);
                long end = System.nanoTime();
                Log.trace("Model created. Time: " + StringUtils.dfSigDigits((end - start) / 1000.0, 2, 3)  + " us");
                Array<Entity> entities = convertToEntities(model);
                Gdx.app.postRunnable(() -> MNW.gsm.setCommand(new GSMClearAndSet(new FunctionGraphState(entities, model.getSpanStart().getAsDouble(), model.getSpanEnd().getAsDouble()))));
                SwingUtilities.invokeLater(() -> libgdxComponent.getComponent(0).requestFocus());
                errorTextArea.setText("\u041a\u043e\u043c\u043f\u0438\u043b\u044f\u0446\u0438\u044f \u0432\u044b\u043f\u043e\u043b\u043d\u0435\u043d\u0430 \u0443\u0441\u043f\u0435\u0448\u043d\u043e");
                errorTextArea.setDisabledTextColor(new Color(0, 0.65f, 0));
            } catch (Exception exception) {
                errorTextArea.setDisabledTextColor(Color.RED);
                if (exception instanceof EvaluationException){
                    errorTextArea.setText(exception.getMessage());
                    Token token = ((EvaluationException) exception).getToken();
                    if (token != null) {
                        inputComponent.highlightError(token);
                    }
                } else {
                    System.err.println("Unexpected Exception!!!!");
                    errorTextArea.setText(ExceptionUtils.getStackTrace(exception));
                }
                exception.printStackTrace();
            }
        });

        pack();
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            split.setDividerLocation(0.4);
            leftSplit.setDividerLocation(0.8);
            inputComponent.setText("Program program_name;\n" +
                    "\n" +
                    "Var\n" +
                    "    a = 0.001;\n" +
                    "    b = 0.07;\n" +
                    "    c = 0.01;\n" +
                    "\n" +
                    "Equations\n" +
                    "    susc' = -a * susc * sick;\n" +
                    "    sick' = a * susc * sick - (b + c) * sick;\n" +
                    "    cured' = b * sick;\n" +
                    "\n" +
                    "Params\n" +
                    "    method = rk45\n" +
                    "    span = [0, 50];\n" +
                    "    step = 0.5;\n" +
                    "    error = 0.0265;\n" +
                    "    x0 = [620, 10, 70];\n" +
                    "    plot = [susc', sick', cured'];");
            inputComponent.dispatchEvent(new ActionEvent(split, 0, "click"));
        });
    }

    private static Array<Entity> convertToEntities(Model model) throws Exception {
        Array<Entity> entities = new Array<>();

        Method method = getMethod(model.getMethod().getTextValue());

        long start = System.currentTimeMillis();
        Array<Array<Vector2>> functions = method.solve(model);
        long end = System.currentTimeMillis();
        Log.trace("Method " + model.getMethod().getTextValue() + ". Time: " + (end - start) + " ms. Points: " + (functions.size == 0 ? 0 : functions.get(0).size));

        for (int i = 0; i < model.getEquations().size; i++) {
            if (!model.getPlots().isEmpty()) {
                Plot plot = null;
                for (Plot p : model.getPlots()) {
                    if (equals(model.getEquations().get(i).getName(), p.getFunctionName())) {
                        plot = p;
                        break;
                    }
                }
                if (plot != null) {
                    Entity e = new Entity();
                    FunctionFromPoints f = new FunctionFromPoints(functions.get(i));
                    FunctionComponent fc = new FunctionComponent(f);
                    fc.lineWidth = 2f;
                    fc.color = plot.getColor();
                    fc.name = plot.getFunctionName().getTextValue();
                    e.add(fc);
                    entities.add(e);
                }
            } else {
                Entity e = new Entity();
                FunctionFromPoints f = new FunctionFromPoints(functions.get(i));
                FunctionComponent fc = new FunctionComponent(f);
                fc.lineWidth = 2f;
                fc.color = FunctionUtils.goodFunctionColor(i);
                fc.name = model.getEquations().get(i).getPureEquationName();
                e.add(fc);
                entities.add(e);
            }
        }

        long val = 1;
        for (Array<Vector2> function : functions) {
            val *= function.size;
        }


        if (val < 5_000_000_000_000L) {
            int size = entities.size;
            for (int i = 0; i < size; i++) {
                Entity a = entities.get(i);
                FunctionFromPoints f1 = (FunctionFromPoints) a.get(M.fun).graphFunction;

                Array<Vector2> minMax = FunctionUtils.findMinMax(f1.getPoints());
                if (minMax.get(0).y != Float.MAX_VALUE && minMax.get(1).y != Float.MIN_VALUE && !Float.isNaN(minMax.get(0).y) && !Float.isNaN(minMax.get(1).y)) {
                    entities.add(new Entity().add(new PointComponent(minMax.get(0).x, minMax.get(0).y, "", com.badlogic.gdx.graphics.Color.BLUE)));
                    entities.add(new Entity().add(new PointComponent(minMax.get(1).x, minMax.get(1).y, "", com.badlogic.gdx.graphics.Color.RED)));
                }

                for (int j = i + 1; j < size; j++) {
                    Entity b = entities.get(j);
                    FunctionFromPoints f2 = (FunctionFromPoints) b.get(M.fun).graphFunction;
                    Array<Vector2> crossPoints = FunctionUtils.findCrossPoints(f1.getPoints(), f2.getPoints());
                    for (Vector2 crossPoint : crossPoints) {
                        if (!Float.isNaN(crossPoint.x) && !Float.isNaN(crossPoint.y))
                            entities.add(new Entity().add(new PointComponent(crossPoint.x, crossPoint.y, a.get(M.fun).name + " + " + b.get(M.fun).name)));
                    }
                }
            }
        } else {
            System.err.println("High complexity. Crossing points won't be calculated");
        }

        return entities;
    }
    
    private static Method getMethod(String name){
        if (name.equalsIgnoreCase("euler")){
            return new Euler();
        } else if (name.equalsIgnoreCase("rk4")){
            return new RungeKutta4();
        } else if (name.equalsIgnoreCase("rk45") || name.equalsIgnoreCase("rkf")){
            return new RungeKutta45();
        } else {
            throw new RuntimeException("Unknown method: " + name);
        }
    }

    private static boolean equals(Token equationName, Token plotName){
        return equationName != null &&
                (equationName.getTextValue().equals(plotName.getTextValue())
                        || (equationName.getTextValue().substring(0, equationName.getTextValue().length() - 1).equals(plotName.getTextValue())));
    }


    private static Container createLibgdxComponent(){
        LwjglAWTCanvas canvas = new LwjglAWTCanvas(new ProjectContinuousModelization());
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(canvas.getCanvas(), BorderLayout.CENTER);
        return container;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingLauncher::new);
    }

}
