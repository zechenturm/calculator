package test;

import cfg.CFG;
import machine.ByteCode;
import org.junit.jupiter.api.Test;
import ssa.SSA;
import ssa.SSAValue;

import static org.junit.jupiter.api.Assertions.*;


public class SSATest
{
    @Test
    void every_ssa_node_corresponds_to_a_cfg_node()
    {
        var bc = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP)
            };
        var cfg = new CFG(bc);
        var ssa = new SSA(cfg);

        assertEquals(cfg.root(), ssa.root().cfgNode);
    }

    @Test
    void a_program_consisting_only_of_nop_creates_an_ssa_node_with_empty_entry_and_exit_sets()
    {
        var bc = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.NOP)
            };
        var cfg = new CFG(bc);
        var ssa = new SSA(cfg);

        assertEquals(0, ssa.root().entrySet.size());
        assertEquals(0, ssa.root().exitSet.size());
    }

    @Test
    void the_root_node_alsways_has_an_empty_entry_set()
     {
         var bc = new ByteCode[]
             {
                 new ByteCode(ByteCode.Type.NOP),
                 new ByteCode(ByteCode.Type.LOAD_VALUE, 10),
                 new ByteCode(ByteCode.Type.LOAD_VALUE, 20),
                 new ByteCode(ByteCode.Type.ADD)
             };
         var cfg = new CFG(bc);
         var ssa = new SSA(cfg);

         assertEquals(0, ssa.root().entrySet.size());
     }

    @Test
    void loading_a_constant_causes_that_constant_to_be_in_the_exitSet()
     {
         var bc = new ByteCode[]
             {
                 new ByteCode(ByteCode.Type.LOAD_VALUE, 10)
             };
         var cfg = new CFG(bc);
         var ssa = new SSA(cfg);

         assertEquals(1, ssa.root().exitSet.size());

         var orig = cfg.root().codeBlock()[0];
         var ssaVal = ssa.root().exitSet.get(0);

         assertEquals(orig, ssaVal.owner);
         assertEquals(0, ssaVal.index);
         assertEquals(SSAValue.Location.STACK, ssaVal.location);
     }

    @Test
    void storing_to_a_variable_causes_that_variable_to_be_in_the_exit_set()
    {
        var bc = new ByteCode[]
            {
                new ByteCode(ByteCode.Type.STORE, 10)
            };
        var cfg = new CFG(bc);
        var ssa = new SSA(cfg);

        assertEquals(1, ssa.root().exitSet.size());

        var orig = cfg.root().codeBlock()[0];
        var ssaVal = ssa.root().exitSet.get(0);

        assertEquals(orig, ssaVal.owner);
        assertEquals(0, ssaVal.index);
        assertEquals(SSAValue.Location.VARIABLE, ssaVal.location);
    }
}
