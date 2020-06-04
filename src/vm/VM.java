package vm;

import java.nio.ByteBuffer;
import java.util.Stack;

public class VM
{
    private final byte LOAD_VALUE = 0;
    private final byte ADD = 3;

    private int returnCode = 0;
    private ByteBuffer codeBuffer;
    private Stack<Integer> stack = new Stack<>();

    public VM(byte[] code)
    {
        codeBuffer = ByteBuffer.allocate(code.length).put(code);
    }

    public int execute() {
        codeBuffer.rewind();
        while (codeBuffer.remaining() > 0)
        {
            byte currentCode = codeBuffer.get();
            switch (currentCode)
            {
                case LOAD_VALUE:
                    var value = codeBuffer.getInt(codeBuffer.position());
                    codeBuffer.position(codeBuffer.position() + 4);
                    stack.push(value);
                    break;
                case ADD:
                    var add1 = stack.pop();
                    var add2 = stack.pop();
                    stack.push(add1 + add2);
            }
        }

        if (stack.empty())
            return 0;

        return stack.pop();
    }
}
