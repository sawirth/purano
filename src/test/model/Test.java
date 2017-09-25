package test.model;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashSet;

public class Test {
    private ArrayList<String> someList;

    public String stateful_static_field() {
        this.someList.add("test");
        System.out.println("test");
        return someList.get(0);
    }

    public String stateless_static_field() {
        this.someList.add("test");
        System.out.println("test");
        return "hello";
    }

    public String statelessAndFieldAndStatic() {
        if (this.someList.size() == 0) {
            addToList("test");
            System.out.println("test");
        }

        return "hello";
    }

    private void addToList(String string) {
        this.someList.add(string);
    }

    public String StatelessAndStaticModifier(int a) {
        if (a < 5) {
            System.out.println("test");
            return "random";
        }

        return "something";
    }
}
