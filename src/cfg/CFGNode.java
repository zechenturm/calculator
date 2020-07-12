package cfg;

import machine.ByteCode;

import java.util.ArrayList;
import java.util.Arrays;

public class CFGNode
{
    public ByteCode[] code;

    public ArrayList<CFGNode> next = new ArrayList<>();
    public ArrayList<CFGNode> previous = new ArrayList<>();

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

    public boolean dominates(CFGNode other)
    {
        if (other == this) return true;
        if (next.contains(other)) return true;
        return false;
    }
}
