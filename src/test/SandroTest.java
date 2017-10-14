package test;

public class SandroTest {
    public void modifyArgumentIndirect(int param, Model model) {
        model.setPrivateInt(param);
    }

    public static void modifyArgumentIndirectStatic(int param, Model model) {
        model.publicInt = param;
    }
}

