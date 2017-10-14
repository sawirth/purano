package ch.sawirth.utils;

import java.util.HashMap;
import java.util.Map;

public class JavaTypeNameUtils {
    private static final Map<String, String> primitiveTypeNamesByDescriptor;

    static {
        primitiveTypeNamesByDescriptor = new HashMap<>();
        primitiveTypeNamesByDescriptor.put("I", "int");
        primitiveTypeNamesByDescriptor.put("V", "void");
        primitiveTypeNamesByDescriptor.put("B", "byte");
        primitiveTypeNamesByDescriptor.put("C", "char");
        primitiveTypeNamesByDescriptor.put("D", "double");
        primitiveTypeNamesByDescriptor.put("F", "float");
        primitiveTypeNamesByDescriptor.put("J", "long");
        primitiveTypeNamesByDescriptor.put("S", "short");
        primitiveTypeNamesByDescriptor.put("Z", "boolean");
    }

    public static String convertToPrimitiveTypeNameIfNecessary(String descriptor) {
        String primitiveTypeName = primitiveTypeNamesByDescriptor.get(descriptor);
        if (primitiveTypeName != null) {
            return primitiveTypeName;
        }

        return descriptor;
    }
}
