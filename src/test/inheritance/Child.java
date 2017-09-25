package test.inheritance;

public class Child extends BaseClass {
    private int value;

    @Override
    public int statelessInBase(int a, int b) {
        return this.value * (a - b);
    }

    @Override
    public int abstractMethodInBaseClass(int x) {
        return x * x * x;
    }
}
