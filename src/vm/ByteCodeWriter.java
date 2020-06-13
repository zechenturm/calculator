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
    ArrayList<Integer> adresses = new ArrayList<>();
    ByteBuffer buffer;
    ByteCode[] code;
    int ramSize = 0;

    public ByteCodeWriter(ByteCode[] code)
    {
        this.code = code;
        var size = getCodeSize(code);
        buffer = ByteBuffer.allocate(size);
    }

    private byte[] convert(ByteCode code)
    {
        byte[] bytes;

        adresses.add(buffer.position());

        switch (code.type)
        {
            case NOP:
            case ADD:
            case SUB:
            case MUL:
            case DIV:
                bytes = new byte[1];
                break;
            case JUMP:
            case BR_IF_0:
                bytes = new byte[5];
                var data = ByteBuffer.allocate(4).putInt(code.data).array();
                System.arraycopy(data, 0, bytes, 1, 4);
                jumps.add(buffer.position()+1);
                break;
            case STORE:
                ramSize = code.data+1;
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
                    case NOP,ADD, SUB, MUL, DIV -> 1;
                    case LABEL -> 0;
                    default -> 5;
                };
    }

    private int getCodeSize(ByteCode[] code)
    {
        int size = 0;
        for (var c : code)
            size += getCodeSize(c);

        // check if last ByteCode is a label and insert an extra byte for NOP
        // otherwise jumoing to this label is out of bounds
        if ( code.length > 0 && code[code.length-1].type == ByteCode.Type.LABEL)
            ++size;
        return size;
    }

    private void fixJumps()
    {
        for (var addr : jumps)
        {
            var label = buffer.getInt(addr);
            var jumpAddr = adresses.get(label);
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

    public int getRamSize()
    {
        return ramSize;
    }
}
