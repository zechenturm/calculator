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

    private static final char[] reservedChars = {';', '(', ')', '+', '-', '*', '/', '='};

    public Lexer(String input)
    {
        content = input;
    }


    private static class Pair
    {
        public Token token;
        public int index;

        public Pair(Token t, int i)
        {
            token = t;
            index = i;
        }
    }

    private static class Tuple extends Pair
    {
        public boolean lastTokenNumber;

        public Tuple(Token t, int i, boolean lastTokenNumber)
        {
            super(t, i);
            this.lastTokenNumber = lastTokenNumber;
        }

        public Tuple(Pair p, boolean lastTokenNumber)
        {
            this(p.token, p.index, lastTokenNumber);
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
        var i = isSingleCharToken(c);
        if (i != -1)
        {
            return handleSingleCharToken(textOffset, peekText, c);
        }

        if (c >= '0' && c <= '9')
        {
            var p = peekNumberToken(peekText);
            p.index += textOffset;
            return new Tuple(p, true);
        }

        i = isReserved(peekText);
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

    private int isSingleCharToken(char c)
    {
        for (int i = 0; i < reservedChars.length; i++)
            if (reservedChars[i] == c)
                return i;
        return -1;
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
        Pair p;
        if (c == '-')
        {
            var t = handleUnaryMinus(content);
            p = Objects.requireNonNullElseGet(t, () -> new Pair(new OperatorToken("-"), 1));
        }
        else
            p = new Pair(new OperatorToken(Character.toString(c)), 1);

        return new Tuple(p, false);
    }

    private Pair handleUnaryMinus(String content) {
        if (content.length() <= 1)
            return null;

        var c = content.charAt(1);
        if (!(c >= '0' && c <= '9' && !lastTokenNumber))
            return null;

        content = content.substring(1);
        var p = peekNumberToken(content);
        var t = (NumberToken) p.token;

        t.content = "-" + t.content;
        return new Pair(t, p.index+1);
    }

    private NumberToken getNumberToken() {
        var p = peekNumberToken(content);
        content = content.substring(p.index);
        return (NumberToken) p.token;
    }

    private Pair peekNumberToken(String content)
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
        return new Pair(t, index);
    }
}
