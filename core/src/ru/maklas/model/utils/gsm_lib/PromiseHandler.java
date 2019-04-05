package ru.maklas.model.utils.gsm_lib;

import org.jetbrains.annotations.Nullable;

/**
 * Created by maklas on 08.10.2017.
 */

public abstract class PromiseHandler<T> {

    final boolean triggerAfterResume;

    public PromiseHandler(boolean triggerAfterResume) {
        this.triggerAfterResume = triggerAfterResume;
    }

    public abstract void handle(@Nullable T promise);

}
