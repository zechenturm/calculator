package test;

import cfg.CFG;
import machine.ByteCode;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CFGTest
{
    @Test
    void no_ByteCode_generates_empty_CFG()
    {
        var cfg = new CFG(new ByteCode[0]);
        assertNull(cfg.root());
    }

    @Test
    void single_ByteCode_generates_CFG_with_just_root_node_that_contains_the_ByteCode()
    {
        var bc = new ByteCode[]{ new ByteCode(ByteCode.Type.NOP) };
        var cfg = new CFG(bc);
        var root = cfg.root();
        assertNotNull(root);

        assertArrayEquals(root.codeBlock(), bc);
    }

    @Test
    void multiple_ByteCodes_that_are_not_branches_generate_a_single_node_containing_those_codes()
    {
        var bc = new ByteCode[]
        {
            new ByteCode(ByteCode.Type.NOP),
            new ByteCode(ByteCode.Type.NOP)
        };
        var cfg = new CFG(bc);
        var root = cfg.root();

        assertArrayEquals(root.codeBlock(), bc);
    }

    @Test
    void a_jump_to_the_next_instruction_splits_the_code_into_2_code_blocks()
    {
        var bc = new ByteCode[]
        {
            new ByteCode(ByteCode.Type.NOP),
            new ByteCode(ByteCode.Type.JUMP, 2),
            new ByteCode(ByteCode.Type.NOP),
            new ByteCode(ByteCode.Type.NOP)
        };

        var firstBlock = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP),
                new ByteCode(ByteCode.Type.JUMP, 2),
            };

        var secondBlock = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP),
                new ByteCode(ByteCode.Type.NOP)
            };

        var cfg = new CFG(bc);
        var root = cfg.root();
        assertNotNull(root);


        var second = root.next;
        assertNotNull(second);

        assertArrayEquals(firstBlock, root.codeBlock());
        assertArrayEquals(secondBlock, second.codeBlock());

    }
}
