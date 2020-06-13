package machine;

public class DebugCode extends Interpreter {
    @Override
    public void add() {
        printIgnored();
        System.out.println("add");
        super.add();
    }

    @Override
    public void subtract() {
        printIgnored();
        System.out.println("sub");
        super.subtract();
    }

    @Override
    public void multiply() {
        printIgnored();
        System.out.println("mul");
        super.multiply();
    }

    @Override
    public void divide() {
        printIgnored();
        System.out.println("div");
        super.divide();
    }

    @Override
    public void loadValue(int value) {
        printIgnored();
        System.out.println("push " + value);
        super.loadValue(value);
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

    @Override
    public void jump(int label) {
        printIgnored();
        System.out.println("jmp " + label);
        super.jump(label);
    }

    private void printIgnored()
    {
        if (ignore != -1)
            System.out.print("!");
    }


}
