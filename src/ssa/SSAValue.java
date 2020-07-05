package ssa;

import machine.ByteCode;

public class SSAValue
{
    public enum Location
    {
        STACK, VARIABLE
    }

    public ByteCode owner;
    public int index;
    public Location location;

    public SSAValue(ByteCode owner, Location location)
    {
        this.owner = owner;
        this.location = location;
    }
}
