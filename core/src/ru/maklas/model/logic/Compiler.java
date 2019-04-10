package ru.maklas.model.logic;

import com.badlogic.gdx.utils.Array;
import org.apache.commons.lang3.StringUtils;
import ru.maklas.model.logic.model.Equation;
import ru.maklas.model.logic.model.Model;
import ru.maklas.model.logic.model.Var;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Compiler {

    private static final Pattern numberPattern = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern wordPattern = Pattern.compile("[a-zA-Z_]\\w*");
    private static final Pattern tokenizingPattern = Pattern.compile(wordPattern.pattern() + "|" + numberPattern.pattern() + "|\\[|]|;|\\*|/|\\+|-|=|,|\\(|\\)|\\^");
    private static final Pattern forbiddenSymbolsPattern = Pattern.compile("[^\\s\\w\\-+.*/^,()\\\\_;=\\[]]");
    private static final Array<String> headers = Array.with("program", "var", "equations", "params");

    public static Model compile(String text) throws EvaluationException {
        Array<Token> tokens = tokenize(text);
        int lastHeaderIndex = getNextHeaderIndex(tokens, 0);
        if (lastHeaderIndex != 0) throw new EvaluationException("Program must start with a header. Ex: Program, Var, Equations, Params", tokens.first());

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
                    throw new EvaluationException("Header content is empty", header);
                }
                parseHeader(header, tokens, start, end, model);
                parsedHeaders.add(header.getTextValue().toLowerCase());
                start = i + 1;
                header = token;
                if (parsedHeaders.contains(header.getTextValue().toLowerCase(), false)){
                    throw new EvaluationException("Header " + header.getTextValue() + " was already declared", header);
                }
            }
        }
        end = tokens.size - 1;
        if (end < start){
            throw new EvaluationException("Header content is empty", header);
        }
        parseHeader(header, tokens, start, end, model);


        return model;
    }

    private static void parseHeader(Token header, Array<Token> tokens, int start, int end, Model model) throws EvaluationException {
        if ("program".equalsIgnoreCase(header.getTextValue())){
            Token name = tokens.get(start);
            if (name.getType() == TokenType.word) {
                model.setProgramName(name);
            } else {
                throw new EvaluationException("Invalid program name", name);
            }
            if (end > start + 1 && tokens.size > start + 2){
                throw new EvaluationException("Unexpected token ", tokens.get(start + 2));
            }

        } else if ("var".equalsIgnoreCase(header.getTextValue())){
            if (tokens.get(start).getType() != TokenType.end){
                throw new EvaluationException("Vars should be declared on the next line", header);
            }

            for (int i = start + 1; i < end + 1; i+=4) {
                if (tokens.get(i).getType() != TokenType.word){
                    throw EvaluationException.unexpected(tokens.get(i));
                }
                if (tokens.get(i + 1).getType() != TokenType.equals){
                    throw EvaluationException.unexpected(tokens.get(i + 1));
                }
                if (tokens.get(i + 2).getType() != TokenType.number){
                    throw EvaluationException.unexpected(tokens.get(i + 2));
                }
                if (tokens.get(i + 3).getType() != TokenType.end){
                    throw EvaluationException.unexpected(tokens.get(i + 3));
                }

                Var var = new Var(tokens.get(i), tokens.get(i + 2));
                for (Var modelVar : model.getVars()) {
                    if (modelVar.getName().getTextValue().equals(var.getName().getTextValue())){
                        throw new EvaluationException("Repeated variable name", tokens.get(i));
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
            for (int i = start; i < end; i++) {
                Token token = tokens.get(i);
                if (name == null){
                    name = token;
                    if (name.getType() != TokenType.word){
                        throw new EvaluationException("Expected equation name.", token);
                    }
                } else if (expression == null){
                    if (token.getType() == TokenType.equals){
                        expression = new Array<>();
                    } else {
                        throw new EvaluationException("Expected '='", token);
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
            int i = start;
            while (i < end){
                if (tokens.get(i).getType() == TokenType.end){
                    i++;
                }
                Token firstToken = tokens.get(i);
                if (firstToken.getType() != TokenType.word){
                    throw new EvaluationException("Unexpected token. Expected parameter name", firstToken);
                }
                expectToken(i + 1, tokens, TokenType.equals, "'='", firstToken);
                String paramName = firstToken.getTextValue();
                int valueStart = i + 2;

                if ("method".equalsIgnoreCase(paramName)){
                    expectToken(valueStart, tokens, TokenType.word, "method name", tokens.get(valueStart - 1));
                    model.setMethod(tokens.get(valueStart));
                    i = valueStart + 1;
                } else if ("step".equalsIgnoreCase(paramName)){
                    expectToken(valueStart, tokens, TokenType.number, "number", tokens.get(valueStart - 1));
                    model.setStep(tokens.get(valueStart));
                    i = valueStart + 1;
                } else if ("span".equalsIgnoreCase(paramName)){
                    expectToken(valueStart, tokens, "[", "'['", tokens.get(valueStart - 1));
                    expectToken(valueStart + 1, tokens, TokenType.number, "span start", tokens.get(valueStart - 1));
                    expectToken(valueStart + 2, tokens, TokenType.comma, "','", tokens.get(valueStart - 1));
                    expectToken(valueStart + 3, tokens, TokenType.number, "span end", tokens.get(valueStart - 1));
                    expectToken(valueStart + 4, tokens, "]", "']'", tokens.get(valueStart - 1));
                    model.setSpanStart(tokens.get(valueStart + 1));
                    model.setSpanEnd(tokens.get(valueStart + 3));
                    i = valueStart + 5;
                } else if ("x0".equalsIgnoreCase(paramName)) {
                    expectToken(valueStart, tokens, "[", "'['", tokens.get(valueStart - 1));
                    int pos = 1;
                    while (true){
                        if (tokens.size)
                    }
                } else {
                    throw new EvaluationException("Unexpected token", firstToken);
                }
            }
        }
    }

    private static void expectToken(int index, Array<Token> tokens, TokenType type, String expectation, Token previousToken) throws EvaluationException {
        if (tokens.size <= index){
            throw new EvaluationException("Line not finished. Expected: " + expectation, previousToken);
        } else if (tokens.get(index).getType() != type){
            throw new EvaluationException("Unexpected token. Expected: " + expectation, tokens.get(index));
        }
    }

    private static void expectToken(int index, Array<Token> tokens, String text, String expectation, Token previousToken) throws EvaluationException {
        if (tokens.size <= index){
            throw new EvaluationException("Line not finished. Expected: " + expectation, previousToken);
        } else if (!text.equals(tokens.get(index).getTextValue())){
            throw new EvaluationException("Unexpected token. Expected: " + expectation, tokens.get(index));
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

        Array<Token> tokens = new Array<>();
        for (int lineIndex = 0; lineIndex < lines.length; lineIndex++) {
            String line = lines[lineIndex];
            int commentaryStart = StringUtils.indexOf(line, "//");
            if (commentaryStart >= 0){
                line = line.substring(0, commentaryStart);
            }
            if (StringUtils.isBlank(line)) continue;
            Matcher matcher = forbiddenSymbolsPattern.matcher(line);
            if (matcher.find()){
                throw new EvaluationException("Invalid symbol at line " + (lineIndex + 1) + ", col " + matcher.start() +": '" + matcher.group() + "'");
            }
            tokenize(line, lineIndex + 1, tokens);
        }
        return tokens;
    }

    /** Parse and add tokens**/
    private static void tokenize(String line, int lineNumber, Array<Token> tokens) throws EvaluationException {
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
                default:
                    if (group.matches("-?\\d+(\\.\\d+)?")){
                        type = TokenType.number;
                    } else if (group.matches("[a-zA-Z]\\w*")){
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
            if (type == TokenType.end && tokens.size > 0 && tokens.last().getType() == TokenType.end) continue;
            Token token = new Token(type, line, lineNumber, matcher.start(), matcher.end());

            tokens.add(token);
        }
        if (tokens.size > 0 && tokens.last().getType() != TokenType.end)
        tokens.add(new Token(TokenType.end, line, lineNumber, line.length() - 1, line.length() - 1));
    }

}
