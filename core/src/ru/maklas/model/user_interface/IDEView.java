package ru.maklas.model.user_interface;

import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class IDEView extends BaseStage {

    private final IDETextArea textArea;
    private final VisTable mainTable;
    private final VisTextButton testButton;

    public IDEView() {
        textArea = new IDETextArea();
        mainTable = new VisTable();
        mainTable.setFillParent(true);
        addActor(mainTable);
        mainTable.add(new VisLabel("Label")).top().left().row();
        mainTable.add(textArea).grow().row();
        testButton = new VisTextButton("Test");
        mainTable.add(testButton).left();
    }

    public IDETextArea getTextArea() {
        return textArea;
    }

    public void onTest(Runnable r){
        testButton.addChangeListener(r);
    }


}
