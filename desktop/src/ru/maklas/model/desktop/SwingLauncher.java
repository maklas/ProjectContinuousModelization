package ru.maklas.model.desktop;

import com.badlogic.gdx.Application;
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
import ru.maklas.model.logic.methods.Method;
import ru.maklas.model.logic.methods.MethodProvider;
import ru.maklas.model.logic.methods.MethodType;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Plot;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.states.FunctionGraphState;
import ru.maklas.model.states.LoadingState;
import ru.maklas.model.utils.Log;
import ru.maklas.model.utils.StringUtils;
import ru.maklas.model.utils.gsm_lib.GSMClearAndSet;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SwingLauncher extends JFrame {

    private static Color COLOR_SUCCESS = new Color(0, 0.65f, 0);
    private static Color COLOR_ERROR = Color.RED;
    private static Color COLOR_INTERRUPTED = new Color(0.3f, 0.65f, 0);

    private final JTextArea errorTextArea;
    private ExecutorService executorService;
    private AtomicBoolean executing = new AtomicBoolean(false);
    private final TextInputComponent inputComponent;
    private final Container libgdxComponent;

    public SwingLauncher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        inputComponent = new TextInputComponent();
        errorTextArea = new JTextArea();
        errorTextArea.setLineWrap(true);
        errorTextArea.setEnabled(false);
        errorTextArea.setDisabledTextColor(Color.RED);
        errorTextArea.setFont(errorTextArea.getFont().deriveFont(16f));
        errorTextArea.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        leftSplit.setTopComponent(inputComponent);
        leftSplit.setBottomComponent(errorTextArea);

        libgdxComponent = createLibgdxComponent();
        split.setLeftComponent(leftSplit);
        split.setRightComponent(libgdxComponent);
        setContentPane(split);

        JMenuBar menu = new JMenuBar();
        JMenuItem launchButton = new JMenuItem("Запустить");
        JMenuItem cancelButton = new JMenuItem("Прервать");
        menu.add(launchButton);
        menu.add(cancelButton);
        setJMenuBar(menu);

        /* При нажатии "Запустить" */
        launchButton.addActionListener(e -> onLaunch());

        /* При нажатии "Прервать" */
        cancelButton.addActionListener(e -> onInterrupt());

        pack();
        setSize(1600, 900);
        setLocationRelativeTo(null);
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            split.setDividerLocation(0.4);
            leftSplit.setDividerLocation(0.8);
            inputComponent.setText(getDefaultText());
            inputComponent.dispatchEvent(new ActionEvent(split, 0, "click"));
        });
    }


    private void onLaunch(){
        if (executing.get()){
            return;
        }

        inputComponent.clearErrors();
        print("", COLOR_SUCCESS);
        String text = inputComponent.getText();
        Model model;
        try {
            long start = System.nanoTime();
            model = Compiler.compile(text);
            model.getMetaData().compilationTimeNano = System.nanoTime() - start;
            Log.trace("Model created. Time: " + StringUtils.dfSigDigits(model.getMetaData().compilationTimeUs(), 2, 3)  + " us");
            print("Компиляция выполнена успешно за " + StringUtils.dfSeparated(model.getMetaData().compilationTimeUs(), 2, 2) + " мкс", COLOR_SUCCESS);
        } catch (Exception exception) {
            if (exception instanceof EvaluationException){
                print(exception.getMessage(), COLOR_ERROR);
                Token token = ((EvaluationException) exception).getToken();
                if (token != null) {
                    inputComponent.highlightError(token);
                }
            } else {
                Log.error("Unexpected Exception!!!!", exception);
                print(ExceptionUtils.getStackTrace(exception), COLOR_ERROR);
            }
            return;
        }

        if (executorService == null || executorService.isTerminated()){
            executorService = Executors.newSingleThreadExecutor();
        }

        Application app = Gdx.app;
        if (executing.compareAndSet(false, true)){
            Log.debug("Execution started");
            app.postRunnable(() -> MNW.gsm.setCommand(new GSMClearAndSet(new LoadingState())));
            executorService.submit(() -> {
                try {
                    Array<Entity> entities = convertToEntities(model);
                    app.postRunnable(() -> MNW.gsm.setCommand(new GSMClearAndSet(new FunctionGraphState(entities, model.getSpanStart().getAsDouble(), model.getSpanEnd().getAsDouble()))));
                    SwingUtilities.invokeLater(() -> libgdxComponent.getComponent(0).requestFocus());
                } catch (Exception e1) {
                    if (e1 instanceof InterruptedException){

                        return;
                    }
                    SwingUtilities.invokeLater(() -> {
                        if (e1 instanceof EvaluationException){
                            print(e1.getMessage(), COLOR_ERROR);
                            Token token = ((EvaluationException) e1).getToken();
                            if (token != null) {
                                inputComponent.highlightError(token);
                            }
                        } else {
                            Log.error("Unexpected Exception!!!!", e1);
                            print(ExceptionUtils.getStackTrace(e1), COLOR_ERROR);
                        }
                    });
                } finally {
                    executing.set(false);
                    Log.debug("Execution finished");
                }
            });
        } else {
            errorTextArea.setText("Ошибка. Поток уже занят");
        }

    }

    private void onInterrupt() {
        if (executing.get()){
            executorService.shutdown();
        }
        inputComponent.clearErrors();
        print("Операция прервана", COLOR_INTERRUPTED);
        Log.debug("Execution Interrupted");
    }

    private static Array<Entity> convertToEntities(Model model) throws Exception {
        Array<Entity> entities = new Array<>();

        MethodType methodType = MethodType.get(model.getMethod().getTextValue());
        Method method = MethodProvider.getMethod(methodType);

        long start = System.nanoTime();
        Array<Array<Vector2>> functions = method.solve(model);
        model.getMetaData().methodExecutionTimeNano = System.nanoTime() - start;
        Log.trace("Method " + model.getMethod().getTextValue() + ". Time: " + StringUtils.dfSeparated(model.getMetaData().methodExecutionTimeMillis(), 0, 1) + " ms. Functions: " + functions.size + ". Points: " + (functions.size == 0 ? 0 : functions.get(0).size));

        if (Thread.interrupted()){
            throw new InterruptedException();
        }
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

        if (Thread.interrupted()){
            throw new InterruptedException();
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

    private static String getDefaultText(){
        return "Program program_name;\n" +
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
                "    method = rk4\n" +
                "    span = [0, 50];\n" +
                "    step = 0.5;\n" +
                "    error = 0.0265;\n" +
                "    x0 = [620, 10, 70];\n" +
                "    plot = [susc', sick', cured'];";
    }

    private void print(String text, Color color){
        if (SwingUtilities.isEventDispatchThread()){
            errorTextArea.setText(text);
            errorTextArea.setDisabledTextColor(color);
        } else {
            SwingUtilities.invokeLater(() -> {
                errorTextArea.setText(text);
                errorTextArea.setDisabledTextColor(color);
            });
        }
    }

    private void append(String text){
        if (SwingUtilities.isEventDispatchThread()){
            errorTextArea.append("\n" + text);
        } else {
            SwingUtilities.invokeLater(() -> {
                errorTextArea.setText("\n" + text);
            });
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingLauncher::new);
    }

}
