package vm;

public class CodeGen implements VM
{
    private ByteCode code;

    public void loadValue(int value)
    {
        code = new ByteCode(ByteCode.Type.LOAD_VALUE, value);
    }

    @Override
    public void load(int index) {
        code = new ByteCode(ByteCode.Type.LOAD, index);
    }

    @Override
    public void store(int index) {
        code = new ByteCode(ByteCode.Type.STORE, index);
    }

    @Override
    public void branchIfZero(int label) {

    }

    @Override
    public void label(int index) {

    }

    @Override
    public void jump(int label) {

    }

    public ByteCode generate()
    {
        return code;
    }

    public void add()
    {
        code = new ByteCode(ByteCode.Type.ADD, 0);
    }

    @Override
    public void subtract() {
        code = new ByteCode(ByteCode.Type.SUB, 0);
    }

    public void multiply()
    {
        code = new ByteCode(ByteCode.Type.MUL, 0);
    }

    public void divide()
    {
        code = new ByteCode(ByteCode.Type.DIV, 0);
    }
}
