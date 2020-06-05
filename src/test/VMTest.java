package test;

import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;
import vm.ByteCode;
import vm.ByteCodeWriter;
import vm.CodeGen;
import vm.VM;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VMTest
{
    @Test
    public void testCreate()
    {
        var vm = new VM(new byte[0]);
        var ret = vm.execute();
        assertEquals(0, ret);
    }

    private void testScript(String script, int result)
    {
        var l = new Lexer(script);
        var g = new CodeGen();
        var p = new Parser(l, g);

        p.parse();

        var code = g.generate();
        for (var c : code)
            System.out.println(c);
        var w = new ByteCodeWriter(code);
        var bytes = w.convert();
        for (var c : bytes)
            System.out.println(c);
        var v = new VM(bytes);
        assertEquals(result, v.execute());
    }

    @Test
    public void testSimplePrograms()
    {
        var w = new ByteCodeWriter(new ByteCode[]{new ByteCode(ByteCode.Type.LOAD_VALUE, 1)});
        var v = new VM(w.convert());
        assertEquals(1, v.execute());

        testScript("1+2", 3);
        testScript("1-2", -1);
        testScript("2*3", 6);
        testScript("6/3", 2);
    }

    @Test
    public void testVariables()
    {
        testScript("x = 1; x+1", 2);
        testScript("x = 1; y = 2; x", 1);
    }

    @Test
    public void testIf()
    {
        testScript("5 + if 1 2", 7);
        testScript("5 + if 1 2 else 3", 7);
        testScript("5 + if 0 2 else 3", 8);
    }
}
