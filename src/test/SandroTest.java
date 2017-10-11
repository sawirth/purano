package test;

public class SandroTest {
    private int field;
    private static int staticField;

    public int stateless(int a) {
        return a * a;
    }

    public int stateful(int a) {
        return this.field * a;
    }

    public void fieldModifier(int a) {
        this.field = a;
    }

    public void staticModifier(int a) {
        staticField = a;
    }

    public void argumentModifier(Model model) {
        model.setA(5);
    }

    public void directModifier(Model model) {
        model.a = 5;
    }

    public int dependWithGetter(Model model) {
        return 5 * model.getA();
    }

    public int dependDirect(Model model) {
        return 2 * model.a;
    }
}
