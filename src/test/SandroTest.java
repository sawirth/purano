package test;

public class SandroTest {
    private int field;
    private static int staticField;
    private Model model;

    public void modifyDirect(Model model) {
        model.a = 5;
    }

    public void modifyIndirect(Model model) {
        model.setA(5);
    }
}
