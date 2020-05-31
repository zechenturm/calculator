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

    @Test
    public void testSimplePrograms()
    {
        var w = new ByteCodeWriter(new ByteCode[]{new ByteCode(ByteCode.Type.LOAD_VALUE, 1)});
        var v = new VM(w.convert());
        assertEquals(1, v.execute());

        w = new ByteCodeWriter(new ByteCode[]{new ByteCode(ByteCode.Type.LOAD_VALUE, 10)});
        v = new VM(w.convert());
        assertEquals(10, v.execute());

        var l = new Lexer("1+2");
        var g = new CodeGen();
        var p = new Parser(l, g);

        p.parse();

        w = new ByteCodeWriter(g.generate());
        v = new VM(w.convert());
        assertEquals(3, v.execute());
    }
}
