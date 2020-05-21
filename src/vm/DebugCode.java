package vm;

public class DebugCode extends Interpreter {
    @Override
    public void add() {
        System.out.println("add");
        super.add();
    }

    @Override
    public void sub() {
        System.out.println("sub");
        super.sub();
    }

    @Override
    public void mul() {
        System.out.println("mul");
        super.mul();
    }

    @Override
    public void div() {
        System.out.println("div");
        super.div();
    }

    @Override
    public void push(int value) {
        System.out.println("push " + value);
        super.push(value);
    }

    @Override
    public int pop() {
        var res = super.pop();
        System.out.println("pop " + res);
        return res;
    }

    @Override
    public void load(int index) {
        System.out.println("load " + index);
        super.load(index);
    }

    @Override
    public void store(int index, int value) {
        System.out.println("store " + value + " at " + index);
        super.store(index, value);
    }

    @Override
    public void branchIfZero(int offset) {
        System.out.println("brIf0 " +  offset);
        super.branchIfZero(offset);
    }

    @Override
    public void label(int index) {
        System.out.println("label " +  index);
        super.label(index);
    }


}
