package cfg;

import machine.ByteCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class CFG
{
    private CFGNode root;
    private ByteCode[] code;
    private final TreeSet<Integer> splits = new TreeSet<>();
    private final ArrayList<CFGNode> nodes = new ArrayList<>();
    private final ArrayList<Edge> edges = new ArrayList<>();

    private static class Edge
    {
        public final int from, to;
        public Edge(int from, int to) { this.from = from; this.to = to; }
        public String toString() { return "Edge<" + from + " -> " + to + ">"; }
    }

    public CFG(ByteCode[] code)
    {
        if (code.length == 0)
            return;
        this.code = code;

        findSplits();
        createNodes();
        root = nodes.get(0);
        linkNodes();
    }

    private void findSplits()
    {
        for (var from = 0; from < code.length; ++from)
        {
            if (code[from].isJump())
            {
                var to = code[from].data - 1;
                splits.add(from);
                splits.add(to);
                edges.add(new Edge(from, to + 1));
            }
        }
    }

    private void createNodes()
    {
        splits.add(code.length - 1);
        var last = 0;
        for(var current : splits)
        {
            var size = current - last+1;
//            System.out.println("cur: " +  current + " last: " + last + " size: " + size);
            var node = new CFGNode(new ByteCode[size]);
            System.arraycopy(code, last, node.codeBlock(), 0, size);
            node.lastByteCodeIndex = current;
            nodes.add(node);
            last = ++current;
        }
    }

    private CFGNode indexToNode(int index)
    {
        for(var node : nodes)
        {
            if (node.lastByteCodeIndex >= index)
                return node;
        }
        return null;
    }

    private void linkNodes()
    {
        for ( var edge : edges)
        {
            var from = indexToNode(edge.from);
            from.next = indexToNode(edge.to);
        }
    }

    public CFGNode root()
    {
        return root;
    }
}
