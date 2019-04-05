package ru.maklas.model.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Consumer;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import ru.maklas.model.assets.A;

public class ControlTable extends VisTable {

    private final boolean vertical;

    public ControlTable(boolean vertical) {
        this.vertical = vertical;
        setBackground(new TextureRegionDrawable(A.images.whiteBox10pxHalfAlpha));
        setColor(Color.LIGHT_GRAY.cpy());
        defaults().left().pad(1);
    }

    public void addCheckBox(String name, boolean checked, Consumer<Boolean> listener){
        VisCheckBox checkBox = new VisCheckBox(name, checked);
        checkBox.getStyle().fontColor = Color.BLACK;
        checkBox.addChangeListener(listener);
        add(checkBox);
        if (vertical){
            row();
        }
    }

    public void addButton(String name, Runnable listener){
        VisTextButton button = new VisTextButton(name);
        button.addChangeListener(listener);
        add(button);
        if (vertical){
            row();
        }
    }

    public void addButton(String name, Consumer<TextButton> listener){
        VisTextButton button = new VisTextButton(name);
        button.addChangeListener(() -> listener.accept(button));
        add(button);
        if (vertical){
            row();
        }
    }

    public Label addLabel(String text){
        VisLabel label = new VisLabel(text);
        label.setColor(Color.BLACK);
        add(label);
        if (vertical){
            row();
        }
        return label;
    }
}
