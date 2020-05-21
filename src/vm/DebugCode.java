package vm;

public class DebugCode extends Interpreter {
    @Override
    public void add() {
        printIgnored();
        System.out.println("add");
        super.add();
    }

    @Override
    public void sub() {
        printIgnored();
        System.out.println("sub");
        super.sub();
    }

    @Override
    public void mul() {
        printIgnored();
        System.out.println("mul");
        super.mul();
    }

    @Override
    public void div() {
        printIgnored();
        System.out.println("div");
        super.div();
    }

    @Override
    public void push(int value) {
        printIgnored();
        System.out.println("push " + value);
        super.push(value);
    }

    @Override
    public int pop() {
        printIgnored();
        var res = super.pop();
        System.out.println("pop " + res);
        return res;
    }

    @Override
    public void load(int index) {
        printIgnored();
        System.out.println("load " + index);
        super.load(index);
    }

    @Override
    public void store(int index) {
        printIgnored();
        System.out.println("store " + index);
        super.store(index);
    }

    @Override
    public void branchIfZero(int offset) {
        printIgnored();
        System.out.println("brIf0 " +  offset);
        super.branchIfZero(offset);
    }

    @Override
    public void label(int index) {
        System.out.println("label " +  index);
        super.label(index);
    }

    private void printIgnored()
    {
        if (ignore)
            System.out.print("!");
    }


}
