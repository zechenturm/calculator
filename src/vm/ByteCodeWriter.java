package vm;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class ByteCodeWriter
{
    public byte[] convert(ByteCode code)
    {
        byte[] bytes;

        switch (code.type)
        {
            case ADD:
            case SUB:
            case MUL:
            case DIV:
                bytes = new byte[1];
                break;
            case LABEL:
                return new byte[0];
            default:
                bytes = new byte[5];
                var data = ByteBuffer.allocate(4).putInt(code.data).array();
                System.arraycopy(data, 0, bytes, 1, 4);
        }

        bytes[0] = (byte) code.type.ordinal();
        return bytes;
    }

    public byte[] convert(ByteCode[] code)
    {
        var buffer = new ByteArrayOutputStream();
        for (var c : code)
            buffer.writeBytes(convert(c));
        return buffer.toByteArray();
    }
}
