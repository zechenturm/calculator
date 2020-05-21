package vm;

import java.util.ArrayList;
import java.util.Stack;

public class Interpreter implements VM
{
        Stack<Integer> stack = new Stack<>();
        ArrayList<Integer> vars = new ArrayList<>();

        private boolean ignore = false;

    public void add()
    {
        if (ignore) return;
        stack.push(stack.pop() + stack.pop());
    }

    public void sub()
    {
        if (ignore) return;
        var subtract = stack.pop();
        var subtractFrom = stack.pop();
        stack.push(subtractFrom - subtract);
    }

    public void mul()
    {
        if (ignore) return;
        stack.push(stack.pop() * stack.pop());
    }

    public void div()
    {
        if (ignore) return;
        var dividend = stack.pop();
        var divisor = stack.pop();
        stack.push(divisor / dividend);
    }

    public void push(int num)
    {
        if (ignore) return;
        stack.push(num);
    }

    @Override
    public int pop() {
        if (stack.isEmpty() || ignore)
            return 0;
        return stack.pop();
    }

    public void load(int index)
    {
        if (ignore) return;
        push(vars.get(index));
    }

    public void store(int index, int value)
    {
        if (ignore) return;
        try
        {
            vars.add(index, value);
        }
        catch (IndexOutOfBoundsException ioe)
        {
            vars.add(value);
        }
    }

    @Override
    public void branchIfZero(int offset) {
        if (stack.pop() == 0)
            ignore = true;
    }

    @Override
    public void label(int index) {
        ignore = false;
    }
}
