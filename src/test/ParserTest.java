package test;

import lexer.Lexer;
import org.junit.jupiter.api.Test;
import parser.Parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ParserTest
{
    @Test
    public void TestCreate()
    {
        var l = new Lexer("");
        var p = new Parser(l);
        var r = p.eval();
        assertEquals(0, r);
    }

    @Test
    public void TestSingleNumber()
    {
        for (var i = 0; i < 10; ++i)
        {
            var num = (int) (Math.random()*1e6);
            var l = new Lexer(Integer.toString(num));
            var p = new Parser(l);
            var r = p.eval();
            assertEquals(num, r);
        }
    }

    @Test
    public void testSimpleAdd()
    {
        var l = new Lexer("1+1");
        var p = new Parser(l);
        var r = p.eval();
        assertEquals(2, r);
        l = new Lexer("1+2");
        p = new Parser(l);
        r = p.eval();
        assertEquals(3, r);
    }

    @Test
    public void testSimpleSubtract()
    {
        var l = new Lexer("1-2");
        var p = new Parser(l);
        var r = p.eval();
        assertEquals(-1, r);
        l = new Lexer("1-5");
        p = new Parser(l);
        r = p.eval();
        assertEquals(-4, r);
    }

    @Test
    public void testSimpleMultiply()
    {
        var l = new Lexer("2*2");
        var p = new Parser(l);
        var r = p.eval();
        assertEquals(4, r);
        l = new Lexer("2*-5");
        p = new Parser(l);
        r = p.eval();
        assertEquals(-10, r);
    }

    @Test
    public void testSimpleDivide()
    {
        var l = new Lexer("6/2");
        var p = new Parser(l);
        var r = p.eval();
        assertEquals(3, r);
        l = new Lexer("10/-5");
        p = new Parser(l);
        r = p.eval();
        assertEquals(-2, r);
    }

    @Test
    public void testMoreComplexExressions()
    {
        var l = new Lexer("16 / 4 * 5");
        var p = new Parser(l);
        var r = p.eval();
        assertEquals(20, r);
    }

    @Test
    public void testOperatorPrecedence()
    {
        var l = new Lexer("4 + 16 / 4");
        var p = new Parser(l);
        var r = p.eval();
        assertEquals(8, r);

        l = new Lexer("5 + 16 / 4");
        p = new Parser(l);
        r = p.eval();
        assertEquals(9, r);

        l = new Lexer("4 + 16 / 4 / 2");
        p = new Parser(l);
        r = p.eval();

        assertEquals(6, r);

        l = new Lexer("4 + 16 / 4 * 3");
        p = new Parser(l);
        r = p.eval();

        assertEquals(16, r);

        l = new Lexer("4 + 9 * 4 / 3");
        p = new Parser(l);
        r = p.eval();

        assertEquals(16, r);

        l = new Lexer("4 - 9 * 4 / 3");
        p = new Parser(l);
        r = p.eval();

        assertEquals(-8, r);

        l = new Lexer("4 - 9 * 4 / 3 + 9 / 3");
        p = new Parser(l);
        r = p.eval();

        assertEquals(-5, r);

    }

    @Test
    public void testParens()
    {
        var l = new Lexer("1+(2+3)");
        var p = new Parser(l);
        assertEquals(6, p.eval());

        l = new Lexer("5*(2+3)");
        p = new Parser(l);
        assertEquals(25, p.eval());
    }
}
