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
            assertEquals(OperatorToken.class, t.getClass());
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
                new TestSet("16 / 4 * 5",new NumberToken("16"), new OperatorToken("/"), new NumberToken("4"), new OperatorToken("*"), new NumberToken("5"), new EOFToken()),
                new TestSet("x = 1 y = 2 x", new IdentToken("x"), new AssignToken("="), new NumberToken("1"), new IdentToken("y"), new AssignToken("="), new NumberToken("2"), new IdentToken("x")),
                new TestSet("1 + -1-1", new NumberToken("1"), new OperatorToken("+"), new NumberToken("-1"), new OperatorToken("-"), new NumberToken("1"), new EOFToken())

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
                new TestSet("1 * (2+3 )", new NumberToken("1"), new OperatorToken("*"), new ParenToken("("), new NumberToken("2"), new OperatorToken("+"), new NumberToken("3"), new ParenToken(")")),

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

    @Test
    public void testMultiline()
    {
        var l = new Lexer("1 +\n2");
        Token[] expectedTokens = {new NumberToken("1"),
                new OperatorToken("+"),
                new NumberToken("2"), new EOFToken()};

        for (var token : expectedTokens)
        {
            var t = l.next();
            assertEquals(token.getClass(), t.getClass());
            assertEquals(token.content, t.content);
        }
    }

    @Test
    public void testIf()
    {
        var l = new Lexer("if");
        var t = l.next();

        assertEquals(ConditionalToken.class, t.getClass());
        assertEquals("if", t.content);

        l = new Lexer("if 0 then 10 end");

        Token[] expectedTokens = {new ConditionalToken("if"),
                new NumberToken("0"),
                new ConditionalToken("then"),
                new NumberToken("10"),
                new ConditionalToken("end"),
                new EOFToken()};

        for (var token : expectedTokens)
        {
            t = l.next();
            assertEquals(token.content, t.content);
            assertEquals(token.getClass(), t.getClass());
        }

        l = new Lexer("x = 0 if 1 then x = 10 end x");

        expectedTokens = new Token[]{
                new IdentToken("x"),
                new AssignToken("="),
                new NumberToken("0"),
                new ConditionalToken("if"),
                new NumberToken("1"),
                new ConditionalToken("then"),
                new IdentToken("x"),
                new AssignToken("="),
                new NumberToken("10"),
                new ConditionalToken("end"),
                new IdentToken("x"),
                new EOFToken()};

        for (var token : expectedTokens)
        {
            t = l.next();
            assertEquals(token.content, t.content);
            assertEquals(token.getClass(), t.getClass());
        }
    }

    @Test
    public void testIfElse()
    {
        var l = new Lexer("if 0 then 10 else 2 end");

        Token[] expectedTokens = {new ConditionalToken("if"),
                new NumberToken("0"),
                new ConditionalToken("then"),
                new NumberToken("10"),
                new ConditionalToken("else"),
                new NumberToken("2"),
                new ConditionalToken("end"),
                new EOFToken()};

        for (var token : expectedTokens)
        {
            var t = l.next();
            assertEquals(token.content, t.content);
            assertEquals(token.getClass(), t.getClass());
        }
    }

    @Test
    public void testEndStmt()
    {
        var l = new Lexer(";");
        Token[] expectedTokens = {
                new EndStmtToken(),
                new EOFToken()
        };

        for (var token : expectedTokens)
        {
            var t = l.next();
            assertEquals(token.content, t.content);
            assertEquals(token.getClass(), t.getClass());
        }

        l = new Lexer("x = 1 + 1;");
        expectedTokens = new Token[] {
                new IdentToken("x"),
                new AssignToken("="),
                new NumberToken("1"),
                new OperatorToken("+"),
                new NumberToken("1"),
                new EndStmtToken(),
                new EOFToken()
        };

        for (var token : expectedTokens)
        {
            var t = l.next();
            assertEquals(token.content, t.content);
            assertEquals(token.getClass(), t.getClass());
        }
    }

    private void testTokens(String input, Token ...expectedTokens)
    {
        var l = new Lexer(input);
        for (var token : expectedTokens)
        {
            var t = l.next();
            assertEquals(token.content, t.content);
            assertEquals(token.getClass(), t.getClass());
        }
    }

    @Test
    public void testBuiltinFunctions()
    {
        testTokens(":in", new BuiltinFuncToken("in"));
        testTokens(":in + 1", new BuiltinFuncToken("in"),
                                 new OperatorToken("+"),
                                 new NumberToken("1"));

        testTokens(":out", new BuiltinFuncToken("out"));
        testTokens(":out 1", new BuiltinFuncToken("out"),
                                  new NumberToken("1"));

        testTokens(":mysuperawesomefunction", new BuiltinFuncToken("mysuperawesomefunction"));
    }

}
