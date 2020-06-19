package cfg;

import machine.ByteCode;

public class CFGNode
{
    private ByteCode[] code;

    public CFGNode next;

    public CFGNode(ByteCode[] code)
    {
        this.code = code;
    }

    public ByteCode[] codeBlock()
    {
        return code;
    }
}
