package lexer;

import token.Token;

class TokenFactory {
    private final Pair[] pairs;


    public static class Pair
    {
        String content;
        TokenConstructor constructor;

        public Pair(String content, TokenConstructor constructor)
        {
            this.content = content;
            this.constructor = constructor;
        }
    }

    public TokenFactory(Pair ...pairs)
    {
        this.pairs = pairs;
    }

    public Tuple handle(String peekText, boolean lastTokenNumber) {
        for (Pair pair : pairs)
            if (peekText.startsWith(pair.content))
                return pair.constructor.construct(peekText, lastTokenNumber);
        return null;
    }
}
