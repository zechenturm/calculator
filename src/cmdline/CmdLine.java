package cmdline;

import lexer.Lexer;
import parser.Parser;

public class CmdLine {
    public static void main(String[] args)
    {
        var builder = new StringBuilder();
        for (var str : args)
        {
            builder.append(str);
        }
        var l = new Lexer(builder.toString());
        var p = new Parser(l);
        System.out.println(builder.toString());
        System.out.println(p.eval());
    }
}
