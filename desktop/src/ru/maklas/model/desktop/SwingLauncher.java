package ru.maklas.model.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;
import ru.maklas.model.ProjectContinuousModelization;
import ru.maklas.model.mnw.MNW;
import ru.maklas.model.states.MainMenuState;

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
            if (MNW.gsm != null && MNW.gsm.getCurrentState() instanceof MainMenuState){
                ((MainMenuState) MNW.gsm.getCurrentState()).process(text);
            }
        });

        pack();
        setVisible(true);
        setSize(1000, 600);
        SwingUtilities.invokeLater(() -> split.setDividerLocation(JSplitPane.CENTER_ALIGNMENT));
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
