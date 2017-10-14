package ch.sawirth.model.modifiers;

import ch.sawirth.model.FieldDependency;

import java.util.Set;

public class FieldModifier {
    public final String name;
    public final String type;
    public final boolean hasDirectAccess;
    public final Set<Integer> dependsOnParameterFromIndex;
    public final Set<FieldDependency> localFieldDependencies;
    public final Set<FieldDependency> staticFieldDependencies;

    public FieldModifier(String name,
                         String type,
                         boolean hasDirectAccess,
                         Set<Integer> dependsOnParameterFromIndex,
                         Set<FieldDependency> localFieldDependencies,
                         Set<FieldDependency> staticFieldDependencies)
    {
        this.name = name;
        this.type = type;
        this.hasDirectAccess = hasDirectAccess;
        this.dependsOnParameterFromIndex = dependsOnParameterFromIndex;
        this.localFieldDependencies = localFieldDependencies;
        this.staticFieldDependencies = staticFieldDependencies;
    }
}
