package ch.sawirth.model;

import ch.sawirth.model.modifiers.FieldModifier;
import java.util.List;

public class MethodRepresentation {
    public final String name;
    public final String purityType;
    public final List<MethodArgument> methodArguments;
    public final List<FieldModifier> fieldModifiers;
    public final List<FieldModifier> staticFieldModifiers;

    public MethodRepresentation(String name,
                                String purityType,
                                List<MethodArgument> methodArguments,
                                List<FieldModifier> fieldModifiers,
                                List<FieldModifier> staticFieldModifiers) {
        this.name = name;
        this.purityType = purityType;
        this.methodArguments = methodArguments;
        this.fieldModifiers = fieldModifiers;
        this.staticFieldModifiers = staticFieldModifiers;
    }
}
