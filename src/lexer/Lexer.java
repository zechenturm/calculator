package lexer;

import token.*;

public class Lexer {
    String content;
    boolean lastTokenNumber = false;
    private static final String[] reservedWords = {
      "if",
      "then",
      "else",
      "end"
    };

    private static final TokenFactory reserved = new TokenFactory(
            new TokenFactory.Pair("+", (t, l) -> nonNumberTokenToTuple(new OperatorToken("+"))),
            new TokenFactory.Pair("-", Lexer::handleMinus),
            new TokenFactory.Pair("*", (t, l) -> nonNumberTokenToTuple(new OperatorToken("*"))),
            new TokenFactory.Pair("/", (t, l) -> nonNumberTokenToTuple(new OperatorToken("/"))),
            new TokenFactory.Pair(";", (t, l) -> new Tuple(new EndStmtToken(), 1, false)),
            new TokenFactory.Pair("(", (t, l) -> nonNumberTokenToTuple(new ParenToken("("))),
            new TokenFactory.Pair(")", (t, l) -> nonNumberTokenToTuple(new ParenToken(")"))),
            new TokenFactory.Pair("=", (t, l) -> nonNumberTokenToTuple(new AssignToken("="))),
            new TokenFactory.Pair("if", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("if"))),
            new TokenFactory.Pair("then", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("then"))),
            new TokenFactory.Pair("else", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("else"))),
            new TokenFactory.Pair("end", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("end"))),
            new TokenFactory.Pair(":in", (t, l) -> nonNumberTokenToTuple(new BuiltinFuncToken("in")).plusOffset(1)),
            new TokenFactory.Pair(":out", (t, l) -> nonNumberTokenToTuple(new BuiltinFuncToken("out")).plusOffset(1))
    );

    private static Tuple nonNumberTokenToTuple(Token t)
    {
        return new Tuple(t, t.content.length(), false);
    }

    public Lexer(String input)
    {
        content = input;
    }

    public Token peek()
    {
        return peekInternal().token;
    }

    private Tuple peekInternal()
    {
        if (content.isEmpty())
            return new Tuple(new EOFToken(), 0, lastTokenNumber);

        int textOffset = getFirstNonSpace();
        var peekText = content.substring(textOffset);

        var t = reserved.handle(peekText, lastTokenNumber);
        if (t != null)
            return t.plusOffset(textOffset);

        var c = peekText.charAt(0);
        if (c >= '0' && c <= '9')
            return peekNumberToken(peekText).plusOffset(textOffset);

        var identEnd = getIdentifierEnd(peekText);
        return new Tuple(new IdentToken(peekText.substring(0, identEnd)), textOffset+identEnd, true);
    }

    private int getFirstNonSpace() {
        int index = 0;
        while (content.charAt(index) == ' ' || content.charAt(index) == '\n' )
            ++index;

        return index;
    }

    private int getIdentifierEnd(String content)
    {
        var index = 0;
        var c =content.charAt(index);
        try
        {
            while ( c >= 'a' && c <= 'z')
            {
                ++index;
                c = content.charAt(index);
            }
        }
        catch (StringIndexOutOfBoundsException ignored) {}
        return index;
    }

    public Token next()
    {
        var p = peekInternal();
        content = content.substring(p.index);
        lastTokenNumber = p.lastTokenNumber;
        return p.token;
    }

    private static Tuple handleMinus(String content, boolean lastTokenNumber)
    {
        if (content.length() <= 1)
            return new Tuple(new OperatorToken("-"), 1, false);

        var c = content.charAt(1);
        if (!(c >= '0' && c <= '9' && !lastTokenNumber))
            return new Tuple(new OperatorToken("-"), 1, false);

        content = content.substring(1);
        var p = peekNumberToken(content);
        var t = (NumberToken) p.token;

        t.content = "-" + t.content;
        return new Tuple(t, p.index+1, true);
    }

    private static Tuple peekNumberToken(String content)
    {
        int index = 0;
        while(index < content.length())
        {
            var c = content.charAt(index);
            if (c < '0' || c > '9')
                break;
            ++index;
        }

        var t = new NumberToken(content.substring(0, index));
        return new Tuple(t, index, true);
    }
}
