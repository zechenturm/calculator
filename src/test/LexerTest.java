package test;

import lexer.Lexer;
import token.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LexerTest {
    @Test
    public void testCreation()
    {
        var l = new Lexer("");
        var t = l.next();
        assertTrue(t instanceof EOFToken);
    }

    @Test
    public void testSingleDigit()
    {
        for (var num = 0; num < 10; ++num)
        {
            var numString = Integer.toString(num);
            var l = new Lexer(numString);
            var t = l.next();
            assertEquals(numString, t.content);
            assertTrue(t instanceof NumberToken);
        }
    }

    @Test
    public void testNegativeNumber()
    {
        for (var num = 0; num < 10; ++num)
        {
            var numString = Integer.toString(-num);
            var l = new Lexer(numString);
            var t = l.next();
            assertEquals(numString, t.content);
            assertTrue(t instanceof NumberToken);
        }
    }

    @Test
    public void testMultipleDigits()
    {
        for (var num = 0; num < 10; ++num)
        {
            var numString = Integer.toString((int)(Math.random()*1e6));
            var l = new Lexer(numString);
            var t = l.next();
            assertEquals(numString, t.content);
            assertTrue(t instanceof NumberToken);
        }
    }

    @Test
    public void testOperators()
    {
        String[] operators = {"+", "-", "*", "/"};
        for (var op : operators)
        {
            var l = new Lexer(op);
            var t = l.next();
            assertEquals(op, t.content);
            assertTrue(t instanceof OperatorToken);
        }
    }

    @Test
    public void testEOF()
    {
        var l = new Lexer("1");
        var t1 = l.next();
        var t2 = l.next();
        assertTrue(t2 instanceof EOFToken);
    }

    @Test
    public void test3Tokens()
    {
        class TestSet
        {
            public final String input;
            public final Token[] output;

            public TestSet( String in, Token... out)
            {
                input = in;
                output = out;
            }
        }


        TestSet[] tests =
        {
                new TestSet("+1", new OperatorToken("+"), new NumberToken("1"), new EOFToken()),
                new TestSet("1 + -1", new NumberToken("1"), new OperatorToken("+"), new NumberToken("-1"), new EOFToken()),
                new TestSet("1+1",new NumberToken("1"), new OperatorToken("+"), new NumberToken("1"), new EOFToken()),
                new TestSet("12 * 1456",new NumberToken("12"), new OperatorToken("*"), new NumberToken("1456"), new EOFToken()),
                new TestSet("4 * 5",new NumberToken("4"), new OperatorToken("*"), new NumberToken("5"), new EOFToken()),
                new TestSet("475 - 789",new NumberToken("475"), new OperatorToken("-"), new NumberToken("789"), new EOFToken()),
                new TestSet("475 -789",new NumberToken("475"), new OperatorToken("-"),  new NumberToken("789"), new EOFToken()),
                new TestSet("42/7",new NumberToken("42"), new OperatorToken("/"), new NumberToken("7"), new EOFToken()),
                new TestSet("42 /  7",new NumberToken("42"), new OperatorToken("/"), new NumberToken("7"), new EOFToken()),
                new TestSet("16 / 4 * 5",new NumberToken("16"), new OperatorToken("/"), new NumberToken("4"), new OperatorToken("*"), new NumberToken("5"), new EOFToken())

        };

        for (var set: tests)
        {
            var l = new Lexer(set.input);

            for (var t: set.output)
            {
                var n = l.next();
                assertEquals(t.content, n.content);
                assertEquals(t.getClass(), n.getClass());
            }
        }

    }

    @Test
    public void testPeek()
    {
        class TestSet
        {
            public final String input;
            public final Token[] output;

            public TestSet( String in, Token... out)
            {
                input = in;
                output = out;
            }
        }

        TestSet[] tests = {
                new TestSet("1 +", new NumberToken("1"), new OperatorToken("+")),
                new TestSet("1 + 2", new NumberToken("1"), new OperatorToken("+"), new NumberToken("2")),
                new TestSet("123 + 456", new NumberToken("123"), new OperatorToken("+"), new NumberToken("456")),
                new TestSet("123 + -456", new NumberToken("123"), new OperatorToken("+"), new NumberToken("-456")),
                new TestSet("123 - 456", new NumberToken("123"), new OperatorToken("-"), new NumberToken("456")),
                new TestSet("123 - -456", new NumberToken("123"), new OperatorToken("-"), new NumberToken("-456")),
                new TestSet("123 * 456", new NumberToken("123"), new OperatorToken("*"), new NumberToken("456")),
                new TestSet("123 / 456", new NumberToken("123"), new OperatorToken("/"), new NumberToken("456")),
                new TestSet("1 * (2+3 )", new NumberToken("1"), new OperatorToken("*"), new ParenToken("("), new NumberToken("2"), new OperatorToken("+"), new NumberToken("3"), new ParenToken(")"))
        };

        for (var set: tests)
        {
            var l = new Lexer(set.input);

            for (var t: set.output)
            {
                var n = l.peek();
                assertFalse(n.content.isEmpty());
                assertEquals(t.content, n.content);
                assertEquals(t.getClass(), n.getClass());
                l.next();
            }
            var t = l.peek();
            assertEquals(EOFToken.class, t.getClass());
        }
    }

    @Test
    public void testPeekThenNext()
    {
        var l = new Lexer("1 -3");
        l.next();
        l.peek();
        l.next();
        var t = l.next();
        assertEquals("3", t.content);
        assertEquals(NumberToken.class, t.getClass());
    }

    @Test
    public void testParens()
    {
        var l = new Lexer("(");
        var t = l.next();
        assertEquals(ParenToken.class, t.getClass());
        assertEquals(t.content, "(");

        l = new Lexer(")");
        t = l.next();
        assertEquals(ParenToken.class, t.getClass());
        assertEquals(t.content, ")");
    }

    @Test
    public void testAssign()
    {
        var l = new Lexer("x =");
        var t = l.next();
        assertEquals(IdentToken.class, t.getClass());
        assertEquals("x", t.content);
        t = l.next();
        assertEquals(AssignToken.class, t.getClass());

        l = new Lexer("abc= 123");
        t = l.next();
        assertEquals(IdentToken.class, t.getClass());
        assertEquals("abc", t.content);
        t = l.next();
        assertEquals(AssignToken.class, t.getClass());
        t = l.next();
        assertEquals(NumberToken.class, t.getClass());
        assertEquals("123", t.content);
    }

    @Test
    public void testIdentifier()
    {
        var l = new Lexer("x");
        var t = l.next();
        assertEquals(IdentToken.class, t.getClass());
        assertEquals("x", t.content);

        l = new Lexer("abc");
        t = l.next();
        assertEquals(IdentToken.class, t.getClass());
        assertEquals("abc", t.content);
    }

}
