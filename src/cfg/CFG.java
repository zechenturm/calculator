package cfg;

import machine.ByteCode;

public class CFG
{
    private CFGNode root;

    public CFG(ByteCode[] code)
    {
        if (code.length == 0)
            return;

        root = splitFirst(code);
    }

    private CFGNode splitFirst(ByteCode[] code)
    {
        var index = findNextJump(code);

        var newBlock = new ByteCode[index];
        System.arraycopy(code, 0, newBlock, 0, index);
        var firstNode = new CFGNode(newBlock);

        var secondBlock = new ByteCode[code.length - index];
        System.arraycopy(code, index, secondBlock, 0, secondBlock.length);
        firstNode.next = new CFGNode(secondBlock);

        return firstNode;
    }

    private int findNextJump(ByteCode[] code)
    {
        var index = 0;
        while ( index < code.length)
        {
            if (code[index].isJump())
            {
                ++index;
                break;
            }
            ++index;
        }
        return index;
    }

    public CFGNode root()
    {
        return root;
    }
}
