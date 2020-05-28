package cmdline;

import lexer.Lexer;
import parser.Parser;
import vm.Interpreter;

public class CmdLine {
    public static void main(String[] args)
    {
        var builder = new StringBuilder();
        for (var str : args)
        {
            builder.append(str);
        }
        var l = new Lexer(builder.toString());
        var i = new Interpreter();
        var p = new Parser(l, i);
        System.out.println(builder.toString());
        System.out.println(i.pop());
    }
}
