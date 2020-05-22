package parser;

import lexer.Lexer;
import token.*;
import vm.Interpreter;
import vm.VM;

import java.util.ArrayList;

public class Parser
{
    private final VM vm;
    private final Lexer lexer;

    ArrayList<String> symbolTable = new ArrayList<>();

    private int labelIndex = 0;

    public Parser(Lexer l, VM vm)
    {
        lexer = l;
        this.vm = vm;
    }

    public Parser(Lexer l)
    {
        this(l, new Interpreter());
    }

    private void evaluate()
    {
        while (true)
        {
            var currentToken = lexer.next();
            if (shouldBreak(currentToken))
                break;
            if (currentToken instanceof NumberToken)
                vm.push(Integer.parseInt(currentToken.content));

            if (currentToken instanceof IdentToken)
            {
                handleIdentifier(currentToken);
            }

            if (currentToken instanceof ConditionalToken)
            {
                handleConditional(currentToken);
            }

            if (currentToken instanceof OperatorToken)
            {
                if (handleOperator(currentToken)) break;
            }
        }
    }

    private boolean shouldBreak(Token currentToken)
    {
        return (currentToken instanceof EOFToken || currentToken instanceof EndStmtToken);
    }

    private boolean handleOperator(Token currentToken) {
        var t = lexer.next();
        if (t instanceof ParenToken)
        {
            if (t.content.equals(")"))
                return true;
            else
                evaluate();
        }
        else
            if (t instanceof NumberToken)
                vm.push(Integer.parseInt(t.content));
            else
                vm.load(lookup(t.content));

        switch (currentToken.content) {
            case "+":
                handlePunktvStrich();
                vm.add();
                break;
            case "-":
                handlePunktvStrich();
                vm.sub();
                break;
            case "*":
                vm.mul();
                break;
            case "/":
                vm.div();
        }
        return false;
    }

    private void handleConditional(Token currentToken) {
        if (currentToken.content.equals("if")) {
            var currentIndex = labelIndex++;
            evaluate();
            vm.branchIfZero(currentIndex);
            evaluate();
            vm.label(currentIndex);
        }
    }

    private void handleIdentifier(Token currentToken) {
        var nextToken = lexer.peek();
        if (nextToken instanceof AssignToken)
        {
            var varIndex = lookup(currentToken.content);
            evaluate();
            vm.store(varIndex);
        }
        else
            vm.load(lookup(currentToken.content));
    }

    public int eval()
    {
        evaluate();
        return vm.pop();
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
                evaluate();
            else
                vm.push(Integer.parseInt(t.content));

            if (tokenContent.equals("*"))
                vm.mul();
            else
                vm.div();
            tokenContent = lexer.peek().content;
        }
    }

    private int lookup(String name)
    {
        var index = symbolTable.indexOf(name);
        if (index == -1)
        {
            symbolTable.add(name);
            return symbolTable.size()-1;
        }
        return index;
    }
}
