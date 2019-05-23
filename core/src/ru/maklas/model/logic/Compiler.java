package ru.maklas.model.logic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Colors;
import com.badlogic.gdx.utils.Array;
import org.apache.commons.lang3.StringUtils;
import org.mariuszgromada.math.mxparser.Expression;
import ru.maklas.model.functions.FunctionUtils;
import ru.maklas.model.logic.methods.MethodType;
import ru.maklas.model.logic.model.Equation;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Plot;
import ru.maklas.model.logic.model.Var;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {

    private static final Pattern numberPattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
    private static final Pattern wordPattern = Pattern.compile("[#a-zA-Z_]\\w*'?");
    private static final Pattern tokenizingPattern = Pattern.compile(wordPattern.pattern() + "|" + numberPattern.pattern() + "|\\[|]|;|\\*|/|\\+|-|=|,|\\(|\\)|\\^|\\.");
    private static final Pattern forbiddenSymbolsPattern = Pattern.compile("[^\\sa-zA-Z0-9\\-+.*/^',()\\\\_;=\\[\\]]");
    private static final Array<String> headers = Array.with("program", "var", "vars", "equations", "params");
    private static final Array<String> methods = new Array<>();
    static {
        for (MethodType value : MethodType.values()) {
            methods.addAll(value.getCodes());
        }
    }

    public static Model compile(String text) throws EvaluationException {
        Array<Token> tokens = tokenize(text);
        if (tokens.size == 0) throw new EvaluationException("Пустое тело программы");
        int lastHeaderIndex = getNextHeaderIndex(tokens, 0);
        if (lastHeaderIndex != 0) throw new EvaluationException("Программа должна начинаться с заголовка. Например: Program, Var, Equations, Params", tokens.first());

        Model model = new Model();
        model.setSource(text);
        int start = lastHeaderIndex + 1;
        int end = 0;
        Token header = tokens.get(lastHeaderIndex);
        Array<String> parsedHeaders = new Array<>();

        for (int i = lastHeaderIndex + 1; i < tokens.size; i++) {
            Token token = tokens.get(i);
            if (token.getType() == TokenType.header){
                end = i - 1;
                if (end < start){
                    throw new EvaluationException("Пустой заголовок", header);
                }
                parseHeader(header, tokens, start, end, model);
                parsedHeaders.add(header.getTextValue().toLowerCase());
                start = i + 1;
                header = token;
                if (parsedHeaders.contains(header.getTextValue().toLowerCase(), false)){
                    throw new EvaluationException("Заголовок " + header.getTextValue() + " Уже был указан", header);
                }
            }
        }
        end = tokens.size - 1;
        if (end < start){
            throw new EvaluationException("Пустой заголовок", header);
        }
        parseHeader(header, tokens, start, end, model);
        validateModel(model);

        return model;
    }

    private static void validateModel(Model model) throws EvaluationException {
        //VARS
        Array<Var> vars = model.getVars();
        for (int i = 0; i < vars.size; i++) {
            for (int j = 0; j < i; j++) {
                if (vars.get(i).getName().getTextValue().equals(vars.get(j).getName().getTextValue())){
                    throw new EvaluationException("Переменная с именем '" + vars.get(i).getName().getTextValue() + "' Уже была объявлена", vars.get(i).getName());
                }
            }
        }

        for (Var var : vars) {
            try {
                var.getValue().getAsDouble();
            } catch (Exception e) {
                throw new EvaluationException("Значение переменной должно быть численным значением", var.getValue());
            }
        }

        //EQUATIONS
        Array<Equation> equations = model.getEquations();
        if (equations.isEmpty()) throw new EvaluationException("Функции не объявлены");
        for (int i = 0; i < equations.size; i++) {
            for (int j = 0; j < i; j++) {
                if (equations.get(i).getPureEquationName().equals(equations.get(j).getPureEquationName())){
                    throw new EvaluationException("Функция '" + equations.get(i).getName().getTextValue() + "' Уже была объявлена", equations.get(i).getName());
                }
            }
        }

        for (Equation equation : equations) {
            Token expressionToken = equation.getExpressionAsToken();
            String expressionString = expressionToken.getTextValue();
            Expression expression;
            expression = new Expression(expressionString);
            if (!expression.checkLexSyntax()){
                System.err.println(expression.getErrorMessage());
                Token errorToken;
                if (expression.getErrorMessage() != null && expression.getErrorMessage().contains("column")){
                    Matcher matcher = Pattern.compile("column (\\d+)\\.").matcher(expression.getErrorMessage());
                    if (matcher.find()) {

                        int start = Integer.parseInt(matcher.group(1)) + expressionToken.getStart() - 1;
                        int end = start + 1;
                        errorToken = new Token(TokenType.expression, expressionToken.getSourceLineOffset(), expressionToken.getLine(), expressionToken.getLineNumber(), start, end);
                    } else {
                        errorToken = expressionToken;
                    }
                } else {
                    errorToken = expressionToken;
                }
                throw new EvaluationException("Синтаксическая ошибка", errorToken);
            }

            validateExpression(expressionToken, expressionString);
            equation.setCompiledExpression(expression);
        }

        for (Equation equation : equations) {
            String[] variables = equation.getCompiledExpression().getMissingUserDefinedArguments();
            for (String variable : variables) {
                if (variable.equals("x")) continue;
                boolean found = false;
                for (int i = 0; i < equations.size; i++) {
                    if (variable.equals(equations.get(i).getPureEquationName())){
                        found = true;
                        break;
                    }
                }
                for (Var var : vars) {
                    if (variable.equals(var.getName().getTextValue())){
                        found = true;
                        break;
                    }
                }

                if (!found){
                    int index = equation.getFullEquation().indexOf(variable);
                    if (index > 0){
                        int start = index + equation.getExpressionAsToken().getStart();
                        throw new EvaluationException("Неизвестная переменная '" + variable + "'", new Token(TokenType.word, equation.getName().getSourceLineOffset(), equation.getName().getLine(), equation.getName().getLineNumber(), start, start + variable.length()));
                    } else {
                        throw new EvaluationException("\"Неизвестная переменная '" + variable + "'", equation.getExpressionAsToken());
                    }
                }
            }
        }

        for (Equation equation : equations) {
            for (Var var : vars) {
                if (var.getName().getTextValue().equals(equation.getPureEquationName())){
                    throw new EvaluationException("Функция имеет такое же название как одна из переменных", equation.getName());
                }
            }

        }

        //MODEL
        if (model.getMethod() == null){
            throw new EvaluationException("Не указан метод интегрирования. Укажите в Params", model.getParamToken());
        }
        if (!methods.contains(model.getMethod().getTextValue(), false)){
            throw new EvaluationException("Не известный метод интегрирования (" + model.getMethod().getTextValue() + "). Возможные методы: " + methods.toString(", "), model.getMethod());
        }
        //SPAN
        if (model.getSpanStart() == null || model.getSpanEnd() == null){
            throw new EvaluationException("Не указан диапозон интегрирования. укажите диапозон (span) в Params", model.getParamToken());
        }
        double spanStart;
        double spanEnd;
        try {
            spanStart = model.getSpanStart().getAsDouble();
        } catch (Exception e) {
            throw new EvaluationException("Некорректное начало диапозона интегрирования", model.getSpanStart());
        }
        try {
            spanEnd = model.getSpanEnd().getAsDouble();
        } catch (Exception e) {
            throw new EvaluationException("Некорректный конец диапозона интегрирования", model.getSpanStart());
        }
        if (spanEnd <= spanStart){
            throw new EvaluationException("Конец диапозона интегрировнаия не должен быть раньше начала", model.getSpanEnd());
        }
        if (model.getStep() == null){
            throw new EvaluationException("Не указан шаг интегрирования. Укажите шаг (step) в Params", model.getParamToken());
        }
        //STEP
        double step;
        try {
            step = model.getStep().getAsDouble();
        } catch (Exception e) {
            throw new EvaluationException("Некорректный шаг интегрирования", model.getStep());
        }
        if (step <= 0){
            throw new EvaluationException("Шаг интегрирования должен быть положительным числом", model.getStep());
        }
        if (spanEnd - spanStart < step){
            throw new EvaluationException("Диапозон интегрирования должен быть больше шага интегрирования");
        }
        //STEP-ERROR
        if (model.getError() == null && MethodType.RKF.getCodes().contains(model.getMethod().getTextValue().toLowerCase(), false)){
            throw new EvaluationException("Для метода с плавающим шагом интегрирования необходимо указать погрешность (error) в Params", model.getParamToken());
        }
        //DEFAULTS
        if (model.getDefaults().size == 0){
            throw new EvaluationException("Не указаны начальные значения функций (x0) в Params", model.getParamToken());
        }
        if (model.getDefaults().size != model.getEquations().size){
            throw new EvaluationException("Начальный значения (x0) не совпадают с функциями");
        }
        for (Token aDefault : model.getDefaults()) {
            try {
                aDefault.getAsDouble();
            } catch (Exception e) {
                throw new EvaluationException("Начальное значение должно быть числом", aDefault);
            }
        }

        //PLOTS
        for (int i = 0; i < model.getPlots().size; i++) {
            Plot plot = model.getPlots().get(i);
            boolean found = false;
            for (Equation equation : equations) {
                if (equation.getPureEquationName().equals(plot.getFunctionName().getTextValue()) || equation.getName().getTextValue().equals(plot.getFunctionName().getTextValue())){
                    found = true;
                }
            }
            if (!found){
                throw new EvaluationException("Функция не найдена", plot.getFunctionName());
            }

            Token colorToken = plot.getColorToken();
            if (colorToken == null){
                plot.setColor(FunctionUtils.goodFunctionColor(i));
            } else if (colorToken.getTextValue().matches("#[0-9a-fA-F]{6}")){
                String red = colorToken.getTextValue().substring(1, 3);
                String green = colorToken.getTextValue().substring(3, 5);
                String blue = colorToken.getTextValue().substring(5, 7);
                plot.setColor(new Color(Integer.parseInt(red, 16)/ 256f, Integer.parseInt(green, 16)/ 256f, Integer.parseInt(blue, 16)/ 256f, 1));
            } else {
                Color color = Colors.get(colorToken.getTextValue());
                if (color == null){
                    throw new EvaluationException("Не известный цвет", colorToken);
                }
                plot.setColor(color);
            }
        }


    }

    private static void validateExpression(Token expressionToken, String expression) throws EvaluationException {
        Matcher matcher = Pattern.compile("[+\\-*/^][\\s]*([+*/^])").matcher(expression);
        if (matcher.find()){
            throw new EvaluationException("Синтаксическая ошибка. 2 знака подряд.", new Token(TokenType.word, expressionToken.getSourceLineOffset(), expressionToken.getLine(), expressionToken.getLineNumber(), expressionToken.getStart() + matcher.start(), expressionToken.getStart() + matcher.end()));
        }

    }

    private static void parseHeader(Token header, Array<Token> tokens, int start, int finish, Model model) throws EvaluationException {
        if ("program".equalsIgnoreCase(header.getTextValue())){
            Token name = tokens.get(start);
            if (name.getType() == TokenType.word) {
                model.setProgramName(name);
            } else if (name.getType() == TokenType.end){
                throw new EvaluationException("Введите название программы", header);
            } else {
                throw new EvaluationException("Не допустимое название для программы. Название программы может состоять из букв, цифр, нижнего подчёркивания. Но всегда начинаться с буквы", name);
            }
            if (finish > start + 1 && tokens.size > start + 2){
                throw new EvaluationException("Неожиданный токен ", tokens.get(start + 2));
            }

        } else if ("vars".equalsIgnoreCase(header.getTextValue()) || "var".equalsIgnoreCase(header.getTextValue())){
            if (tokens.get(start).getType() == TokenType.end){
                start++;
            }

            for (int i = start; i < finish + 1; i += 4) {
                if (tokens.get(i).getType() != TokenType.word){
                    expectToken(i, tokens, TokenType.word, "название переменной");
                }
                if (tokens.get(i + 1).getType() == TokenType.end){
                    throw new EvaluationException("Переменной не задано значение", tokens.get(i));
                }
                if (tokens.get(i + 1).getType() != TokenType.equals){
                    expectToken(i + 1, tokens, TokenType.equals, "=", tokens.get(i));
                }
                if (tokens.get(i + 2).getType() == TokenType.end){
                    throw new EvaluationException("Переменной не задано значение", tokens.get(i));
                }
                if (tokens.get(i + 2).getType() != TokenType.number){
                    expectToken(i + 2, tokens, TokenType.number, "число", tokens.get(i));
                }
                if (tokens.get(i + 3).getType() != TokenType.end){
                    expectToken(i + 3, tokens, TokenType.end, "; или ↵", tokens.get(i + 2));
                    throw EvaluationException.unexpected(tokens.get(i + 3));
                }

                Var var = new Var(tokens.get(i), tokens.get(i + 2));
                for (Var modelVar : model.getVars()) {
                    if (modelVar.getName().getTextValue().equals(var.getName().getTextValue())){
                        throw new EvaluationException("Повторяющееся название переменной", tokens.get(i));
                    }
                }

                model.getVars().add(var);
            }
        } else if ("equations".equalsIgnoreCase(header.getTextValue())){
            if (tokens.get(start).getType() == TokenType.end){
                start++;
            }

            Token name = null;
            Array<Token> expression = null;
            for (int i = start; i < finish; i++) {
                Token token = tokens.get(i);
                if (name == null){
                    name = token;
                    if (name.getType() != TokenType.word){
                        expectToken(i, tokens, TokenType.word, "название дифференциальной функции");
                        throw new EvaluationException("Некоректное название функции", token);
                    } else if (!name.getTextValue().endsWith("'")){
                        throw new EvaluationException("Функция должна быть дифференциалом первого порядка. Добавьте значок ' после названия функции", token);
                    }
                } else if (expression == null){
                    if (token.getType() == TokenType.equals){
                        expression = new Array<>();
                    } else {
                        throw new EvaluationException("Ожидалось '='", token);
                    }
                } else {
                    if (token.getType() == TokenType.end){
                        model.getEquations().add(new Equation(name, expression));
                        name = null;
                        expression = null;
                    } else {
                        expression.add(token);
                    }
                }
            }
            if (name != null && expression != null){
                model.getEquations().add(new Equation(name, expression));
            }
        } else if ("params".equalsIgnoreCase(header.getTextValue())){
            model.setParamToken(header);
            int i = start;
            while (i < finish){
                if (tokens.get(i).getType() == TokenType.end){
                    i++;
                }
                Token firstToken = tokens.get(i);
                if (firstToken.getType() != TokenType.word){
                    throw new EvaluationException("Неожиданный токен. Ожидалось название параметра", firstToken);
                }
                expectToken(i + 1, tokens, TokenType.equals, "'='", firstToken);
                String paramName = firstToken.getTextValue();
                int valueStart = i + 2;

                if ("method".equalsIgnoreCase(paramName)){
                    expectToken(valueStart, tokens, TokenType.word, "название метода интегрирования");
                    model.setMethod(tokens.get(valueStart));
                    i = valueStart + 1;
                } else if ("step".equalsIgnoreCase(paramName)){
                    expectToken(valueStart, tokens, TokenType.number, "число");
                    model.setStep(tokens.get(valueStart));
                    i = valueStart + 1;
                } else if ("error".equalsIgnoreCase(paramName)){
                    expectToken(valueStart, tokens, TokenType.number, "число");
                    model.setError(tokens.get(valueStart));
                    i = valueStart + 1;
                } else if ("span".equalsIgnoreCase(paramName)){
                    expectToken(valueStart, tokens, "[", "'['");
                    expectToken(valueStart + 1, tokens, TokenType.number, "начало диапозона");
                    expectToken(valueStart + 2, tokens, TokenType.comma, "','");
                    expectToken(valueStart + 3, tokens, TokenType.number, "конец диапозона");
                    expectToken(valueStart + 4, tokens, "]", "']'");
                    expectToken(valueStart + 5, tokens, TokenType.end, "Конец декларации " + paramName, tokens.get(valueStart + 4));
                    model.setSpanStart(tokens.get(valueStart + 1));
                    model.setSpanEnd(tokens.get(valueStart + 3));
                    i = valueStart + 5;
                } else if ("x0".equalsIgnoreCase(paramName)) {
                    expectToken(valueStart, tokens, "[", "'['");
                    if (tokens.size >= valueStart + 1 && "]".equals(tokens.get(valueStart + 1).getTextValue())){
                        if (tokens.size >= valueStart + 2 && TokenType.end == tokens.get(valueStart + 2).getType()){
                            i = valueStart + 4;
                        } else {
                            throw new EvaluationException("Неожиданный токен после декларации " + paramName, tokens.get(valueStart + 2));
                        }
                    } else {
                        int pos = valueStart + 1;
                        boolean expNumber = true;
                        while (true) {
                            if (tokens.size <= pos) {
                                throw new EvaluationException("Неожиданный токен, Не забудьте закрывать объявленные массивы символом ']'", tokens.last());
                            }
                            Token previous = tokens.get(pos - 1);
                            Token token = tokens.get(pos);
                            if (expNumber) {
                                if (token.getType() != TokenType.number) {
                                    expectToken(pos, tokens, TokenType.number, "число", previous);
                                }
                                model.getDefaults().add(token);
                            } else {
                                if ("]".equals(token.getTextValue())) {
                                    expectToken(pos + 1, tokens, TokenType.end, "конец объявления параметра " + paramName, token);
                                    i = pos + 2;
                                    break;
                                } else if (token.getType() != TokenType.comma) {
                                    expectToken(pos, tokens, TokenType.comma, "',' или ']'", previous);
                                }
                            }
                            expNumber = !expNumber;
                            pos++;
                        }
                    }
                } else if ("plot".equalsIgnoreCase(paramName)) {
                    expectToken(valueStart, tokens, "[", "'['");
                    if (tokens.size >= valueStart + 1 && "]".equals(tokens.get(valueStart + 1).getTextValue())){
                        if (tokens.size >= valueStart + 2 && TokenType.end == tokens.get(valueStart + 2).getType()){
                            i = valueStart + 4;
                        } else {
                            throw new EvaluationException("Неожиданный токен после объявления " + paramName, tokens.get(valueStart + 2));
                        }
                    } else {
                        int pos = valueStart + 1;
                        boolean expFun = true;
                        while (true){
                            if (tokens.size <= pos) {
                                throw new EvaluationException("Неожиданный токен, Не забудьте закрывать объявленные массивы символом ']'", tokens.last());
                            }
                            Token previous = tokens.get(pos - 1);
                            Token token = tokens.get(pos);
                            if (expFun) {
                                if (token.getType() != TokenType.word) {
                                    expectToken(pos, tokens, TokenType.word, "название функции", previous);
                                }
                                Token functionName = token;
                                Token color = null;
                                Token next = tokens.get(pos + 1);
                                if ("(".equals(next.getTextValue())){
                                    expectToken(pos + 2, tokens, TokenType.word, "цвет", next);
                                    expectToken(pos + 3, tokens, ")", ")", tokens.get(pos + 2));
                                    color = tokens.get(pos + 2);
                                    pos += 3;
                                }
                                model.getPlots().add(new Plot(functionName, color));
                            } else {
                                if ("]".equals(token.getTextValue())) {
                                    expectToken(pos + 1, tokens, TokenType.end, "Конец объявления параметра " + paramName, token);
                                    i = pos + 2;
                                    break;
                                } else if (token.getType() != TokenType.comma) {
                                    expectToken(pos, tokens, TokenType.comma, "',' или ']'", previous);
                                }
                            }
                            expFun = !expFun;
                            pos++;
                        }
                    }
                } else {
                    throw new EvaluationException("Не известный параметр '" + paramName + "'", firstToken);
                }
            }
        }
    }

    private static void expectToken(int index, Array<Token> tokens, TokenType type, String expectation) throws EvaluationException {
        Token previousToken = tokens.get(index - 1);
        if (previousToken == null) return;
        expectToken(index, tokens, type, expectation, previousToken);
    }
    private static void expectToken(int index, Array<Token> tokens, TokenType type, String expectation, Token previousToken) throws EvaluationException {
        if (tokens.size <= index){
            throw new EvaluationException("Строка не законченая. Ожидалось " + expectation, previousToken);
        } else {
            Token targetToken = tokens.get(index);
            if (targetToken.getType() != type){
                throw new EvaluationException((targetToken.getLength() == 1 ? "Неожиданный символ. " : "Неожиданный токен. ") + "Ожидалось " + expectation, targetToken);
            }
        }
    }

    private static void expectToken(int index, Array<Token> tokens, String text, String expectation) throws EvaluationException {
        Token previousToken = tokens.get(index - 1);
        if (previousToken == null) return;
        expectToken(index, tokens, text, expectation, previousToken);
    }
    private static void expectToken(int index, Array<Token> tokens, String text, String expectation, Token previousToken) throws EvaluationException {
        if (tokens.size <= index){
            throw new EvaluationException("Строка не законченая. Ожидалось " + expectation, previousToken);
        } else if (!text.equals(tokens.get(index).getTextValue())){
            throw new EvaluationException("Неизвестный " + (tokens.get(index).getLength() == 1 ? "символ" : "токен") +  ". Ожидалось " + expectation, tokens.get(index));
        }
    }

    private static int getNextHeaderIndex(Array<Token> tokens, int startSearch){
        for (int i = 0; i < tokens.size; i++) {
            Token token = tokens.get(i);
            if (token.getType() == TokenType.header){
                return i;
            }
        }
        return -1;
    }

    public static Array<Token> tokenize(String code) throws EvaluationException {
        if (code == null) return null;
        String[] lines = code.split("\r?\n");

        int lineOffset = 0;
        Array<Token> tokens = new Array<>();
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            int commentaryStart = StringUtils.indexOf(line, "//");
            if (commentaryStart >= 0){
                line = line.substring(0, commentaryStart);
            }
            if (StringUtils.isBlank(line)) {
                lineOffset += lines[lineIndex].length() + 1;
                continue;
            }
            Matcher matcher = forbiddenSymbolsPattern.matcher(line);
            if (matcher.find()){
                throw new EvaluationException("Запрещённый символ '" + matcher.group() + "'", new Token(TokenType.word, lineOffset, line, lineIndex + 1,  matcher.start(), matcher.end()));
            }
            tokenize(line, lineOffset, lineIndex + 1, tokens);
            lineOffset += lines[lineIndex].length() + 1;
        }
        return tokens;
    }

    /** Parse and add tokens**/
    private static void tokenize(String line, int sourceLineOffset, int lineNumber, Array<Token> tokens) throws EvaluationException {
        Matcher matcher = tokenizingPattern.matcher(line);

        while (matcher.find()){
            String group = matcher.group();
            TokenType type = null;
            switch (group){
                case "-":
                case "+":
                case "*":
                case "/":
                case "^":
                    type = TokenType.sign;
                    break;
                case "[":
                case "]":
                case "(":
                case ")":
                    type = TokenType.bracket;
                    break;
                case "=":
                    type = TokenType.equals;
                    break;
                case ";":
                    type = TokenType.end;
                    break;
                case ",":
                    type = TokenType.comma;
                    break;
                case ".":
                    throw EvaluationException.unexpected(new Token(TokenType.number, sourceLineOffset, line, lineNumber, matcher.start(), matcher.end()));
                default:
                    if (numberPattern.matcher(group).matches()){
                        type = TokenType.number;
                    } else if (wordPattern.matcher(group).matches()){
                        if (headers.contains(group.toLowerCase(), false)){
                            type = TokenType.header;
                        } else {
                            type = TokenType.word;
                        }
                    }
            }


            if (type == null){
                throw EvaluationException.invalidTokenException(group);
            }
            if (type == TokenType.end && tokens.size > 0 && tokens.last().getType() == TokenType.end) continue; //Скипаем два TokenType.end подряд
            Token token = new Token(type, sourceLineOffset, line, lineNumber, matcher.start(), matcher.end());

            tokens.add(token);
        }
        if (tokens.size > 0 && tokens.last().getType() != TokenType.end)
        tokens.add(new Token(TokenType.end, sourceLineOffset, line, lineNumber, line.length(), line.length()));
    }

}
