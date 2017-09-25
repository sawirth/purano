package test.inheritance;

public abstract class BaseClass {
    public int statelessInBase(int a, int b) {
        return (a + b) * 3 * (a - b);
    }

    public abstract int abstractMethodInBaseClass(int x);
}
