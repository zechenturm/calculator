package ssa;

import cfg.CFG;

public class SSA
{
    private final SSANode root;

    public SSA(CFG cfg)
    {
        root = new SSANode(cfg.root());
    }

    public SSANode root()
    {
        return root;
    }
}
