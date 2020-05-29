package parser;

import lexer.Lexer;
import token.*;
import vm.VM;

import java.util.ArrayList;

public class Parser
{
    private final VM vm;
    private final Lexer lexer;

    ArrayList<String> symbolTable = new ArrayList<>();

    private int labelIndex = 0;
    private final String[] builtins;

    public Parser(Lexer l, VM vm, String[] builtinFunctions)
    {
        lexer = l;
        this.vm = vm;
        builtins = builtinFunctions;
    }

    public Parser(Lexer l, VM vm)
    {
        this(l, vm, new String[0]);
    }

    public void parse()
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

        if(currentToken instanceof BuiltinFuncToken)
            handleBuiltinFuncToken(currentToken);

        if (currentToken instanceof ConditionalToken)
            handleConditional(currentToken);

        if (currentToken instanceof ParenToken)
            parse();

        if (currentToken instanceof OperatorToken)
            handleOperator(currentToken);
    }

    private void handleBuiltinFuncToken(Token currentToken)
    {
        int index = -1;
        for (var i = 0; i < builtins.length; i++)
            if (builtins[i].equals(currentToken.content))
            {
                index = i;
                break;
            }
        vm.call(index);
    }

    private void handleNumber(Token currentToken)
    {
        vm.loadValue(Integer.parseInt(currentToken.content));
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
                vm.subtract();
            }
            case "*" -> vm.multiply();
            case "/" -> vm.divide();
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
            parse();
            vm.store(varIndex);
        }
        else
            vm.load(lookup(currentToken.content));
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
