package lexer;

import token.Token;

interface TokenConstructor
{
    Tuple construct(String text, boolean lastTokenNumber);
}