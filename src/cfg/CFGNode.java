package cfg;

import machine.ByteCode;

import java.util.Arrays;

public class CFGNode
{
    public ByteCode[] code;

    public CFGNode next;

    public int lastByteCodeIndex;

    public CFGNode(ByteCode[] code)
    {
        this.code = code;
    }

    public ByteCode[] codeBlock()
    {
        return code;
    }

    public String toString()
    {
        return "Node<" + lastByteCodeIndex + ", " + Arrays.toString(code) + ">";
    }
}
