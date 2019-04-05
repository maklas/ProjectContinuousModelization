package ru.maklas.model.utils.gsm_lib;

import org.jetbrains.annotations.Nullable;

/**
 * Created by maklas on 08.10.2017.
 */

public abstract class PromiseState<T> extends State {

    @Nullable
    abstract public T getPromise();

}
