package ch.sawirth.model;

public class ArgumentModifier {
    public final int argumentIndex;
    public final boolean hasDirectAccess;
    public final boolean isDynamicEffect;
    public final String owner;
    public final String name;
    public final String originOwner;
    public final String originName;

    public ArgumentModifier(int argumentIndex,
                            boolean hasDirectAccess,
                            boolean isDynamicEffect,
                            String owner,
                            String name,
                            String originOwner,
                            String originName)
    {
        this.argumentIndex = argumentIndex;
        this.hasDirectAccess = hasDirectAccess;
        this.isDynamicEffect = isDynamicEffect;
        this.owner = owner;
        this.name = name;
        this.originOwner = originOwner;
        this.originName = originName;
    }
}
