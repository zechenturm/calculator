package vm;

import java.nio.ByteBuffer;
import java.util.Stack;

public class VM
{
    private final byte LOAD_VALUE = 0;
    private final byte ADD = 3;
    private static final byte SUB = 4;
    private final byte MUL = 5;
    private static final byte DIV = 6;

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
                    break;
                case SUB:
                    var sub1 = stack.pop();
                    var sub2 = stack.pop();
                    stack.push(sub2 - sub1);
                    break;
                case MUL:
                    var mul1 = stack.pop();
                    var mul2 = stack.pop();
                    stack.push(mul1 * mul2);
                    break;
                case DIV:
                    var div1 = stack.pop();
                    var div2 = stack.pop();
                    stack.push(div2 / div1);
                    break;
            }
        }

        if (stack.empty())
            return 0;

        return stack.pop();
    }
}
