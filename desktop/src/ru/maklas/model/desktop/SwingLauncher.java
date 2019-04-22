package ru.maklas.model.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ru.maklas.mengine.Entity;
import ru.maklas.model.ProjectContinuousModelization;
import ru.maklas.model.engine.M;
import ru.maklas.model.engine.formulas.FunctionComponent;
import ru.maklas.model.engine.rendering.CrossPointComponent;
import ru.maklas.model.functions.FunctionFromPoints;
import ru.maklas.model.functions.FunctionUtils;
import ru.maklas.model.logic.Compiler;
import ru.maklas.model.logic.EvaluationException;
import ru.maklas.model.logic.Token;
import ru.maklas.model.logic.methods.Euler;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Plot;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.states.FunctionGraphState;
import ru.maklas.model.utils.gsm_lib.GSMClearAndSet;

import javax.swing.*;
import java.awt.*;

public class SwingLauncher extends JFrame {

    public SwingLauncher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        TextInputComponent inputComponent = new TextInputComponent();
        Component libgdxComponent = createLibgdxComponent();
        split.setLeftComponent(inputComponent);
        split.setRightComponent(libgdxComponent);
        setContentPane(split);

        JMenuBar menu = new JMenuBar();
        JMenuItem item = new JMenuItem("Launch");
        menu.add(item);
        setJMenuBar(menu);
        item.addActionListener((e) -> {
            String text = inputComponent.getText();
            Model model;
            try {
                model = Compiler.compile(text);
                Array<Entity> entities = convertToEntities(model);
                Gdx.app.postRunnable(() -> MNW.gsm.setCommand(new GSMClearAndSet(new FunctionGraphState(entities, null, null))));
            } catch (Exception exception) {
                if (exception instanceof EvaluationException){
                    //TODO print
                } else {
                    System.err.println("Unexpected Exception!!!!");
                    //TODO print too
                }
                exception.printStackTrace();
            }
        });

        pack();
        setVisible(true);
        setSize(1000, 600);
        SwingUtilities.invokeLater(() -> split.setDividerLocation(JSplitPane.CENTER_ALIGNMENT));
    }

    private static Array<Entity> convertToEntities(Model model) throws Exception {
        Array<Entity> entities = new Array<>();

        if (model.getMethod().getTextValue().equalsIgnoreCase("euler")){
            Array<Array<Vector2>> functions = new Euler().solve(model);
            for (int i = 0; i < model.getEquations().size; i++) {
                Plot plot = null;
                for (Plot p : model.getPlots()) {
                    if (equals(model.getEquations().get(i).getName(), p.getFunctionName())){
                        plot = p;
                        break;
                    }
                }
                if (plot != null){
                    Entity e = new Entity();
                    FunctionFromPoints f = new FunctionFromPoints(functions.get(i));
                    FunctionComponent fc = new FunctionComponent(f);
                    fc.color = plot.getColor();
                    fc.name = plot.getFunctionName().getTextValue();
                    e.add(fc);
                    entities.add(e);
                }
            }

            int size = entities.size;
            for (int i = 0; i < size; i++) {
                for (int j = i + 1; j < size; j++) {
                    Entity a = entities.get(i);
                    Entity b = entities.get(j);
                    FunctionFromPoints f1 = (FunctionFromPoints) a.get(M.fun).graphFunction;
                    FunctionFromPoints f2 = (FunctionFromPoints) b.get(M.fun).graphFunction;
                    Array<Vector2> crossPoints = FunctionUtils.findCrossPoints(f1.getPoints(), f2.getPoints());
                    for (Vector2 crossPoint : crossPoints) {
                        entities.add(new Entity().add(new CrossPointComponent(a, b, crossPoint.x, crossPoint.y)));
                    }

                }
            }
            return entities;
        } else {
            throw new RuntimeException("Unknown method '" + model.getMethod() + "'");
        }
    }

    private static boolean equals(Token equationName, Token plotName){
        return equationName != null &&
                (equationName.getTextValue().equals(plotName.getTextValue())
                        || (equationName.getTextValue().substring(0, equationName.getTextValue().length() - 1).equals(plotName.getTextValue())));
    }


    private static Component createLibgdxComponent(){
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
