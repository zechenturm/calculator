package vm;

public class CodeGen
{
    private int value;

    public void loadValue(int value)
    {
        this.value = value;
    }

    public ByteCode generate()
    {
        return new ByteCode(ByteCode.Type.LOAD_VALUE, value);
    }
}
