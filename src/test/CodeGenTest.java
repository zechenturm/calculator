package test;

import lexer.Lexer;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import parser.Parser;
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
        assertEquals(expectedResult, b[0]);
    }

    private void testMultiple(Generator g, ByteCode ...expectedResult)
    {
        var cg = new CodeGen();
        g.generate(cg);
        var b = cg.generate();

        for (int i = 0; i < expectedResult.length; ++i)
        {
            assertEquals(expectedResult[i], b[i]);
        }
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

    @Test
    public void testMemoryAccess()
    {
        testSingle(cg -> cg.load(0), new ByteCode(ByteCode.Type.LOAD));
        testSingle(cg -> cg.load(1), new ByteCode(ByteCode.Type.LOAD, 1));

        testSingle(cg -> cg.store(0), new ByteCode(ByteCode.Type.STORE));
        testSingle(cg -> cg.store(1), new ByteCode(ByteCode.Type.STORE, 1));
    }

    @Test
    public void testJumps()
    {
        testMultiple(cg ->
            {
                cg.label(0);
                cg.jump(0);
            },
            new ByteCode(ByteCode.Type.JUMP, 0)
        );

        testMultiple(cg ->
                {
                    cg.label(1);
                    cg.branchIfZero(1);
                },
                new ByteCode(ByteCode.Type.BR_IF_0, 0)
        );

        testMultiple(cg ->
                {
                    cg.branchIfZero(1);
                    cg.loadValue(0);
                    cg.label(1);
                },
                new ByteCode(ByteCode.Type.BR_IF_0, 2),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 0),
                new ByteCode(ByteCode.Type.NOP)
        );

    }

    @Test
    public void testScripts()
    {
        testMultiple(cg -> {
            cg.loadValue(1);
            cg.loadValue(2);
            cg.add();
        },
                new ByteCode(ByteCode.Type.LOAD_VALUE, 1),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 2),
                new ByteCode(ByteCode.Type.ADD));

        testMultiple(cg -> {
            var l = new Lexer("1+2*3");
            var p = new Parser(l, cg);
            p.parse();
        },
                new ByteCode(ByteCode.Type.LOAD_VALUE, 1),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 2),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 3),
                new ByteCode(ByteCode.Type.MUL),
                new ByteCode(ByteCode.Type.ADD)
        );
    }
}
