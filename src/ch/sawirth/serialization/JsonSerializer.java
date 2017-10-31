package ch.sawirth.serialization;

import ch.sawirth.model.*;
import ch.sawirth.model.FieldModifier;
import ch.sawirth.model.NativeEffect;
import ch.sawirth.utils.JavaTypeNameUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.ac.osakau.farseerfc.purano.dep.DepEffect;
import jp.ac.osakau.farseerfc.purano.dep.DepSet;
import jp.ac.osakau.farseerfc.purano.dep.FieldDep;
import jp.ac.osakau.farseerfc.purano.effect.*;
import jp.ac.osakau.farseerfc.purano.reflect.ClassRep;
import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodInsnNode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class JsonSerializer {

    private final Set<ClassRep> classRepSet;
    private final String saveToPath;

    public JsonSerializer(Set<ClassRep> classRepSet, String saveToPath) {
        this.classRepSet = classRepSet;
        this.saveToPath = saveToPath;
    }

    public void serializeToGson() {
        System.out.println();
        System.out.println("Serializing to JSON v1.0");

        HashSet<ClassRepresentation> classRepresentations = new HashSet<>();

        for (ClassRep classRep : classRepSet) {
            classRepresentations.add(createClassRepresentation(classRep));
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(classRepresentations);

        String fileName = "Purano-Result.json";
        File file = new File(this.saveToPath + "\\" + fileName);
        try {
            file.createNewFile();
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.flush();
            writer.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private ClassRepresentation createClassRepresentation(ClassRep classRep) {
        HashSet<MethodRepresentation> methodRepresentations = new HashSet<>();
        for (MethodRep methodRep : classRep.getAllMethods()) {
            methodRepresentations.add(createMethodRepresentation(methodRep));
        }

        return new ClassRepresentation(classRep.getName(), methodRepresentations);
    }

    private MethodRepresentation createMethodRepresentation(MethodRep methodRep) {
        String fullMethodName = methodRep.getInsnNode().owner + "." + methodRep.getInsnNode().name;
        List<String> methodArguments = new ArrayList<>();
        if (methodRep.getMethodNode() != null)
        {
            List<LocalVariableNode> localVariables = methodRep.getMethodNode().localVariables;
            List<String> arguments = methodRep.getDesc().getArguments();

            if (arguments.size() > 0) {
                methodArguments.addAll(arguments);
            }
        }

        DepEffect staticEffects = methodRep.getStaticEffects();
        List<FieldModifier> fieldModifiers = createFieldModifiers(staticEffects.getThisField(), methodRep.isStatic());
        List<FieldModifier> staticFieldModifiers = createStaticFieldModifiers(staticEffects.getStaticField(), methodRep.isStatic());
        List<ArgumentModifier> argumentModifiers = createArgumentModifiers(staticEffects.getArgumentEffects(), methodRep.isStatic());
        ReturnDependency returnDependency = createReturnDependency(staticEffects.getReturnDep().getDeps());
        Set<NativeEffect> nativeEffects = createNativeEffects(staticEffects.getOtherEffects());

        return new MethodRepresentation(
                fullMethodName,
                methodRep.dumpPurity(),
                methodArguments,
                fieldModifiers,
                staticFieldModifiers,
                argumentModifiers,
                returnDependency,
                nativeEffects);
    }

    private List<FieldModifier> createFieldModifiers(Map<String, FieldEffect> fieldEffects, boolean isStaticMethod) {
        List<FieldModifier> fieldModifiers = new ArrayList<>();
        for (FieldEffect effect : fieldEffects.values()) {
            fieldModifiers.add(createFieldModifier(effect, isStaticMethod));
        }

        return fieldModifiers;
    }

    private List<FieldModifier> createStaticFieldModifiers(Map<String, StaticEffect> staticEffects, boolean isStaticMethod) {
        List<FieldModifier> staticFieldModifiers = new ArrayList<>();
        for (StaticEffect effect : staticEffects.values()) {
            staticFieldModifiers.add(createFieldModifier(effect, isStaticMethod));
        }

        return staticFieldModifiers;
    }

    private FieldModifier createFieldModifier(AbstractFieldEffect effect, boolean isStaticMethod) {
        String name = effect.getName();
        String type = JavaTypeNameUtils.convertToPrimitiveTypeNameIfNecessary(effect.getDesc());
        String owner = effect.getOwner();
        boolean hasDirectAccess = effect.getFrom() == null;
        Set<Integer> dependsOnParameterFromIndex = effect.getDeps().getLocals();
        if (!isStaticMethod) {
            Set<Integer> modifiedParameters = new HashSet<>();
            for (Integer integer : dependsOnParameterFromIndex) {
                if (integer > 0) {
                    modifiedParameters.add(--integer);
                }
            }

            dependsOnParameterFromIndex = modifiedParameters;
        }

        Set<FieldDependency> staticFieldDependencies = createFieldDependencies(effect.getDeps().getStatics());
        Set<FieldDependency> localFieldDependencies = createFieldDependencies(effect.getDeps().getFields());
        return new FieldModifier(name, type,
                                 owner,
                                 hasDirectAccess, dependsOnParameterFromIndex, localFieldDependencies, staticFieldDependencies);
    }

    private Set<FieldDependency> createFieldDependencies(Set<FieldDep> fieldDeps) {
        Set<FieldDependency> fieldDependencies = new HashSet<>();
        for (FieldDep fieldDep: fieldDeps) {
            String name = fieldDep.getName();
            String owner = fieldDep.getOwner();
            String type = JavaTypeNameUtils.convertToPrimitiveTypeNameIfNecessary(fieldDep.getDesc());

            //somehow Purano adds a stupid L to the type, if it's not a primitive type
            if (type.startsWith("L") && type.contains("/")) {
                type = type.substring(1);
            }

            fieldDependencies.add(new FieldDependency(name, owner, type));
        }

        return fieldDependencies;
    }

    private List<ArgumentModifier> createArgumentModifiers(Set<ArgumentEffect> argumentEffects, boolean isStaticMethod) {
        List<ArgumentModifier> argumentModifiers = new ArrayList<>();
        for (ArgumentEffect effect : argumentEffects) {
            int position = effect.getArgPos();
            if (!isStaticMethod) {
                --position;
            }

            argumentModifiers.add(new ArgumentModifier(position, effect.getFrom() == null));
        }

        return argumentModifiers;
    }

    private ReturnDependency createReturnDependency(DepSet depSet) {
        boolean dependsOnThis = depSet.getLocals().contains(0);
        Set<Integer> indexOfDependentArguments = new HashSet<>();
        indexOfDependentArguments.addAll(depSet.getLocals());
        indexOfDependentArguments.remove(0);
        Set<FieldDependency> staticFieldDependencies = createFieldDependencies(depSet.getStatics());
        Set<FieldDependency> fieldDependencies = createFieldDependencies(depSet.getFields());
        return new ReturnDependency(staticFieldDependencies, fieldDependencies, indexOfDependentArguments, dependsOnThis);
    }

    private Set<NativeEffect> createNativeEffects(Set<Effect> otherEffects) {
        Set<NativeEffect> nativeEffects = new HashSet<>();

        for (Effect effect : otherEffects) {
            MethodRep fromMethod = effect.getFrom();
            if (fromMethod != null) {
                MethodInsnNode insnNode = fromMethod.getInsnNode();
                MethodInsnNode originMethod = findNativeOrigin(fromMethod);
                nativeEffects.add(new NativeEffect(insnNode.owner, insnNode.name, originMethod.owner, originMethod.name));
            }
        }

        return nativeEffects;
    }

    private MethodInsnNode findNativeOrigin(MethodRep fromMethod) {
        Set<Effect> otherEffects = fromMethod.getStaticEffects().getOtherEffects();

        //Finding the origin method only works if each method in the callgraph has just one native effect
        //In this case we abort and just return the best possible option
        if (otherEffects.size() != 1) {
            return fromMethod.getInsnNode();
        }

        for (Effect effect : otherEffects) {
            if (!(effect instanceof jp.ac.osakau.farseerfc.purano.effect.NativeEffect)) {
                return fromMethod.getInsnNode();
            }

            if (fromMethod.isNative() && effect.getFrom() == null) {
                return fromMethod.getInsnNode();
            }

            return findNativeOrigin(effect.getFrom());
        }

        return fromMethod.getInsnNode();
    }
}


