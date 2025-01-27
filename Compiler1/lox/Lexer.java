package Compiler1.lox;

import java.util.ArrayList;
import java.util.List;
import static Compiler1.lox.TokenType.*;

public class Lexer {
    private final String data;
    private final List<Token> tokens = new ArrayList<>();
    private int start;
    private int current;
    private int line;

    Lexer(String data){
        this.data = data;
    }

    private boolean isEnd(){
        return current >= data.length();
    }

    private char getNextChar(){
        return data.charAt(current++);
    }

    private char peek() {
        if (isEnd()) return '\0';
        return data.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= data.length()) return '\0';
        return data.charAt(current + 1 );
    }
    

    private void addToken(TokenType type, Object literal){
        String lexeme = data.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line) );
    }

    private void addToken(TokenType type){
        addToken(type, null);
    }

    private boolean match(char expected){
        if(isEnd()) return false;
        if(data.charAt(current) != expected) return false;

        current++;
        return true;
    }

    private void handleNumber(){
        while(isDigit(peek())) getNextChar();

        if(peek() == '.' && isDigit(peekNext())){
            getNextChar();
            while(isDigit(peek())) getNextChar();
        }

        addToken(NUMBER, Double.parseDouble(data.substring(start+1, current-1)));
    }

    private void handleString(){
        while(peek() != '"' && !isEnd()){
            if(peek() == '\n') line++;
            getNextChar();
        }

        if(isEnd()){
            Lox.error(line, "Unterminated string.");
            return;
        }
        getNextChar();
        String result = data.substring(start+1, current-1);
        addToken(STRING, result);
    }

    private void handleIdentifier() {
        while (isAlphaNumeric(peek())) getNextChar();
    
        addToken(IDENTIFIER);
      }

    private boolean isAlphaNumeric(char c) {
        return (c >= 'a' && c <= 'z') ||
               (c >= 'A' && c <= 'Z') ||
                c == '_';
      }

    private boolean isDigit(char c){
        return c >= '0' && c <= '9';
    }

    private void scanToken() {
        char c = getNextChar();
        switch (c) {
            case '(':addToken(LEFT_PAREN);break;
            case ')':addToken(RIGHT_PAREN);break;
            case '{':addToken(LEFT_BRACE);break;
            case '}':addToken(RIGHT_BRACE);break;
            case ',':addToken(COMMA);break;
            case '.':addToken(DOT);break;
            case '-':addToken(MINUS);break;
            case '+':addToken(PLUS);break;
            case ':':addToken(SEMICOLON);break;
            case '*':addToken(STAR);break;

            case '/':
                if(match('/')){
                    while (peek() != '\n' && !isEnd()) getNextChar();
                } else{
                    addToken(SLASH);
                }
                break;

            case '!':
                addToken(match('=')?NOT_EQUAL:NOT);
                break;
            
            case '=':
                addToken(match('=')?EQUAL_EQUAL:EQUAL);
                break;
            
            case '>':
                addToken(match('=')?GREATER_EQUAL:GREATER);
                break;

            case '<':
                addToken(match('=')?LESS_EQUAL:LESS);
                break;
            
            case '"':
                handleString();
                break;
            
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
                
            case '\n':
                line++;
                break;    
            default:
                if(isDigit(c)){
                    handleNumber();
                } else if(isAlphaNumeric(c)){
                    handleIdentifier();
                } else {
                    Lox.error(line, "Unexpected Character");
                }
                break;
        }
    }

    public List<Token> scanTokens() {
        while(!isEnd()){
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF, "",null,line));
        return tokens;
    }

}
