package test;

import org.junit.jupiter.api.Test;
import vm.ByteCode;
import vm.ByteCodeWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteCodeWriterTest
{
    @Test
    public void testCreate()
    {
        var w = new ByteCodeWriter();
        byte[] b = w.convert(new ByteCode(ByteCode.Type.LOAD_VALUE));
    }

    private void testSingle(ByteCode code, byte[] bytes)
    {
        var w = new ByteCodeWriter();
        byte[] b = w.convert(code);
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
    }
}
