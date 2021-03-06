package ru.maklas.model.logic.model;

import com.badlogic.gdx.utils.Array;
import ru.maklas.model.logic.Token;

public class Model {

    private Token programName;
    private final Array<Var> vars = new Array<>();
    private final Array<Equation> equations = new Array<>();
    private final Array<Token> defaults = new Array<>();
    private final Array<Plot> plots = new Array<>();
    private Token paramToken;
    private Token method;
    private Token spanStart;
    private Token spanEnd;
    private Token step;
    private Token error;
    private String source;
    private final ModelMetaData metaData = new ModelMetaData();

    public Model() {

    }

    public Token getProgramName() {
        return programName;
    }

    public void setProgramName(Token programName) {
        this.programName = programName;
    }

    public Array<Var> getVars() {
        return vars;
    }

    public Array<Equation> getEquations() {
        return equations;
    }

    public Token getMethod() {
        return method;
    }

    public void setMethod(Token method) {
        this.method = method;
    }

    public Token getSpanStart() {
        return spanStart;
    }

    public void setSpanStart(Token spanStart) {
        this.spanStart = spanStart;
    }

    public Token getSpanEnd() {
        return spanEnd;
    }

    public void setSpanEnd(Token spanEnd) {
        this.spanEnd = spanEnd;
    }

    public Token getStep() {
        return step;
    }

    public void setStep(Token step) {
        this.step = step;
    }

    public Array<Token> getDefaults() {
        return defaults;
    }

    public Array<Plot> getPlots() {
        return plots;
    }

    public Token getError() {
        return error;
    }

    public void setError(Token error) {
        this.error = error;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Token getParamToken() {
        return paramToken;
    }

    public void setParamToken(Token paramToken) {
        this.paramToken = paramToken;
    }

    public ModelMetaData getMetaData() {
        return metaData;
    }

    @Override
    public String toString() {
        return "{" +
                "programName=" + programName +
                ", vars=" + vars +
                ", equations=" + equations +
                ", defaults=" + defaults +
                ", plots=" + plots +
                ", method=" + method +
                ", spanStart=" + spanStart +
                ", spanEnd=" + spanEnd +
                ", step=" + step +
                ", meta=" + metaData +
                '}';
    }
}
