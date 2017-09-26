package test.oneMethod;

import test.simple.SimpleStuff;

import java.io.File;

public class OneMethod {
    private boolean mybool;
    private static boolean ownStaticField;

    /**
     *
     * @param argument
     */
    public void changeStaticFieldBasedOnArgument(boolean argument) {
        OneMethod.ownStaticField = argument;
    }
}
