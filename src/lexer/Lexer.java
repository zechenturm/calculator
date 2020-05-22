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

    private static final TokenFactory reservedChars = new TokenFactory(
            new TokenFactory.Pair("+", () -> new OperatorToken("+")),
            new TokenFactory.Pair("-", () -> new OperatorToken("-")),
            new TokenFactory.Pair("*", () -> new OperatorToken("*")),
            new TokenFactory.Pair("/", () -> new OperatorToken("/")),
            new TokenFactory.Pair(";", EndStmtToken::new),
            new TokenFactory.Pair("(", () ->new ParenToken("(")),
            new TokenFactory.Pair(")", () ->new ParenToken(")")),
            new TokenFactory.Pair("=", () ->new AssignToken("="))
    );

    public Lexer(String input)
    {
        content = input;
    }

     private static class Tuple
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
        if (reservedChars.handle(peekText) != null)
        {
            return handleSingleCharToken(textOffset, peekText, c);
        }

        if (c >= '0' && c <= '9')
        {
            var p = peekNumberToken(peekText);
            p.index += textOffset;
            return p;
        }

        var i = isReserved(peekText);
        if (i != -1)
            return handleReserved(i, textOffset);

        var identEnd = getIdentifierEnd(peekText);
        return new Tuple(new IdentToken(peekText.substring(0, identEnd)), textOffset+identEnd, true);
    }

    private Tuple handleSingleCharToken(int textOffset, String peekText, char c) {
        if (isOperator(c))
        {
            var p = handleOperator(peekText, c);
            p.index += textOffset;
            return p;
        }
        if (c == ';')
            return new Tuple(new EndStmtToken(), textOffset+1, false);
        else if (c == '(')
            return new Tuple(new ParenToken("("), textOffset+1, false);
        else if (c == ')')
            return new Tuple(new ParenToken(")"), textOffset+1, false);
        else if (c == '=')
            return new Tuple(new AssignToken("="), textOffset+1, false);
        return null;
    }

    private Tuple handleReserved(int kwIndex, int textOffset) {
        var word = reservedWords[kwIndex];
        return new Tuple(new ConditionalToken(word), textOffset+word.length(), false);
    }

     private int isReserved(String peekText) {
        for (int i = 0; i < reservedWords.length; i++)
            if (peekText.startsWith(reservedWords[i]))
                return i;
        return -1;
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

    private boolean isOperator(char c)
    {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    private Tuple handleOperator(String content, char c)
    {
        Tuple p;
        if (c == '-')
        {
            var t = handleUnaryMinus(content);
            p = Objects.requireNonNullElseGet(t, () -> new Tuple(new OperatorToken("-"), 1, false));
        }
        else
            p = new Tuple(new OperatorToken(Character.toString(c)), 1, false);

        return p;
    }

    private Tuple handleUnaryMinus(String content) {
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

    private Tuple peekNumberToken(String content)
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
