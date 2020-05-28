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
            handleToken(currentToken);
        }
    }

    private void handleToken(Token currentToken)
    {
        if (currentToken instanceof NumberToken)
            handleNumber(currentToken);

        if (currentToken instanceof IdentToken)
            handleIdentifier(currentToken);

        if (currentToken instanceof ConditionalToken)
            handleConditional(currentToken);

        if (currentToken instanceof ParenToken)
            evaluate();

        if (currentToken instanceof OperatorToken)
            handleOperator(currentToken);
    }

    private void handleNumber(Token currentToken)
    {
        vm.push(Integer.parseInt(currentToken.content));
    }

    private boolean shouldBreak(Token currentToken)
    {
        return (currentToken instanceof EOFToken || currentToken instanceof EndStmtToken || currentToken.content.equals(")"));
    }

    private void handleOperator(Token currentToken)
    {
        var t = lexer.next();
        handleToken(t);

        switch (currentToken.content)
        {
            case "+" ->
            {
                handlePunktvStrich();
                vm.add();
            }
            case "-" ->
            {
                handlePunktvStrich();
                vm.sub();
            }
            case "*" -> vm.mul();
            case "/" -> vm.div();
        }
    }

    private void handleConditional(Token currentToken)
    {
        if (currentToken.content.equals("if"))
        {
            var currentIndex = labelIndex++;
            handleToken(lexer.next());
            vm.branchIfZero(currentIndex);
            handleToken(lexer.next());
            var hasElse = lexer.peek().content.equals("else");
            var elseLabel = labelIndex++;
            if (hasElse)
                vm.jump(elseLabel);
            vm.label(currentIndex);
            if (hasElse)
            {
                lexer.next(); //consume "else" token we already peeked at
                handleToken(lexer.next());
                vm.label(elseLabel);
            }
        }
    }

    private void handleIdentifier(Token currentToken)
    {
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
            var t = lexer.next();
            handleToken(t);
            t = lexer.next();
            handleToken(t);
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
