package vm;

import parser.FunctionSignature;

import java.util.ArrayList;

public class CodeGen implements AbstractMachine
{
    private ArrayList<ByteCode> code = new ArrayList<>();
    private final FunctionSignature[] builtins;

    private int stackTop = 0;
    private int stackSize = 0;

    public CodeGen(FunctionSignature[] builtinFunctions)
    {
        builtins = builtinFunctions;
    }

    public CodeGen()
    {
        builtins = new FunctionSignature[0];
    }

    public void loadValue(int value)
    {
        calcStackSize(1);
        code.add(new ByteCode(ByteCode.Type.LOAD_VALUE, value));
    }

    @Override
    public void load(int index)
    {
        calcStackSize(1);
        code.add(new ByteCode(ByteCode.Type.LOAD, index));
    }

    @Override
    public void store(int index) {
        calcStackSize(-1);
        code.add(new ByteCode(ByteCode.Type.STORE, index));
    }

    @Override
    public void branchIfZero(int label)
    {
        calcStackSize(-1);
        code.add(new ByteCode(ByteCode.Type.BR_IF_0, label));
    }

    @Override
    public void label(int index) {
        code.add(new ByteCode(ByteCode.Type.LABEL, index));
    }

    @Override
    public void jump(int label) {
        code.add(new ByteCode(ByteCode.Type.JUMP, label));
    }

    @Override
    public void call(int index) {
        calcStackSize(1 - builtins[index].numArgs);
        code.add(new ByteCode(ByteCode.Type.CALL, index));
    }

    public void add()
    {
        calcStackSize(-2);
        code.add(new ByteCode(ByteCode.Type.ADD, 0));
    }

    @Override
    public void subtract()
    {
        calcStackSize(-2);
        code.add(new ByteCode(ByteCode.Type.SUB, 0));
    }

    public void multiply()
    {
        calcStackSize(-2);
        code.add(new ByteCode(ByteCode.Type.MUL, 0));
    }

    public void divide()
    {
        calcStackSize(-2);
        code.add(new ByteCode(ByteCode.Type.DIV, 0));
    }

    @Override
    public FunctionSignature[] getBuiltinFunctions() {
        return builtins;
    }

    public ByteCode[] generate()
    {
        var a = new ByteCode[code.size()];
        return code.toArray(a);
    }

    public int getStackSize() {
        return stackSize;
    }

    private void calcStackSize(int change)
    {
        stackTop += change;
        stackSize = Math.max(stackSize, stackTop);
    }
}
