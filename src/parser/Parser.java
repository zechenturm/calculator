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
            if (currentToken instanceof EOFToken)
                break;
            if (currentToken instanceof NumberToken)
                vm.push(Integer.parseInt(currentToken.content));

            if (currentToken instanceof IdentToken)
            {
                var nextToken = lexer.peek();
                if (nextToken instanceof AssignToken)
                {
                    lexer.next();
                    var t = lexer.next();
                    vm.store(lookup(currentToken.content), Integer.parseInt(t.content));
                }
                else
                    vm.push(vm.load(lookup(currentToken.content)));
            }

            if (currentToken instanceof OperatorToken)
            {
                var t = lexer.next();
                if (t instanceof ParenToken)
                {
                    if (t.content.equals(")"))
                        break;
                    else
                        evaluate();
                }
                else
                    if (t instanceof NumberToken)
                        vm.push(Integer.parseInt(t.content));
                    else
                        vm.push(vm.load(lookup(t.content)));

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
            }
        }
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
