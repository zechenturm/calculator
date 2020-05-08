package vm;

public interface VM {
    void add();
    void sub();
    void mul();
    void div();
    void push(int value);
    int pop();
    int load(String name);
    void store(String name, int value);
}
