package ssa;

import cfg.CFGNode;
import machine.ByteCode;

import java.util.ArrayList;

public class SSANode
{
    public CFGNode cfgNode;
    public ArrayList<SSAValue> entrySet;
    public ArrayList<SSAValue> exitSet;

    public SSANode(CFGNode node)
    {
        cfgNode = node;
        entrySet = new ArrayList<>();
        exitSet = new ArrayList<>();

        var location = SSAValue.Location.STACK;
        if (modifiesVariable(cfgNode.code[0]))
            location = SSAValue.Location.VARIABLE;

        if (needsSSAValue(cfgNode.code[0]))
            exitSet.add(new SSAValue(cfgNode.code[0], location));
    }

    private boolean modifiesVariable(ByteCode byteCode)
    {
        return byteCode.type == ByteCode.Type.LOAD || byteCode.type == ByteCode.Type.STORE;
    }

    private boolean needsSSAValue(ByteCode code)
    {
        return switch (code.type)
            {
                case LOAD_VALUE, STORE -> true;
                default -> false;
            };
    }
}
