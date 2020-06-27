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


        var second = root.next.get(0);
        assertNotNull(second);

        assertArrayEquals(firstBlock, root.codeBlock());
        assertArrayEquals(secondBlock, second.codeBlock());

    }

    @Test
    void a_jump_skipping_instructions_produces_2_nodes_skipping_the_code_in_the_middle()
    {
        var bc = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP),
                new ByteCode(ByteCode.Type.JUMP, 3),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 1),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 2)
            };

        var firstBlock = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP),
                new ByteCode(ByteCode.Type.JUMP, 3),
            };

        var secondBlock = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.LOAD_VALUE, 2)

            };

        var cfg = new CFG(bc);
        var root = cfg.root();
        assertNotNull(root);


        var second = root.next.get(0);
        assertNotNull(second);

        assertArrayEquals(firstBlock, root.codeBlock());
        assertArrayEquals(secondBlock, second.codeBlock());
    }

    @Test
    void a_branch_if_0_will_create_a_node_with_2_children_for_when_the_branch_is_taken_and_for_when_it_isnt()
    {
        var bc = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP),
                new ByteCode(ByteCode.Type.BR_IF_0, 3),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 1),
                new ByteCode(ByteCode.Type.LOAD_VALUE, 2)
            };

        var conditionBlock = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP),
                new ByteCode(ByteCode.Type.BR_IF_0, 3),
            };

        var branchNotTakenBlock = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.LOAD_VALUE, 1)
            };

        var branchTakenBlock = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.LOAD_VALUE, 2)
            };

        var cfg = new CFG(bc);
        var root = cfg.root();
        assertNotNull(root);


        var branchNotTaken = root.next.get(0);
        assertNotNull(branchNotTaken);

        var branchTaken = root.next.get(1);
        assertNotNull(branchTaken);

        assertArrayEquals(conditionBlock, root.codeBlock());
        assertArrayEquals(branchNotTakenBlock, branchNotTaken.codeBlock());
        assertArrayEquals(branchTakenBlock, branchTaken.codeBlock());
        assertEquals(branchTaken, branchNotTaken.next.get(0));
    }
}
