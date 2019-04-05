package ru.maklas.model.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;
import ru.maklas.model.ProjectContinuousModelization;

import javax.swing.*;
import java.awt.*;

public class SwingLauncher extends JFrame {

    public SwingLauncher() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        LwjglAWTCanvas canvas = new LwjglAWTCanvas(new ProjectContinuousModelization());
        split.setLeftComponent(new JTextPane());
        Container container = new Container();
        container.setLayout(new BorderLayout());
        container.add(canvas.getCanvas(), BorderLayout.CENTER);
        split.setRightComponent(container);
        setContentPane(split);

        pack();
        setVisible(true);
        setSize(800, 600);
        SwingUtilities.invokeLater(() -> split.setDividerLocation(JSplitPane.CENTER_ALIGNMENT));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SwingLauncher::new);
    }

}
