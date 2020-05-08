package token;

public class Token
{
    public String content;

    public Token(String content)
    {
        this.content = content;
    }

    public String toString()
    {
        return this.getClass() + "<" + content + ">";
    }
}
