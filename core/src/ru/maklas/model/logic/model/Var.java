package ru.maklas.model.logic.model;

import ru.maklas.model.logic.Token;

public class Var {

    Token name;
    Token value;

    public Var(Token name, Token value) {
        this.name = name;
        this.value = value;
    }

    public Token getName() {
        return name;
    }

    public Token getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "{" + name + "=" + value + '}';
    }
}
