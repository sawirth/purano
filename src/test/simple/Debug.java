package test.simple;

import org.objectweb.asm.commons.Method;

import javax.naming.Context;

public class Debug {

    private int privateInt;
    private Context context;
    public final int publicFinalInt;
    private String string;

    public Debug(int privateInt, final int publicFinalInt) {
        this.privateInt = privateInt;
        this.publicFinalInt = publicFinalInt;
    }

    public int getPrivateInt() {
        return privateInt;
    }

    protected int protectedInt() {
        return 100;
    }

    public void callOtherMethod() {
        int test = protectedInt();
    }

    public void callMethodFromOtherClass(SimpleStuff ss) {
        int test = ss.conditionalReturn(5);
    }

    private int stateless_dependOnMultipleArguments(int first, int second) {
        return first * second;
    }

    public static int static_argument(int a) {
        return a * 5;
    }

    /**
     * <b>Modifies argument</b>
     * Calling this method changes the state of the argument 'ss' by calling its method 'setMultiplier'
     * @param ss
     */
    public void argumentModifier(SimpleStuff ss) {
        ss.setMultiplier(10);
    }

    public String dependOnString() {
        return this.string;
    }

    public boolean dependOnBoolean(boolean b) {
        return !b;
    }
}
