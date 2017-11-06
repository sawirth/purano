package ch.sawirth.model;

public class FieldDependency {
    public final String name;
    public final String owner;
    public final String desc;
    public final boolean isThisField;
    public final boolean isDynamicEffect;

    public FieldDependency(String name, String owner, String type, boolean isThisField, boolean isDynamicEffect) {
        this.name = name;
        this.owner = owner;
        this.desc = type;
        this.isThisField = isThisField;
        this.isDynamicEffect = isDynamicEffect;
    }
}
