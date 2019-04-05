package ru.maklas.model.user_interface;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

public class MainMenuView extends BaseStage {

    private VisTextButton functionBtn;
    private VisTextButton paretoBtn;

    public MainMenuView() {
        functionBtn = new VisTextButton("Минимум функции");
        paretoBtn = new VisTextButton("Парето");

        VisTable table = new VisTable();
        table.setFillParent(true);
        addActor(table);

        table.add(functionBtn).padBottom(20);
        table.row();
        table.add(paretoBtn);
    }


    public void onFunction(Runnable r){
        functionBtn.addChangeListener(r);
    }

    public void onPareto(Runnable r){
        paretoBtn.addChangeListener(r);
    }
}
