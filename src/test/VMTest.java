package test;

import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.FunctionSignature;
import parser.Parser;
import vm.ByteCode;
import vm.ByteCodeWriter;
import vm.CodeGen;
import vm.VM;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

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
        var w = new ByteCodeWriter(code);
        var bytes = w.convert();
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

    private void testScript(String script, int result, InputStream in, OutputStream out, FunctionSignature[] builtins)
    {
        var l = new Lexer(script);
        var g = new CodeGen(builtins);
        var p = new Parser(l, g);

        p.parse();

        var code = g.generate();
        var w = new ByteCodeWriter(code);
        var bytes = w.convert();
        var v = new VM(bytes, in, out);

        assertEquals(result, v.execute());
    }

    @Test
    public void testBuiltins()
    {
        var in = new ByteArrayInputStream("100".getBytes());
        testScript(":in", 100, in, null, new FunctionSignature[]{
                new FunctionSignature("in", 0)
        });

        var out = new ByteArrayOutputStream();
        testScript(":out 100", 0, null, out, new FunctionSignature[]{
                new FunctionSignature("in", 0),
                new FunctionSignature("out", 1)
        });

        var output = out.toString();
        assertEquals("100", output);
    }
}
