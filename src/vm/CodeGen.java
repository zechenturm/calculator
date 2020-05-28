package vm;

public class CodeGen
{
    private ByteCode code;

    public void loadValue(int value)
    {
        code = new ByteCode(ByteCode.Type.LOAD_VALUE, value);
    }

    public ByteCode generate()
    {
        return code;
    }

    public void add()
    {
        code = new ByteCode(ByteCode.Type.ADD, 0);
    }

    public void sub()
    {
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
