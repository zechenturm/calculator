package lexer;

import token.Token;

class Tuple
{
    public Token token;
    public int index;
    public boolean lastTokenNumber;

    public Tuple(Token t, int i, boolean lastTokenNumber)
    {
        token = t;
        index = i;
        this.lastTokenNumber = lastTokenNumber;
    }

    public Tuple plusOffset(int offset)
    {
        index += offset;
        return this;
    }
}