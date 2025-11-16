import cordelmaisum.lexer.*;
import cordelmaisum.node.*;
import java.io.PushbackReader;

import java.io.FileReader;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            //String arquivo = "src/test/java/triangulo_existencia.cmu";
            String arquivo = "src/test/java/fibonacci.cmu";
            Lexer lexer =
                    new Lexer(
                            new PushbackReader(
                                    new FileReader(arquivo), 2048));
            Token token;
            while(!((token = lexer.next()) instanceof EOF)) {
                System.out.println(token.getClass());
                System.out.println(" ( "+ token +")");
            }
        }
        catch(Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}