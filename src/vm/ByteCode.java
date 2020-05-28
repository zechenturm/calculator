package vm;

public class ByteCode
{
    public enum Type
    {
        LOAD_VALUE,
        LOAD,
        STORE,
        ADD,
        SUB,
        MUL,
        DIV
    }

    public Type type;
    public int data;

    public ByteCode(Type t, int value)
    {
        type = t;
        data = value;
    }

    public ByteCode(Type t)
    {
        this(t, 0);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!(other instanceof ByteCode))
            return false;
        var bc = (ByteCode) other;
        return type == bc.type && data == bc.data;
    }

    @Override
    public String toString()
    {
        return "ByteCode<" + type + ", " + data + ">";
    }
}
