package ch.sawirth.model;

import java.util.List;

public class MethodRepresentation {
    public final String name;
    public final String purityType;
    public final List<MethodArgument> methodArguments;

    public MethodRepresentation(String name,
                                String purityType,
                                List<MethodArgument> methodArguments) {
        this.name = name;
        this.purityType = purityType;
        this.methodArguments = methodArguments;
    }
}
