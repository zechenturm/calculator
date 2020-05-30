package vm;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

public class ByteCodeWriter
{
    HashMap<Integer, Integer> labels = new HashMap<>();
    ByteArrayOutputStream buffer = new ByteArrayOutputStream();

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
                labels.put(code.data, buffer.size());
                return new byte[0];
            case JUMP:
                bytes = new byte[5];
                var addr = labels.get(code.data);
                var data = ByteBuffer.allocate(4).putInt(addr).array();
                System.arraycopy(data, 0, bytes, 1, 4);
                break;
            default:
                bytes = new byte[5];
                data = ByteBuffer.allocate(4).putInt(code.data).array();
                System.arraycopy(data, 0, bytes, 1, 4);
        }

        bytes[0] = (byte) code.type.ordinal();
        return bytes;
    }

    public byte[] convert(ByteCode[] code)
    {
        for (var c : code)
            buffer.writeBytes(convert(c));
        return buffer.toByteArray();
    }
}
