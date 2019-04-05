package ru.maklas.model.user_interface;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisTable;

public class CornerView extends BaseStage {

    private final VisTable mainTable;
    public final Cell topLeft;
    public final Cell topRight;
    public final Cell bottomLeft;
    public final Cell bottomRight;

    public CornerView() {
        mainTable = new VisTable();
        addActor(mainTable);
        mainTable.setFillParent(true);

        topLeft = mainTable.add().expand().align(Align.topLeft);
        topRight = mainTable.add().expand().align(Align.topRight);
        mainTable.row();
        bottomLeft = mainTable.add().expand().align(Align.bottomLeft);
        bottomRight = mainTable.add().expand().align(Align.bottomRight);
    }
}
