package test;

import java.awt.*;

public class Test {
    private static int staticInt;

    public static void test(Rectangle rectangle) {
        first(rectangle);
    }

    private static void first(Rectangle rectangle) {
        second(rectangle);
    }

    private static void second (Rectangle rectangle) {
        rectangle.height = 100;
        callNative();
    }

    private static void callNative() {
        staticInt = 100;
    }
}
