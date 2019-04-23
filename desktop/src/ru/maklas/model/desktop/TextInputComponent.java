package ru.maklas.model.desktop;

import com.badlogic.gdx.utils.Array;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenTypes;
import org.fife.ui.rtextarea.RTextScrollPane;
import ru.maklas.model.logic.TokenType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class TextInputComponent extends JPanel {

    private final RSyntaxTextArea rSyntaxTextArea;
    private Array<TokenChange> changes = new Array<>();

    public TextInputComponent() {
        super(new BorderLayout());
        rSyntaxTextArea = new RSyntaxTextArea(20, 10);
        rSyntaxTextArea.setSyntaxEditingStyle("text/plain");
        RTextScrollPane scrollPane = new RTextScrollPane(rSyntaxTextArea);
        add(scrollPane, BorderLayout.CENTER);
        rSyntaxTextArea.getSyntaxScheme().getStyle(Token.ERROR_IDENTIFIER).underline = true;
        rSyntaxTextArea.getSyntaxScheme().getStyle(Token.ERROR_IDENTIFIER).foreground = Color.RED;
        rSyntaxTextArea.setFont(rSyntaxTextArea.getFont().deriveFont(16f));
    }

    public void setText(String text){
        rSyntaxTextArea.setText(text);
    }

    public String getText(){
        return rSyntaxTextArea.getText();
    }

    public void highlightError(ru.maklas.model.logic.Token mToken){

        try {
            int start = mToken.getStart();
            int end = mToken.getEnd();
            rSyntaxTextArea.setCaretPosition(mToken.getGlobalOffset() + mToken.getStart());
            Array<Token> arr = new Array<>();
            Token t = rSyntaxTextArea.getTokenListForLine(mToken.getLineNumber() - 1);
            int lineOff = t.getOffset();
            while (t != null && t.getType() != TokenTypes.NULL){
                int tStart = t.getOffset() - lineOff;
                int tEnd = t.getEndOffset() - lineOff;
                if ((tStart > start && tStart < end) || (tEnd > start && tEnd < end)){
                    arr.add(t);
                }
                t = t.getNextToken();
            }
            for (Token token : arr) {
                changes.add(new TokenChange(token, token.getType()));
                token.setType(TokenTypes.ERROR_IDENTIFIER);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearErrors(){
        try {
            changes.foreach(t -> t.t.setType(t.oldType));
        } catch (Exception e) {
            e.printStackTrace();
        }
        changes.clear();
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
