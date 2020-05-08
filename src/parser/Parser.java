package parser;

import lexer.Lexer;
import token.EOFToken;
import token.NumberToken;
import token.OperatorToken;
import token.ParenToken;

import java.util.Stack;

public class Parser
{
    Stack<Integer> stack = new Stack<>();
    private final Lexer lexer;
    public Parser(Lexer l)
    {
        lexer = l;
    }

    public int eval()
    {
        while (true)
        {
            var currentToken = lexer.next();
            if (currentToken instanceof EOFToken)
                break;
            if (currentToken instanceof NumberToken)
                stack.push(Integer.parseInt(currentToken.content));

            if (currentToken instanceof OperatorToken)
            {
                var t = lexer.next();
                if (t instanceof ParenToken)
                {
                    if (t.content.equals(")"))
                        break;
                    else
                        stack.push(eval());
                }
                else
                    stack.push(Integer.parseInt(t.content));

                switch (currentToken.content) {
                    case "+":
                        handlePunktvStrich();
                        add();
                        break;
                    case "-":
                        handlePunktvStrich();
                        sub();
                        break;
                    case "*":
                        mul();
                        break;
                    case "/":
                        div();
                }
            }
        }
        if (stack.empty())
            return 0;
        return stack.pop();
    }

    private void handlePunktvStrich()
    {
        var tokenContent = lexer.peek().content;
        while (tokenContent.equals("/") || tokenContent.equals("*"))
        {
            //skip peeked token since we already looked at that
            lexer.next();

            var t = lexer.next();
            if (t.content.equals("("))
                stack.push(eval());
            else
                stack.push(Integer.parseInt(t.content));

            if (tokenContent.equals("*"))
                mul();
            else
                div();
            tokenContent = lexer.peek().content;
        }
    }

    private void add()
    {
        stack.push(stack.pop() + stack.pop());
    }

    private void sub()
    {
        var subtract = stack.pop();
        var subtractFrom = stack.pop();
        stack.push(subtractFrom - subtract);
    }

    private void mul()
    {
        stack.push(stack.pop() * stack.pop());
    }

    private void div()
    {
        var dividend = stack.pop();
        var divisor = stack.pop();
        stack.push(divisor / dividend);
    }
}
