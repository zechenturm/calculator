package vm;

import parser.FunctionSignature;

import java.util.ArrayList;
import java.util.HashMap;

public class CodeGen implements AbstractMachine
{
    private ArrayList<ByteCode> code = new ArrayList<>();
    private HashMap<Integer, Integer> labels = new HashMap<>();
    private final FunctionSignature[] builtins;

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
        code.add(new ByteCode(ByteCode.Type.LOAD_VALUE, value));
    }

    @Override
    public void load(int index) {
        code.add(new ByteCode(ByteCode.Type.LOAD, index));
    }

    @Override
    public void store(int index) {
        code.add(new ByteCode(ByteCode.Type.STORE, index));
    }

    @Override
    public void branchIfZero(int label) {
        code.add(new ByteCode(ByteCode.Type.BR_IF_0, label));
    }

    @Override
    public void label(int index) {
        labels.put(index, code.size());
    }

    @Override
    public void jump(int label) {
        code.add(new ByteCode(ByteCode.Type.JUMP, label));
    }

    @Override
    public void call(int index) {
        code.add(new ByteCode(ByteCode.Type.CALL, index));
    }

    public void add()
    {
        code.add(new ByteCode(ByteCode.Type.ADD, 0));
    }

    @Override
    public void subtract() {
        code.add(new ByteCode(ByteCode.Type.SUB, 0));
    }

    public void multiply()
    {
        code.add(new ByteCode(ByteCode.Type.MUL, 0));
    }

    public void divide()
    {
        code.add(new ByteCode(ByteCode.Type.DIV, 0));
    }

    @Override
    public FunctionSignature[] getBuiltinFunctions() {
        return builtins;
    }

    private static boolean isJump(ByteCode code)
    {
        return code.type == ByteCode.Type.JUMP || code.type == ByteCode.Type.BR_IF_0;
    }

    private void fixAddresses()
    {
        code.stream().filter(CodeGen::isJump).forEach( bc -> bc.data = labels.get(bc.data) );
    }

    public ByteCode[] generate()
    {
        fixAddresses();
        var a = new ByteCode[code.size()];
        return code.toArray(a);
    }
}
