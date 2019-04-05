package ru.maklas.model.user_interface;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Consumer;
import com.kotcrab.vis.ui.util.Validators;
import com.kotcrab.vis.ui.widget.*;

public class ParetoView extends BaseStage {

    private final VisTextButton start;
    private final VisLabel functionDescription;
    private final VisValidatableTextField populationSizeField;
    private final VisValidatableTextField minField;
    private final VisValidatableTextField maxField;
    private final VisValidatableTextField generationMemoryField;
    private final VisValidatableTextField crossPointField;
    private final VisValidatableTextField bitMutMin;
    private final VisValidatableTextField bitMutMax;
    private final VisSlider mutationChanceSlider;
    private final VisLabel mutationChanceLabel;
    private final VisValidatableTextField bitsPerGeneField;
    private final VisValidatableTextField qField;
    private final VisLabel precisionLabel;
    private final VisCheckBox randomFunctionCheckBox;

    public ParetoView() {
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.align(Align.topLeft);
        table.pad(20);
        table.defaults().padBottom(10).padRight(5).left();
        addActor(table);

        start = new VisTextButton("Start");
        start.setColor(Color.GREEN);
        functionDescription = label("");
        populationSizeField = new VisValidatableTextField(new Validators.IntegerValidator());
        minField = new VisValidatableTextField(new Validators.FloatValidator());
        maxField = new VisValidatableTextField(new Validators.FloatValidator());
        minField.setAlignment(Align.center);
        maxField.setAlignment(Align.center);
        generationMemoryField = new VisValidatableTextField(new Validators.IntegerValidator());
        crossPointField = new VisValidatableTextField(new Validators.IntegerValidator());
        bitMutMin = new VisValidatableTextField(new Validators.IntegerValidator());
        bitMutMax = new VisValidatableTextField(new Validators.IntegerValidator());
        bitMutMin.setAlignment(Align.center);
        bitMutMax.setAlignment(Align.center);
        mutationChanceSlider = new VisSlider(0, 100, 1, false);
        mutationChanceLabel = label("100%");
        bitsPerGeneField = new VisValidatableTextField(new Validators.IntegerValidator());
        qField = new VisValidatableTextField(new Validators.FloatValidator());
        precisionLabel = label("");
        randomFunctionCheckBox = new VisCheckBox("", false);


        table.add(start).padBottom(35);
        table.row();

        table.add(label("Randomize functions"));
        table.add(randomFunctionCheckBox);
        table.row();

        table.add(label("Gene length")).right();
        table.add(inTable(t -> { t.add(bitsPerGeneField).width(75).padRight(10); t.add(precisionLabel);}));
        table.row();

        table.add(label("Population size")).right();
        table.add(populationSizeField).width(75);
        table.row();

        table.add(label("Min-Max value")).right();
        table.add(inTable(t -> { t.defaults().padRight(5); t.add(minField).width(75); t.add(label("-")); t.add(maxField).width(75).left();}));
        table.row();

        table.add(label("Generation memory")).right();
        table.add(generationMemoryField).width(75);
        table.row();

        table.add(label("Cross points")).right();
        table.add(crossPointField).width(75);
        table.row();

        VisTable mutationTable = new VisTable();
        mutationTable.defaults().padRight(10);
        mutationTable.add(bitMutMin).width(30).padRight(5);
        mutationTable.add(label("-")).padRight(5);
        mutationTable.add(bitMutMax).width(30).left();
        mutationTable.add(label("Chance:")).left();
        mutationTable.add(mutationChanceSlider).width(100).left();
        mutationTable.add(mutationChanceLabel).width(100).left();

        table.add(label("Bit mutations min-max")).right();
        table.add(mutationTable).left();
        table.row();

        table.add(label("Q")).right();
        table.add(qField).width(75);
        table.row();


        mutationChanceSlider.addChangeLsitener(c -> mutationChanceLabel.setText(String.valueOf(c.intValue())));
    }

    private VisLabel label(String text){
        return new VisLabel(text, Color.BLACK);
    }

    public void onStart(Runnable r){
        start.addChangeListener(r);
    }

    public void setFunctionDescription(String s){
        functionDescription.setText(s);
    }

    public void setPopulationSize(int size){
        populationSizeField.setText(String.valueOf(size));
    }

    public int getPopulationSize(){
        return getInt(populationSizeField);
    }

    public void setMinMax(int min, int max){
        minField.setText(String.valueOf(min));
        maxField.setText(String.valueOf(max));
    }

    public float getMin(){
        return getFloat(minField);
    }

    public float getMax(){
        return getFloat(maxField);
    }

    public int getBitsPerGene(){
        return getInt(bitsPerGeneField);
    }

    public void setBitsPerGeneLen(int len){
        bitsPerGeneField.setText(String.valueOf(len));
    }

    public int getGenerationMemory(){
        return getInt(generationMemoryField);
    }

    public void setGenerationMemory(int val){
        generationMemoryField.setText(String.valueOf(val));
    }

    public int getCrossingPoints(){
        return getInt(crossPointField);
    }

    public void setCrossingPoints(int val){
        crossPointField.setText(String.valueOf(val));
    }

    public int getBitMutationMin(){
        return getInt(bitMutMin);
    }

    public int getBitMutationMax(){
        return getInt(bitMutMax);
    }

    public void setBitMutationMinMax(int min, int max){
        bitMutMin.setText(String.valueOf(min));
        bitMutMax.setText(String.valueOf(max));
    }

    public int getMutationChance(){
        return ((int) mutationChanceSlider.getValue());
    }

    public void setMutationChance(int chance){
        mutationChanceSlider.setValue(chance);
    }

    public double getQ(){
        return getFloat(qField);
    }

    public void setQ(double q){
        qField.setText(String.valueOf(q));
    }

    public void setPrecision(String s){
        precisionLabel.setText("Precision: " + s);
    }

    public void setRandomizeFunctions(boolean randomize){
        randomFunctionCheckBox.setChecked(randomize);
    }

    public boolean getRandomizeFunctions(){
        return randomFunctionCheckBox.isChecked();
    }

    private static int getInt(VisTextField field){
        try {
            return Integer.parseInt(field.getText().replaceAll("[^\\d-]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static float getFloat(VisTextField field){
        try {
            return Float.parseFloat(field.getText().replaceAll("[^\\d-.]", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }



    private VisTable inTable(Consumer<VisTable> t){
        VisTable table = new VisTable();
        t.accept(table);
        return table;
    }
}
