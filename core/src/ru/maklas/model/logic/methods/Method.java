package ru.maklas.model.logic.methods;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import org.jetbrains.annotations.Nullable;
import ru.maklas.model.logic.model.Model;

public interface Method {

    Array<Array<Vector2>> solve(Model model, @Nullable MethodCallback callback) throws Exception;

}
