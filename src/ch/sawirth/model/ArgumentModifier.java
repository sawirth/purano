package ch.sawirth.model;

public class ArgumentModifier {
    public final int argumentIndex;
    public final boolean hasDirectAccess;

    public ArgumentModifier(int argumentIndex, boolean hasDirectAccess) {
        this.argumentIndex = argumentIndex;
        this.hasDirectAccess = hasDirectAccess;
    }
}
