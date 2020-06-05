package vm;

import java.nio.ByteBuffer;
import java.util.Stack;

public class VM
{
    private final byte LOAD_VALUE = 1;
    private final byte LOAD = 2;
    private final byte STORE = 3;
    private final byte ADD = 4;
    private static final byte SUB = 5;
    private final byte MUL = 6;
    private static final byte DIV = 7;

    private int[] variables = new int[2];
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
                    advance(4);
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
                case STORE:
                    var index = codeBuffer.getInt(codeBuffer.position());
                    advance(4);
                    variables[index] = stack.pop();
                    break;
                case LOAD:
                    index = codeBuffer.getInt(codeBuffer.position());
                    advance(4);
                    stack.push(variables[index]);
                    break;

            }
        }

        if (stack.empty())
            return 0;

        return stack.pop();
    }

    private void advance(int num)
    {
        codeBuffer.position(codeBuffer.position() + num);
    }
}
