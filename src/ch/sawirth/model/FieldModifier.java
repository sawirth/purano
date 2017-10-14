package ch.sawirth.model;

import java.util.Set;

public class FieldModifier {
    public final String name;
    public final String type;
    public final String owner;
    public final boolean hasDirectAccess;
    public final Set<Integer> dependsOnParameterFromIndex;
    public final Set<FieldDependency> fieldDependencies;
    public final Set<FieldDependency> staticFieldDependencies;

    public FieldModifier(String name,
                         String type,
                         String owner, boolean hasDirectAccess,
                         Set<Integer> dependsOnParameterFromIndex,
                         Set<FieldDependency> fieldDependencies,
                         Set<FieldDependency> staticFieldDependencies)
    {
        this.name = name;
        this.type = type;
        this.owner = owner;
        this.hasDirectAccess = hasDirectAccess;
        this.dependsOnParameterFromIndex = dependsOnParameterFromIndex;
        this.fieldDependencies = fieldDependencies;
        this.staticFieldDependencies = staticFieldDependencies;
    }
}
