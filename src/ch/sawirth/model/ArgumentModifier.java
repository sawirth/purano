package ch.sawirth.model;

public class ArgumentModifier {
    public final int argumentIndex;
    public final boolean hasDirectAccess;
    public final boolean isDynamicEffect;
    public final String owner;

    public ArgumentModifier(int argumentIndex, boolean hasDirectAccess, boolean isDynamicEffect) {
        this(argumentIndex, hasDirectAccess, isDynamicEffect, "");
    }

    public ArgumentModifier(int argumentIndex, boolean hasDirectAccess, boolean isDynamicEffect, String owner) {
        this.argumentIndex = argumentIndex;
        this.hasDirectAccess = hasDirectAccess;
        this.isDynamicEffect = isDynamicEffect;
        this.owner = owner;
    }
}
