package vm;

public interface VM {
    void add();
    void sub();
    void mul();
    void div();
    void push(int value);
    int pop();
    int load(int index);
    void store(int index, int value);
}
