package ru.maklas.model.desktop;

import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;

public class TextInputComponent extends JPanel {

    private final RSyntaxTextArea rSyntaxTextArea;

    public TextInputComponent() {
        super(new BorderLayout());
        rSyntaxTextArea = new RSyntaxTextArea(20, 10);
        rSyntaxTextArea.setSyntaxEditingStyle("text/plain");
        RTextScrollPane scrollPane = new RTextScrollPane(rSyntaxTextArea);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setText(String text){
        rSyntaxTextArea.setText(text);
    }

    public String getText(){
        return rSyntaxTextArea.getText();
    }

}
