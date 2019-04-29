package ru.maklas.model.desktop;

import com.badlogic.gdx.utils.Array;
import org.fife.ui.rsyntaxtextarea.*;
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
        rSyntaxTextArea.setFont(rSyntaxTextArea.getFont().deriveFont(16f));
        rSyntaxTextArea.getSyntaxScheme().getStyle(Token.ERROR_IDENTIFIER).underline = true;
        rSyntaxTextArea.getSyntaxScheme().getStyle(Token.ERROR_IDENTIFIER).foreground = Color.RED;
    }

    public void setText(String text){
        rSyntaxTextArea.setText(text);
    }

    public String getText(){
        return rSyntaxTextArea.getText();
    }

    public void highlightError(ru.maklas.model.logic.Token mToken){
        try {
            rSyntaxTextArea.getCaret().setDot(mToken.getSourceOffset());
            rSyntaxTextArea.getCaret().moveDot(mToken.getSourceOffset() + mToken.getLength());
            Array<Token> arr = new Array<>();
            Token t = rSyntaxTextArea.getTokenListFor(mToken.getSourceOffset(), mToken.getSourceOffset() + mToken.getLength());
            if (t != null && t.getType() != TokenTypes.NULL){
                do {
                    arr.add(t);
                    t = t.getNextToken();
                } while (t != null && t.getType() != TokenTypes.NULL);
            }
            for (Token token : arr) {
                token.setType(TokenTypes.ERROR_STRING_DOUBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rSyntaxTextArea.invalidate();
    }

    public void clearErrors(){

    }

    private static class TokenChange {
        Token t;
        int oldType;

        public TokenChange(Token t, int oldType) {
            this.t = t;
            this.oldType = oldType;
        }
    }

}
