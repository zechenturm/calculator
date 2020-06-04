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

        var w = new ByteCodeWriter(g.generate());
        var v = new VM(w.convert());
        assertEquals(result, v.execute());
    }

    @Test
    public void testSimplePrograms()
    {
        var w = new ByteCodeWriter(new ByteCode[]{new ByteCode(ByteCode.Type.LOAD_VALUE, 1)});
        var v = new VM(w.convert());
        assertEquals(1, v.execute());

        testScript("1+2", 3);

        testScript("2*3", 6);
    }
}
