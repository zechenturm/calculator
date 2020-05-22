package lexer;

import token.*;

import java.util.Objects;

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
            new TokenFactory.Pair("-", Lexer::handleMinusToken),
            new TokenFactory.Pair("*", (t, l) -> nonNumberTokenToTuple(new OperatorToken("*"))),
            new TokenFactory.Pair("/", (t, l) -> nonNumberTokenToTuple(new OperatorToken("/"))),
            new TokenFactory.Pair(";", (t, l) -> new Tuple(new EndStmtToken(), 1, false)),
            new TokenFactory.Pair("(", (t, l) -> nonNumberTokenToTuple(new ParenToken("("))),
            new TokenFactory.Pair(")", (t, l) -> nonNumberTokenToTuple(new ParenToken(")"))),
            new TokenFactory.Pair("=", (t, l) -> nonNumberTokenToTuple(new AssignToken("="))),
            new TokenFactory.Pair("if", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("if"))),
            new TokenFactory.Pair("then", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("then"))),
            new TokenFactory.Pair("else", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("else"))),
            new TokenFactory.Pair("end", (t, l) -> nonNumberTokenToTuple(new ConditionalToken("end")))

    );

    private static Tuple nonNumberTokenToTuple(Token t)
    {
        return new Tuple(t, t.content.length(), false);
    }

    private static Tuple handleMinusToken(String text, boolean lastTokenNumber)
    {
        var t = handleUnaryMinus(text, lastTokenNumber);
        return Objects.requireNonNullElseGet(t, () -> new Tuple(new OperatorToken("-"), 1, false));
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

        var c = peekText.charAt(0);

        var t = reserved.handle(peekText, lastTokenNumber);
        if (t != null)
        {
            t.index += textOffset;
            return t;
        }

        if (c >= '0' && c <= '9')
        {
            var p = peekNumberToken(peekText);
            p.index += textOffset;
            return p;
        }

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

    private static Tuple handleUnaryMinus(String content, boolean lastTokenNumber)
    {
        if (content.length() <= 1)
            return null;

        var c = content.charAt(1);
        if (!(c >= '0' && c <= '9' && !lastTokenNumber))
            return null;

        content = content.substring(1);
        var p = peekNumberToken(content);
        var t = (NumberToken) p.token;

        t.content = "-" + t.content;
        return new Tuple(t, p.index+1, false);
    }

    private NumberToken getNumberToken() {
        var p = peekNumberToken(content);
        content = content.substring(p.index);
        return (NumberToken) p.token;
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
