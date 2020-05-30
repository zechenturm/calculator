package vm;

import parser.FunctionSignature;

import java.util.ArrayList;
import java.util.Stack;

public class Interpreter implements AbstractMachine
{
        Stack<Integer> stack = new Stack<>();
        ArrayList<Integer> vars = new ArrayList<>();

        protected int ignore = -1;

    public void add()
    {
        if (ignore > -1) return;
        stack.push(stack.pop() + stack.pop());
    }

    public void subtract()
    {
        if (ignore > -1) return;
        var subtract = stack.pop();
        var subtractFrom = stack.pop();
        stack.push(subtractFrom - subtract);
    }

    public void multiply()
    {
        if (ignore > -1) return;
        stack.push(stack.pop() * stack.pop());
    }

    public void divide()
    {
        if (ignore > -1) return;
        var dividend = stack.pop();
        var divisor = stack.pop();
        stack.push(divisor / dividend);
    }

    public void loadValue(int num)
    {
        if (ignore > -1) return;
        stack.push(num);
    }

    public int pop() {
        if (stack.isEmpty() || ignore > -1)
            return 0;
        return stack.pop();
    }

    public void load(int index)
    {
        if (ignore > -1) return;
        loadValue(vars.get(index));
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

    @Override
    public void jump(int label)
    {
        if (ignore != -1)
            return;
        ignore = label;
    }

    @Override
    public void call(int index) {
        //ignore for now
    }

    @Override
    public FunctionSignature[] getBuiltinFunctions() {
        return new FunctionSignature[0];
    }
}
