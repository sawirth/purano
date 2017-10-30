package ch.sawirth.model;

import java.util.List;
import java.util.Set;

public class MethodRepresentation {
    public final String name;
    public final String purityType;
    public final List<MethodArgument> methodArguments;
    public final List<FieldModifier> fieldModifiers;
    public final List<FieldModifier> staticFieldModifiers;
    public final List<ArgumentModifier> argumentModifiers;
    public final ReturnDependency returnDependency;
    public final Set<NativeEffect> nativeEffects;

    public MethodRepresentation(String name,
                                String purityType,
                                List<MethodArgument> methodArguments,
                                List<FieldModifier> fieldModifiers,
                                List<FieldModifier> staticFieldModifiers,
                                List<ArgumentModifier> argumentModifiers,
                                ReturnDependency returnDependency,
                                Set<NativeEffect> nativeEffects) {
        this.name = name;
        this.purityType = purityType;
        this.methodArguments = methodArguments;
        this.fieldModifiers = fieldModifiers;
        this.staticFieldModifiers = staticFieldModifiers;
        this.argumentModifiers = argumentModifiers;
        this.returnDependency = returnDependency;
        this.nativeEffects = nativeEffects;
    }
}
