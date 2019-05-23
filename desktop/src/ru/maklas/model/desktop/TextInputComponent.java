package ru.maklas.model.desktop;

import com.badlogic.gdx.utils.Array;
import org.fife.ui.rsyntaxtextarea.*;
import org.fife.ui.rtextarea.RTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;

public class TextInputComponent extends JPanel {

    private final RTextArea rTextArea;
    private DefaultHighlighter highlighter;
    private Array<Object> tags = new Array<>();

    public TextInputComponent() {
        super(new BorderLayout());
        rTextArea = new RTextArea(20, 10);
        //rTextArea.setSyntaxEditingStyle("text/plain");
        RTextScrollPane scrollPane = new RTextScrollPane(rTextArea);
        add(scrollPane, BorderLayout.CENTER);
        rTextArea.setFont(rTextArea.getFont().deriveFont(16f));
        //rTextArea.getSyntaxScheme().getStyle(Token.ERROR_IDENTIFIER).underline = true;
        //rTextArea.getSyntaxScheme().getStyle(Token.ERROR_IDENTIFIER).foreground = Color.RED;
        highlighter = new DefaultHighlighter();
        rTextArea.setHighlighter(highlighter);
        rTextArea.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                clearErrors();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                clearErrors();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                clearErrors();
            }
        });
    }

    public void setText(String text){
        rTextArea.setText(text);
    }

    public String getText(){
        return rTextArea.getText();
    }

    public void highlightError(ru.maklas.model.logic.Token mToken){
        try {
            int start = mToken.getSourceOffset();
            int end = mToken.getSourceOffset() + mToken.getLength();
            Object tag = highlighter.addHighlight(start, end, new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
            tags.add(tag);
        } catch (Exception e) {
            e.printStackTrace();
        }
        rTextArea.invalidate();
    }

    public void clearErrors(){
        highlighter.removeAllHighlights();
        tags.clear();
    }

}
