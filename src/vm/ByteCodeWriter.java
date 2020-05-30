package vm;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class ByteCodeWriter
{
    HashMap<Integer, Integer> labels = new HashMap<>();
    ArrayList<Integer> jumps = new ArrayList<>();
    ByteBuffer buffer;
    ByteCode[] code;

    public ByteCodeWriter(ByteCode[] code)
    {
        this.code = code;
        var size = getCodeSize(code);
        buffer = ByteBuffer.allocate(size);
    }

    private byte[] convert(ByteCode code)
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
                labels.put(code.data, buffer.position());
                return new byte[0];
            case JUMP:
            case BR_IF_0:
                bytes = new byte[5];
                var data = ByteBuffer.allocate(4).putInt(code.data).array();
                System.arraycopy(data, 0, bytes, 1, 4);
                jumps.add(buffer.position()+1);
                break;
            default:
                bytes = new byte[5];
                data = ByteBuffer.allocate(4).putInt(code.data).array();
                System.arraycopy(data, 0, bytes, 1, 4);
        }

        bytes[0] = (byte) code.type.ordinal();
        return bytes;
    }

    private int getCodeSize(ByteCode code)
    {
        return switch (code.type)
                {
                    case ADD, SUB, MUL, DIV -> 1;
                    case LABEL -> 0;
                    default -> 5;
                };
    }

    private int getCodeSize(ByteCode[] code)
    {
        int size = 0;
        for (var c : code)
            size += getCodeSize(c);
        return size;
    }

    private void fixJumps()
    {
        for (var addr : jumps)
        {
            var label = buffer.getInt(addr);
            var jumpAddr = labels.get(label);
            buffer.putInt(addr, jumpAddr);
        }
    }

    public byte[] convert()
    {
        for (var c : code)
            buffer.put(convert(c));
        fixJumps();
        return buffer.array();
    }
}
