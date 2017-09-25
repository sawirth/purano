package test.simple;

import java.awt.*;

public class SimpleStuff {
    private int multiplier = 2;
    private static int staticInt = 5;
    public static final int staticFinalInt = 100;


    /**
     * <b>Stateless</b><br>
     * Return value depends only on the argument 'a'
     * @param a
     * @return Two times the given int
     */
    public int stateless(int a) {
        return a * 2;
    }

    /**
     * <b>Stateful</b><br>
     * Return value depends on argument 'a' and the its own field 'multiplier (int)'
     * @param a
     * @return The multiplication of the multiplier with the given parameter
     */
    public int stateful(int a) {
        return a * this.multiplier;
    }

    public int impure(int a) {
        return a * this.multiplier++;
    }

    public int impure() {
        this.multiplier++;
        return 6;
    }

    public static int getStaticInt() {
        return staticInt;
    }

    /**
     * <b>Static modifier</b>
     * <br>
     * @param a
     * @return
     */
    public static int changeStaticInt(int a) {
        staticInt = a;
        return staticInt;
    }

    /**
     * <b>Stateful</b><br>
     * Return value depends on the field 'id' of the given parameter 'event'
     * @param event
     * @return
     */
    public int getFromObject(Event event) {
        return event.id * 5;
    }

    /**
     * <b>Stateless</b><br>
     * Return value depends only on the given parameter 'param'
     * @param param
     * @return
     */
    public int getFromParameterAndStaticFinal(int param) {
        return param * staticFinalInt;
    }

    /**
     * <b>Stateful</b><br>
     * Return value only depends on the field 'multiplier'
     * @return
     */
    public int getMultiplier() {
        return this.multiplier;
    }

    public int getStaticFinalIntFromOtherClass() {
        return Constants.STATIC_FINAL_INT;
    }

    /**
     * <b>Stateless</b>
     * <br> Return value depends on static field 'STATIC_INT' of class 'Constants'
     * @return int
     */
    public int getStaticIntFromOtherClass() {
        return Constants.STATIC_INT;
    }

    public int conditionalReturn(int a) {
        if (a > 10) {
            Constants.STATIC_INT = a;
        }

        return a;
    }
}
