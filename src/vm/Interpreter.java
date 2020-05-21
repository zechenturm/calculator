package vm;

import java.util.ArrayList;
import java.util.Stack;

public class Interpreter implements VM
{
        Stack<Integer> stack = new Stack<>();
        ArrayList<Integer> vars = new ArrayList<>();

    public void add()
    {
        stack.push(stack.pop() + stack.pop());
    }

    public void sub()
    {
        var subtract = stack.pop();
        var subtractFrom = stack.pop();
        stack.push(subtractFrom - subtract);
    }

    public void mul()
    {
        stack.push(stack.pop() * stack.pop());
    }

    public void div()
    {
        var dividend = stack.pop();
        var divisor = stack.pop();
        stack.push(divisor / dividend);
    }

    public void push(int num)
    {
        stack.push(num);
    }

    @Override
    public int pop() {
        if (stack.isEmpty())
            return 0;
        return stack.pop();
    }

    public void load(int index)
    {
        push(vars.get(index));
    }

    public void store(int index, int value)
    {
        try
        {
            vars.add(index, value);
        }
        catch (IndexOutOfBoundsException ioe)
        {
            vars.add(value);
        }
    }
}
