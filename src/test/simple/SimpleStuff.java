package test.simple;

import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;

import java.awt.*;
import java.io.File;

public class SimpleStuff {
    private int multiplier = 2;
    public int otherField = 3;
    public static int staticInt = 5;
    public static final int staticFinalInt = 100;
    public File file;


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

    public int dependOnArgument_FieldModifier(int a) {
        this.multiplier++;
        return a * 5;
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

    public int stateful_multiple_fields() {
        return this.multiplier * this.otherField;
    }

    public int stateful_multiple_fields_notThisButSameClass() {
        SimpleStuff ss = new SimpleStuff();
        return ss.otherField * ss.multiplier;
    }

    public int stateful_multiple_fields_otherClass() {
        Data d = new Data();
        d.a = 4;
        d.b = 4;

        return d.a * d.b;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
}
