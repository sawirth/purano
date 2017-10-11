package ch.sawirth.model;

import java.util.Set;

public class ReturnDependency {
    public final Set<FieldDependency> staticFieldDependencies;
    public final Set<FieldDependency> fieldDependencies;
    public final Set<Integer> indexOfDependentArguments;
    public final boolean dependsOnThis;

    public ReturnDependency(Set<FieldDependency> staticFieldDependencies,
                            Set<FieldDependency> fieldDependencies,
                            Set<Integer> indexOfDependentArguments,
                            boolean dependsOnThis) {
        this.staticFieldDependencies = staticFieldDependencies;
        this.fieldDependencies = fieldDependencies;
        this.indexOfDependentArguments = indexOfDependentArguments;
        this.dependsOnThis = dependsOnThis;
    }
}
