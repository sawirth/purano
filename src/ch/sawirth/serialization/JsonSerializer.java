package ch.sawirth.serialization;

import ch.sawirth.model.*;
import ch.sawirth.model.FieldModifier;
import ch.sawirth.model.NativeEffect;
import ch.sawirth.utils.FindDynamicOnlyEffectsHelper;
import ch.sawirth.utils.JavaTypeNameUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.ac.osakau.farseerfc.purano.dep.DepEffect;
import jp.ac.osakau.farseerfc.purano.dep.DepSet;
import jp.ac.osakau.farseerfc.purano.dep.FieldDep;
import jp.ac.osakau.farseerfc.purano.effect.*;
import jp.ac.osakau.farseerfc.purano.reflect.ClassRep;
import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;
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
        Collection<MethodRep> methodReps = classRep.getAllMethods();
        for (MethodRep methodRep : methodReps) {
            methodRepresentations.add(createMethodRepresentation(methodRep));
        }

        return new ClassRepresentation(classRep.getName(), methodRepresentations);
    }

    private MethodRepresentation createMethodRepresentation(MethodRep methodRep) {
        String fullMethodName = methodRep.getInsnNode().owner + "." + methodRep.getInsnNode().name;
        List<String> methodArguments = new ArrayList<>();
        if (methodRep.getMethodNode() != null) {
            List<String> arguments = methodRep.getDesc().getArguments();

            if (arguments.size() > 0) {
                methodArguments.addAll(arguments);
            }
        }

        String classOwner = methodRep.getInsnNode().owner;
        DepEffect staticEffects = methodRep.getStaticEffects();
        DepEffect dynamicEffects = methodRep.getDynamicEffects();
        DepEffect dynamicOnlyEffects = FindDynamicOnlyEffectsHelper.getDynamicDepEffect(staticEffects, dynamicEffects);

        List<FieldModifier> fieldModifiers = createFieldModifiers(staticEffects.getThisField(),
                                                                  dynamicOnlyEffects.getThisField(),
                                                                  methodRep.isStatic(),
                                                                  classOwner);

        List<FieldModifier> staticFieldModifiers = createStaticFieldModifiers(staticEffects.getStaticField(),
                                                                              dynamicOnlyEffects.getStaticField(),
                                                                              methodRep.isStatic());

        List<ArgumentModifier> argumentModifiers = createArgumentModifiers(staticEffects.getArgumentEffects(),
                                                                           dynamicOnlyEffects.getArgumentEffects(),
                                                                           methodRep.isStatic());

        ReturnDependency returnDependency = createReturnDependency(staticEffects.getReturnDep().getDeps(),
                                                                   dynamicOnlyEffects.getReturnDep().getDeps(),
                                                                   methodRep.isStatic(),
                                                                   classOwner,
                                                                   methodArguments.size());

        Set<NativeEffect> nativeEffects = createNativeEffects(staticEffects.getOtherEffects(), dynamicOnlyEffects.getOtherEffects());

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

    private List<FieldModifier> createFieldModifiers(
            Map<String, FieldEffect> staticFieldEffects,
            Map<String, FieldEffect> dynamicFieldEffects,
            boolean isStaticMethod,
            String classOwner) {
        List<FieldModifier> fieldModifiers = new ArrayList<>();
        for (FieldEffect effect : staticFieldEffects.values()) {
            fieldModifiers.add(createFieldModifier(effect, isStaticMethod, classOwner, false));
        }

        for (FieldEffect effect : dynamicFieldEffects.values()) {
            fieldModifiers.add(createFieldModifier(effect, isStaticMethod, classOwner, true));
        }

        return fieldModifiers;
    }

    private List<FieldModifier> createStaticFieldModifiers(Map<String, StaticEffect> staticEffects,
                                                           Map<String, StaticEffect> dynamicEffects,
                                                           boolean isStaticMethod) {
        List<FieldModifier> staticFieldModifiers = new ArrayList<>();
        for (StaticEffect effect : staticEffects.values()) {
            staticFieldModifiers.add(createFieldModifier(effect, isStaticMethod, "", false));
        }

        for (StaticEffect effect : dynamicEffects.values()) {
            staticFieldModifiers.add(createFieldModifier(effect, isStaticMethod, "", true));
        }

        return staticFieldModifiers;
    }

    private FieldModifier createFieldModifier(AbstractFieldEffect effect,
                                              boolean isStaticMethod,
                                              String classOwner,
                                              boolean isDynamicEffect) {
        String name = effect.getName();
        String type = JavaTypeNameUtils.convertToPrimitiveTypeNameIfNecessary(effect.getDesc());
        type = type.endsWith(";") ? type.replace(";", "") : type;
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

        Set<FieldDependency> staticFieldDependencies = createFieldDependencies(effect.getDeps().getStatics(),
                                                                               classOwner,
                                                                               isDynamicEffect);
        Set<FieldDependency> localFieldDependencies = createFieldDependencies(effect.getDeps().getFields(),
                                                                              classOwner,
                                                                              isDynamicEffect);
        return new FieldModifier(name,
                                 type,
                                 owner,
                                 hasDirectAccess,
                                 dependsOnParameterFromIndex,
                                 localFieldDependencies,
                                 staticFieldDependencies);
    }

    private Set<FieldDependency> createFieldDependencies(Set<FieldDep> fieldDeps,
                                                         String classOwner,
                                                         boolean isDynamicEffect) {
        Set<FieldDependency> fieldDependencies = new HashSet<>();
        for (FieldDep fieldDep : fieldDeps) {
            String name = fieldDep.getName();
            String owner = fieldDep.getOwner();
            owner = owner.replace('/', '.');
            String type = JavaTypeNameUtils.convertToPrimitiveTypeNameIfNecessary(fieldDep.getDesc());

            //somehow Purano adds a stupid L to the type, if it's not a primitive type
            if (type.startsWith("L") && type.contains("/")) {
                type = type.substring(1);
            }

            type = type.replace('/', '.');

            if (classOwner.equals(owner)) {
                fieldDependencies.add(new FieldDependency(name, owner, type, true, isDynamicEffect));
            } else {
                fieldDependencies.add(new FieldDependency(name, owner, type, false, isDynamicEffect));
            }
        }

        return fieldDependencies;
    }

    private List<ArgumentModifier> createArgumentModifiers(Set<ArgumentEffect> staticArgumentEffects,
                                                           Set<ArgumentEffect> dynamicArgumentEffects,
                                                           boolean isStaticMethod) {
        List<ArgumentModifier> argumentModifiers = new ArrayList<>();
        for (ArgumentEffect effect : staticArgumentEffects) {
            int position = effect.getArgPos();
            if (!isStaticMethod) {
                --position;
            }

            argumentModifiers.add(new ArgumentModifier(position, effect.getFrom() == null, false));
        }

        for (ArgumentEffect effect : dynamicArgumentEffects) {
            int position = effect.getArgPos();
            if (!isStaticMethod) {
                --position;
            }

            String owner = effect.getFrom().getInsnNode().owner;

            argumentModifiers.add(
                    new ArgumentModifier(position, effect.getFrom() == null, true, owner));
        }

        return argumentModifiers;
    }

    private ReturnDependency createReturnDependency(DepSet staticDepSet,
                                                    DepSet dynamicDepSet,
                                                    boolean isStatic,
                                                    String classOwner,
                                                    int numberOfArguments)
    {
        boolean dependsOnThis = staticDepSet.getLocals().contains(0) && !staticDepSet.getFields().isEmpty();
        Set<Integer> indexOfDependentArguments = new HashSet<>();
        indexOfDependentArguments.addAll(staticDepSet.getLocals());
        indexOfDependentArguments.addAll(dynamicDepSet.getLocals());

        if (!isStatic) {
            indexOfDependentArguments.remove(0);
            List<Integer> correctedArguments = new ArrayList<>();
            for (Integer integer : indexOfDependentArguments) {
                correctedArguments.add(--integer);
            }

            indexOfDependentArguments.clear();
            indexOfDependentArguments.addAll(correctedArguments);
        }

        Set<Integer> correctedIndices = new HashSet<>();
        for (Integer integer : indexOfDependentArguments) {
            if (integer < numberOfArguments) {
                correctedIndices.add(integer);
            }
        }

        Set<FieldDependency> staticFieldDependencies = createFieldDependencies(staticDepSet.getStatics(),
                                                                               classOwner,
                                                                               false);

        Set<FieldDependency> fieldDependencies = createFieldDependencies(staticDepSet.getFields(), classOwner, false);

        Set<FieldDependency> dynamicStaticFieldDependencies = createFieldDependencies(dynamicDepSet.getStatics(),
                                                                                      classOwner,
                                                                                      true);

        Set<FieldDependency> dynamicFieldDependencies = createFieldDependencies(dynamicDepSet.getFields(),
                                                                                classOwner,
                                                                                true);

        fieldDependencies.addAll(dynamicFieldDependencies);
        staticFieldDependencies.addAll(dynamicStaticFieldDependencies);

        return new ReturnDependency(staticFieldDependencies,
                                    fieldDependencies,
                                    correctedIndices,
                                    dependsOnThis);
    }

    private Set<NativeEffect> createNativeEffects(Set<Effect> staticOtherEffects, Set<Effect> dynamicOtherEffects) {
        Set<NativeEffect> nativeEffects = new HashSet<>();

        for (Effect effect : staticOtherEffects) {
            MethodRep fromMethod = effect.getFrom();
            if (fromMethod != null) {
                MethodInsnNode insnNode = fromMethod.getInsnNode();
                MethodInsnNode originMethod = findNativeOrigin(fromMethod);
                nativeEffects.add(new NativeEffect(insnNode.owner,
                                                   insnNode.name,
                                                   originMethod.owner,
                                                   originMethod.name,
                                                   false));
            }
        }

        for (Effect effect : dynamicOtherEffects) {
            MethodRep fromMethod = effect.getFrom();
            if (fromMethod != null) {
                MethodInsnNode insnNode = fromMethod.getInsnNode();
                MethodInsnNode originMethod = findNativeOrigin(fromMethod);
                nativeEffects.add(new NativeEffect(insnNode.owner,
                                                   insnNode.name,
                                                   originMethod.owner,
                                                   originMethod.name,
                                                   true));
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


