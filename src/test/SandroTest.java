package test;

public class SandroTest {
    private int test;
    private static int staticField;
    private model model;

    public void modifyModel(int param) {
        this.model.field = param * staticField * this.test;
    }

    private class model {
        public int field;
    }
}

