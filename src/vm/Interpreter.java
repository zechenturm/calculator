package vm;

import java.util.ArrayList;
import java.util.Stack;

public class Interpreter implements VM
{
        Stack<Integer> stack = new Stack<>();
        ArrayList<Integer> vars = new ArrayList<>();

        protected int ignore = -1;

    public void add()
    {
        if (ignore > -1) return;
        stack.push(stack.pop() + stack.pop());
    }

    public void sub()
    {
        if (ignore > -1) return;
        var subtract = stack.pop();
        var subtractFrom = stack.pop();
        stack.push(subtractFrom - subtract);
    }

    public void mul()
    {
        if (ignore > -1) return;
        stack.push(stack.pop() * stack.pop());
    }

    public void div()
    {
        if (ignore > -1) return;
        var dividend = stack.pop();
        var divisor = stack.pop();
        stack.push(divisor / dividend);
    }

    public void push(int num)
    {
        if (ignore > -1) return;
        stack.push(num);
    }

    @Override
    public int pop() {
        if (stack.isEmpty() || ignore > -1)
            return 0;
        return stack.pop();
    }

    public void load(int index)
    {
        if (ignore > -1) return;
        push(vars.get(index));
    }

    public void store(int index)
    {
        if (ignore > -1) return;
        var value = stack.pop();
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
        if (ignore != -1)
            return;
        if (stack.pop() == 0)
            ignore = offset;
    }

    @Override
    public void label(int index) {
        if (ignore == index)
            ignore = -1;
    }
}
