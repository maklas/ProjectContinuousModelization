package ru.maklas.model.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Consumer;
import com.badlogic.gdx.utils.MapFunction;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;
import ru.maklas.expression.Compiler;
import ru.maklas.expression.Expression;
import ru.maklas.expression.ExpressionEvaluationException;
import ru.maklas.model.functions.*;
import ru.maklas.model.utils.persistance.BiConsumer;

public class FunctionSelectionView extends BaseStage {

    VisTable mainTable;
    VisSelectBox<FunctionDefenition> selectBox;
    Container<ParamTable> paramTableContainer;
    Consumer<GraphFunction> changeListener = f -> {};

    Array<FunctionDefenition> functionDefenitions = new Array<>();

    public FunctionSelectionView(GraphFunction function) {

        mainTable = new VisTable();
        mainTable.setFillParent(true);
        mainTable.top();

        selectBox = new VisSelectBox<>();

        paramTableContainer = new Container<>();

        addActor(mainTable);
        mainTable.add(selectBox).left();
        mainTable.add().width(200);
        mainTable.add(paramTableContainer).right();

        functionDefenitions.add(new FunctionDefenition<>("Quadratic", new ParabolaFunction(0.1f, 0, 0))
                .addParam("a", 1, (f, a) -> f.a = a, f -> f.a)
                .addParam("b", 1, (f, b) -> f.b = b, f -> f.b)
                .addParam("c", 1, (f, c) -> f.c = c, f -> f.c)
                .update());

        functionDefenitions.add(new FunctionDefenition<>("Sine wave", new SineWaveFunction(50, 75, 0, 0))
                .addParam("Amp", 10, (f, a) -> f.amp = a, f -> f.amp)
                .addParam("Wave length", 3, (f, l) -> f.waveLen = l, f -> f.waveLen)
                .addParam("Y-shift", 1, (f, s) -> f.shift = s, f -> f.shift)
                .addParam("Decay", 1, (f, d) -> f.decay = d, f -> f.decay)
                .update());

        functionDefenitions.add(new FunctionDefenition<>("Triangle wave", new TriangleWaveFunction(50, 1 / 50d, 25, 0))
                .addParam("Amp", 10, (f, a) -> f.amp = a, f -> f.amp)
                .addParam("Wave length", 3, (f, l) -> f.freq = 1.0 / l, f -> 1.0 / f.freq)
                .addParam("Offset", 1, (f, s) -> f.offset = s, f -> f.offset)
                .addParam("Phase", 1, (f, d) -> f.phase = d, f -> f.phase)
                .update());

        functionDefenitions.add(new FunctionDefenition<>("AM function", new SinusoidalAMFunction(new SineWaveFunction(15, 1000), new SineWaveFunction(5, 20)))
                .addParam("Signal: Amplitude", 10, (f, a) -> f.signal.amp = a, f -> f.signal.amp)
                .addParam("Signal: Wave length", 3, (f, l) -> f.signal.waveLen = l, f -> f.signal.waveLen)
                .addParam("Carrier: Amplitude", 10, (f, a) -> f.carrier.amp = a, f -> f.carrier.amp)
                .addParam("Carrier: Wave length", 3, (f, l) -> f.carrier.waveLen = l, f -> f.carrier.waveLen)
                .update());

        functionDefenitions.add(new FunctionDefenition<>("De Broglie matter wave", new DeBroglieFunction())
                .addParam("A", 10, (f, a) -> f.A = a, f -> f.A)
                .addParam("c", 10, (f, a) -> f.c = a, f -> f.c)
                .addParam("n0", 10, (f, a) -> f.n0 = a, f -> f.n0)
                .addParam("n", 10, (f, a) -> f.n = a, f -> f.n)
                .addParam("v", 10, (f, a) -> f.v = a, f -> f.v)
                .addParam("t", 10, (f, a) -> f.t = a, f -> f.t)
                .addParam("r", 10, (f, a) -> f.r = a, f -> f.r)
                .addParam("a", 10, (f, a) -> f.a = a, f -> f.a)
                .addParam("s", 10, (f, a) -> f.s = a, f -> f.s)
                .addParam("u", 10, (f, a) -> f.u = a, f -> f.u)
                .addParam("dk", 10, (f, a) -> f.dk = a, f -> f.dk)
                .update());

        functionDefenitions.add(new FunctionDefenition<>("Custom", new CustomFunction())
                .update());

        FunctionDefenition<CustomExpressionFunction> customExpressionDefenition = new FunctionDefenition<>("CustomExpression", new CustomExpressionFunction("x"));
        functionDefenitions.add(customExpressionDefenition
                .addStringParam("Expression", "x", (f, e) -> {
                    try {
                        Expression compile = Compiler.compile(e);
                        f.setExpression(compile);
                        customExpressionDefenition.paramTable.getField("Expression").getStyle().fontColor = Color.WHITE;
                    } catch (ExpressionEvaluationException ignore) {
                        customExpressionDefenition.paramTable.getField("Expression").getStyle().fontColor = Color.RED;
                    }
                }, f -> {
                    Expression exp = f.getExpression();
                    return exp == null ? "" : exp.toString();
                })
                .update());


        selectBox.setItems(functionDefenitions);

        setFrom(function);

        selectBox.addChangeListener(this::set);
    }

    private void setFrom(GraphFunction function){
        for (FunctionDefenition functionDefenition : functionDefenitions) {
            if (functionDefenition.clazz.isAssignableFrom(function.getClass())){
                selectBox.setSelected(functionDefenition);
                functionDefenition.paramTable.function = function;
                set(functionDefenition);
                functionDefenition.paramTable.update();
                return;
            }
        }
    }

    private void set(FunctionDefenition functionDefenition){
        paramTableContainer.setActor(functionDefenition.paramTable);
        if (changeListener != null){
            changeListener.accept(getFunction());
        }
    }


    public GraphFunction getFunction(){
        return paramTableContainer.getActor().getFunction();
    }

    public void onFunctionChange(Consumer<GraphFunction> functionConsumer){
        changeListener = functionConsumer;
    }



    private class FunctionDefenition<T extends GraphFunction> {

        ParamTable<T> paramTable;
        String name;
        Class<T> clazz;

        public FunctionDefenition(String name, T instance) {
            this.name = name;
            clazz = (Class<T>) instance.getClass();
            paramTable = new ParamTable<>(instance).onChanged(f -> changeListener.accept(f));
        }

        public FunctionDefenition<T> addParam(String name, float defaultValue, BiConsumer<T, Double> writeFunction, MapFunction<T, Double> readFunction){
            paramTable.addParam(name, defaultValue, writeFunction, readFunction);
            return this;
        }

        public FunctionDefenition<T> addStringParam(String name, String defaultValue, BiConsumer<T, String> writeFunction, MapFunction<T, String> readFunction){
            paramTable.addStringParam(name, defaultValue, writeFunction, readFunction);
            return this;
        }

        public FunctionDefenition update(){
            paramTable.update();
            return this;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private class ParamTable<T extends GraphFunction> extends VisTable {

        private T function;
        private Consumer<T> changeListener;
        private Array<ValueReader> readers = new Array<>();
        private Array<VisTextField> fields = new Array<>();

        public ParamTable(T function) {
            this.function = function;
            right();
            defaults().padBottom(15);
        }

        public ParamTable<T> addParam(String name, float defaultValue, BiConsumer<T, Double> writeFunction, MapFunction<T, Double> readFunction){
            VisLabel label = new VisLabel(name, Color.BLACK);
            VisTextField field = new VisValidatableTextField(new Validators.FloatValidator());
            field.setUserObject(name);
            fields.add(field);
            field.setText(String.valueOf(defaultValue));
            field.addChangeListener(() -> {
                String text = field.getText();
                double val = 0;
                try {
                    val = Double.parseDouble(text);
                } catch (NumberFormatException e) {}

                writeFunction.consume(function, val);
                if (changeListener != null){
                    changeListener.accept(function);
                }
            });
            field.setUserObject(name);
            readers.add(new ValueReader(field, readFunction));
            add(label).padRight(10);
            add(field).width(150);
            row();
            return this;
        }

        public ParamTable<T> addStringParam(String name, String defaultValue, BiConsumer<T, String> writeFunction, MapFunction<T, String> readFunction){
            VisLabel label = new VisLabel(name, Color.BLACK);
            VisTextField field = new VisTextField(defaultValue);
            field.setUserObject(name);
            fields.add(field);
            field.addChangeListener(() -> {
                String text = field.getText();
                writeFunction.consume(function, text);
                if (changeListener != null){
                    changeListener.accept(function);
                }
            });
            readers.add(new ValueReader(field, readFunction));
            add(label).padRight(10);
            add(field).width(150);
            row();
            return this;
        }

        ParamTable<T> onChanged(Consumer<T> consumer){
            changeListener = consumer;
            return this;
        }

        ParamTable<T> update(){
            readers.foreach(ValueReader::read);
            return this;
        }

        public VisTextField getField(String name){
            for (VisTextField field : fields) {
                if (name.equals(field.getUserObject())){
                    return field;
                }
            }
            return null;
        }

        public T getFunction() {
            return function;
        }

        private class ValueReader {
            VisTextField field;
            MapFunction<T, ?> readFunction;

            public ValueReader(VisTextField field, MapFunction<T, ?> readFunction) {
                this.field = field;
                this.readFunction = readFunction;
            }

            void read(){
                field.setText(String.valueOf(readFunction.map(function)));
            }
        }

    }

}
