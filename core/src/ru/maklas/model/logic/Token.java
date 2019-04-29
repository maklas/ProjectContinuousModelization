package ru.maklas.model.logic;

public class Token {

    private final TokenType type;
    private final String line;
    /** Start position of the LINE of this token in the source code **/
    private final int sourceLineOffset;
    /** Line number, relative to the source code. Starts from 1 **/
    private final int lineNumber;
    /** Start in it's line **/
    private final int start;
    /** End position in it's line **/
    private final int end;
    private String valueCache = null;

    public Token(TokenType type, int sourceLineOffset, String line, int lineNumber, int start, int end) {
        this.type = type;
        this.sourceLineOffset = sourceLineOffset;
        this.line = line;
        this.lineNumber = lineNumber;
        this.start = start;
        this.end = end;
    }

    public TokenType getType() {
        return type;
    }

    public String getLine() {
        return line;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getStart() {
        return start;
    }

    public int getLength(){
        return end - start;
    }

    public int getEnd() {
        return end;
    }

    public String getTextValue(){
        if (valueCache == null){
            try {
                valueCache = line.substring(start, end);
            } catch (Exception e) {
                valueCache = "";
            }
        }
        return valueCache;
    }

    @Override
    public String toString() {
        return getTextValue();
    }

    public boolean isOpenBracket(){
        String text = getTextValue();
        return "[".equals(text) || "(".equals(text);
    }

    public boolean isClosedBracket(){
        String text = getTextValue();
        return "]".equals(text) || ")".equals(text);
    }

    public double getAsDouble(){
        return Double.parseDouble(getTextValue());
    }

    public String position(){
        return "line " + lineNumber + ", column " + start;
    }

    public int getSourceLineOffset() {
        return sourceLineOffset;
    }

    public int getSourceOffset(){
        return sourceLineOffset + start;
    }
}
