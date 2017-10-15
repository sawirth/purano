package ch.sawirth.serialization;

import ch.sawirth.model.*;
import ch.sawirth.model.FieldModifier;
import ch.sawirth.utils.JavaTypeNameUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.ac.osakau.farseerfc.purano.dep.DepEffect;
import jp.ac.osakau.farseerfc.purano.dep.FieldDep;
import jp.ac.osakau.farseerfc.purano.effect.AbstractFieldEffect;
import jp.ac.osakau.farseerfc.purano.effect.ArgumentEffect;
import jp.ac.osakau.farseerfc.purano.effect.FieldEffect;
import jp.ac.osakau.farseerfc.purano.effect.StaticEffect;
import jp.ac.osakau.farseerfc.purano.reflect.ClassRep;
import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;
import org.objectweb.asm.tree.LocalVariableNode;
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
        List<MethodArgument> methodArguments = new ArrayList<>();
        if (methodRep.getMethodNode() != null)
        {
            List<LocalVariableNode> localVariables = methodRep.getMethodNode().localVariables;
            List<String> arguments = methodRep.getDesc().getArguments();

            if (methodRep.isInit() && arguments.size() > 0) {
                arguments.remove(0);
            }

            if (arguments.size() > 0) {
                methodArguments.addAll(createMethodArguments(arguments, localVariables, methodRep.isStatic()));
            }
        }

        DepEffect staticEffects = methodRep.getStaticEffects();
        List<FieldModifier> fieldModifiers = createFieldModifiers(staticEffects.getThisField(), methodRep.isStatic());
        List<FieldModifier> staticFieldModifiers = createStaticFieldModifiers(staticEffects.getStaticField(), methodRep.isStatic());
        List<ArgumentModifier> argumentModifiers = createArgumentModifiers(staticEffects.getArgumentEffects(), methodRep.isStatic());
        return new MethodRepresentation(
                fullMethodName,
                methodRep.dumpPurity(),
                methodArguments,
                fieldModifiers,
                staticFieldModifiers,
                argumentModifiers);
    }

    private List<MethodArgument> createMethodArguments(List<String> parameterTypes, List<LocalVariableNode> localVariableNodes, boolean isStatic) {
        List<MethodArgument> methodArguments = new ArrayList<>();
        int position = isStatic ? 0 : 1;
        for(String parameterType : parameterTypes) {
            String name = localVariableNodes.get(position).name;
            if (isStatic) {
                methodArguments.add(new MethodArgument(position, name, parameterType));
            } else {
                methodArguments.add(new MethodArgument(position - 1, name, parameterType));
            }

            position++;
        }

        return methodArguments;
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
            for (Iterator<Integer> it = dependsOnParameterFromIndex.iterator(); it.hasNext() ; ) {
                //0 is the argument "this" which we are not intersted here as this is also represented as a dependency
                Integer integer = it.next();
                it.remove();
                if (integer > 0) {
                    dependsOnParameterFromIndex.add(--integer);
                }
            }
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
}

