package vm;

public interface VM {
    void add();
    void subtract();
    void multiply();
    void divide();
    void loadValue(int value);
    void load(int index);
    void store(int index);
    void branchIfZero(int label);
    void label(int index);
    void jump (int label);
}
