package ru.maklas.model.logic.methods;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import ru.maklas.model.logic.model.Model;

public interface Method {

    Array<Array<Vector2>> solve(Model model) throws Exception;

}
