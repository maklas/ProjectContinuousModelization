package ru.maklas.model.user_interface;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextArea;
import ru.maklas.model.assets.A;

public class IDETextArea extends VisTable {

    private VisTextArea textArea;

    public IDETextArea() {
        textArea = new VisTextArea();
        add(textArea).grow();
        textArea.getStyle().font = A.images.font;
    }

    public void setText(String text){
        textArea.setText(text);
    }

    public String getText(){
        return textArea.getText();
    }

}
