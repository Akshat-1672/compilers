package Compiler1.lox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {
    
    private static boolean hadError;
    
        public static void main(String[] args) throws IOException{
            if(args.length > 1){
                System.out.println("Usage: jlox [script]");
                System.exit(64); 
            }
            else if (args.length == 1){
                runUsingFilePath(args[0]);
            } else{
                runInteractive();
            }
        }
    
        private static void runUsingFilePath(String Path) throws IOException{
            byte[] bytes = Files.readAllBytes(Paths.get(Path));
            run(new String(bytes, Charset.defaultCharset()));
            if (hadError) System.exit(65);
        }
    
        private static void runInteractive() throws IOException{
            InputStreamReader input = new InputStreamReader(System.in);
            BufferedReader reader = new BufferedReader(input);
    
            for (;;) { 
                System.out.print("> ");
                String line = reader.readLine();
                if (line == null) break;
                run(line);
                hadError = false;
            }
        }
    
        private static void run(String data){
            Lexer lexer = new Lexer(data);
            List<Token> tokens = lexer.scanTokens();
    
            for(Token token:tokens){
                System.err.println(token);
            }
        }
    
        static void error(int line, String message) {
            report(line, "", message);
          }
        
          private static void report(int line, String where,
                                     String message) {
            System.err.println(
                "[line " + line + "] Error" + where + ": " + message);
            hadError = true;
      }
}