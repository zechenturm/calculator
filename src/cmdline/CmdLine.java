package cmdline;

import lexer.Lexer;
import parser.FunctionSignature;
import parser.Parser;
import machine.ByteCodeWriter;
import machine.CodeGen;
import machine.VM;

public class CmdLine {
    public static void main(String[] args)
    {
        var builder = new StringBuilder();
        for (var str : args)
        {
            builder.append(str);
        }
        var l = new Lexer(builder.toString());
        var cg = new CodeGen(new FunctionSignature[]{
                new FunctionSignature("in", 0),
                new FunctionSignature("out", 1)
        });
        var p = new Parser(l, cg);
        p.parse();
        var bcw = new ByteCodeWriter(cg.generate());
        var vm = new VM(bcw.convert(), System.in, System.out);
        System.out.println("VM returned: " + vm.execute());
    }
}
