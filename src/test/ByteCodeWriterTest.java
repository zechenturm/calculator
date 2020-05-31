package test;

import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;
import vm.ByteCode;
import vm.ByteCodeWriter;
import vm.CodeGen;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteCodeWriterTest
{
    @Test
    public void testCreate()
    {
        var w = new ByteCodeWriter(new ByteCode[]{new ByteCode(ByteCode.Type.LOAD_VALUE)});
        byte[] b = w.convert();
    }

    private void testSingle(ByteCode code, byte[] bytes)
    {
        var w = new ByteCodeWriter(new ByteCode[]{code});
        byte[] b = w.convert();
        assertEquals(bytes.length, b.length);
        for (int i = 0; i < bytes.length; ++i)
            assertEquals(bytes[i], b[i]);
    }

    private void testMultiple(ByteCode[] code, byte[] bytes)
    {
        var w = new ByteCodeWriter(code);
        byte[] b = w.convert();
        assertEquals(bytes.length, b.length);
        for (int i = 0; i < bytes.length; ++i)
            assertEquals(bytes[i], b[i]);
    }

    @Test
    public void testSingleCode()
    {
        testSingle(new ByteCode(ByteCode.Type.ADD), new byte[]{(byte) ByteCode.Type.ADD.ordinal()});
        testSingle(new ByteCode(ByteCode.Type.SUB), new byte[]{(byte) ByteCode.Type.SUB.ordinal()});
        testSingle(new ByteCode(ByteCode.Type.MUL), new byte[]{(byte) ByteCode.Type.MUL.ordinal()});
        testSingle(new ByteCode(ByteCode.Type.DIV), new byte[]{(byte) ByteCode.Type.DIV.ordinal()});

        testSingle(new ByteCode(ByteCode.Type.LOAD), new byte[]{(byte) ByteCode.Type.LOAD.ordinal(), 0, 0, 0, 0});
        testSingle(new ByteCode(ByteCode.Type.LOAD, 1), new byte[]{(byte) ByteCode.Type.LOAD.ordinal(), 0, 0, 0, 1});
        testSingle(new ByteCode(ByteCode.Type.LOAD, 256), new byte[]{(byte) ByteCode.Type.LOAD.ordinal(), 0, 0, 1, 0});
        testSingle(new ByteCode(ByteCode.Type.LOAD, 257), new byte[]{(byte) ByteCode.Type.LOAD.ordinal(), 0, 0, 1, 1});

        testSingle(new ByteCode(ByteCode.Type.LOAD_VALUE, 257), new byte[]{(byte) ByteCode.Type.LOAD_VALUE.ordinal(), 0, 0, 1, 1});
        testSingle(new ByteCode(ByteCode.Type.STORE, 256), new byte[]{(byte) ByteCode.Type.STORE.ordinal(), 0, 0, 1, 0});

        testSingle(new ByteCode(ByteCode.Type.LABEL, 0), new byte[0]);

        testSingle(new ByteCode(ByteCode.Type.CALL, 65540), new byte[]{(byte) ByteCode.Type.CALL.ordinal(), 0, 1, 0, 4});

    }

    @Test
    public void testMultipleCodes()
    {
        testMultiple(new ByteCode[]{new ByteCode(ByteCode.Type.ADD)}, new byte[]{(byte) ByteCode.Type.ADD.ordinal()});

        testMultiple(new ByteCode[]{
                new ByteCode(ByteCode.Type.ADD),
                new ByteCode(ByteCode.Type.SUB)},
                new byte[]{(byte) ByteCode.Type.ADD.ordinal(),
                           (byte) ByteCode.Type.SUB.ordinal()
                });

        testMultiple(new ByteCode[]{
                        new ByteCode(ByteCode.Type.LOAD_VALUE, 1),
                        new ByteCode(ByteCode.Type.LOAD_VALUE, 2),
                        new ByteCode(ByteCode.Type.ADD)},
                new byte[]{
                        (byte) ByteCode.Type.LOAD_VALUE.ordinal(), 0, 0, 0, 1,
                        (byte) ByteCode.Type.LOAD_VALUE.ordinal(), 0, 0, 0, 2,
                        (byte) ByteCode.Type.ADD.ordinal()
                });
    }

    @Test
    public void testJumps()
    {
        testMultiple(new ByteCode[]{
                        new ByteCode(ByteCode.Type.LABEL, 0),
                        new ByteCode(ByteCode.Type.JUMP, 0)},
                new byte[]{
                        (byte) ByteCode.Type.JUMP.ordinal(), 0, 0, 0, 0
                });

        testMultiple(new ByteCode[]{
                        new ByteCode(ByteCode.Type.LOAD_VALUE, 3),
                        new ByteCode(ByteCode.Type.LABEL, 0),
                        new ByteCode(ByteCode.Type.ADD),
                        new ByteCode(ByteCode.Type.JUMP, 0)},
                new byte[]{
                        (byte) ByteCode.Type.LOAD_VALUE.ordinal(), 0, 0, 0, 3,
                        (byte) ByteCode.Type.ADD.ordinal(),
                        (byte) ByteCode.Type.JUMP.ordinal(), 0, 0, 0, 5
                });

        testMultiple(new ByteCode[]{
                        new ByteCode(ByteCode.Type.JUMP, 0),
                        new ByteCode(ByteCode.Type.LOAD_VALUE, 3),
                        new ByteCode(ByteCode.Type.LABEL, 0),
                        new ByteCode(ByteCode.Type.ADD)},
                new byte[]{
                        (byte) ByteCode.Type.JUMP.ordinal(), 0, 0, 0, 10,
                        (byte) ByteCode.Type.LOAD_VALUE.ordinal(), 0, 0, 0, 3,
                        (byte) ByteCode.Type.ADD.ordinal(),
                });

        testMultiple(new ByteCode[]{
                        new ByteCode(ByteCode.Type.BR_IF_0, 0),
                        new ByteCode(ByteCode.Type.LOAD_VALUE, 3),
                        new ByteCode(ByteCode.Type.LABEL, 0),
                        new ByteCode(ByteCode.Type.ADD)},
                new byte[]{
                        (byte) ByteCode.Type.BR_IF_0.ordinal(), 0, 0, 0, 10,
                        (byte) ByteCode.Type.LOAD_VALUE.ordinal(), 0, 0, 0, 3,
                        (byte) ByteCode.Type.ADD.ordinal(),
                });
    }

    private ByteCodeWriter createWriter(String input)
    {
        var l = new Lexer(input);
        var g = new CodeGen();
        var p = new Parser(l, g);
        p.parse();
        return new ByteCodeWriter(g.generate());
    }

    @Test
    public void testRamSize()
    {
        var w = createWriter("");
        w.convert();
        assertEquals(0, w.getRamSize());

        w = createWriter("x = 0;");
        w.convert();
        assertEquals(1, w.getRamSize());

        w = createWriter("x = 0; y = 0;");
        w.convert();
        assertEquals(2, w.getRamSize());
    }
}
