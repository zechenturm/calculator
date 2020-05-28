package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import vm.ByteCode;
import vm.CodeGen;

public class CodeGenTest
{
    private interface Generator
    {
        void generate(CodeGen cg);
    }

    private void testSingle(Generator g, ByteCode expectedResult)
    {
        var cg = new CodeGen();
        g.generate(cg);
        var b = cg.generate();
        assertEquals(expectedResult, b);
    }

    @Test
    public void testLoadValue()
    {
        testSingle(cg -> cg.loadValue(0), new ByteCode(ByteCode.Type.LOAD_VALUE, 0));
        testSingle(cg -> cg.loadValue(1), new ByteCode(ByteCode.Type.LOAD_VALUE, 1));
    }

    @Test
    public void testOperators()
    {
        testSingle(CodeGen::add, new ByteCode(ByteCode.Type.ADD));
        testSingle(CodeGen::subtract, new ByteCode(ByteCode.Type.SUB));
        testSingle(CodeGen::multiply, new ByteCode(ByteCode.Type.MUL));
        testSingle(CodeGen::divide, new ByteCode(ByteCode.Type.DIV));
    }
}
