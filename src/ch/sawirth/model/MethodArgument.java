package ch.sawirth.model;

public class MethodArgument {
    public final int position;
    public final String name;
    public final String type;

    public MethodArgument(int position, String name, String type) {
        this.position = position;
        this.name = name;
        this.type = type;
    }
}