package ch.sawirth.model;

import java.util.Set;

public class ClassRepresentation {
    public final String fullName;
    public final Set<MethodRepresentation> methodMap;

    public ClassRepresentation(String fullName, Set<MethodRepresentation> methodMap) {
        this.fullName = fullName;
        this.methodMap = methodMap;
    }
}
