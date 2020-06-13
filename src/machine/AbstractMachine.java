package machine;

import parser.FunctionSignature;

public interface AbstractMachine {
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
    void call(int index);

    FunctionSignature[] getBuiltinFunctions();
}
