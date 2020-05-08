package vm;

import java.util.HashMap;
import java.util.Stack;

public class Interpreter implements VM
{
        Stack<Integer> stack = new Stack<>();
        HashMap<String, Integer> vars = new HashMap<>();

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

    public int load(String name)
    {
        return vars.get(name);
    }

    public void store(String name, int value)
    {
        vars.put(name, value);
    }
}
