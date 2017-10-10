package ch.sawirth.serialization;

import ch.sawirth.model.ClassRepresentation;
import ch.sawirth.model.MethodArgument;
import ch.sawirth.model.MethodRepresentation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jp.ac.osakau.farseerfc.purano.reflect.ClassRep;
import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;
import org.objectweb.asm.tree.LocalVariableNode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            classRepresentations.add(CreateClassRepresentation(classRep));
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

    private ClassRepresentation CreateClassRepresentation(ClassRep classRep) {
        HashSet<MethodRepresentation> methodRepresentations = new HashSet<>();
        for (MethodRep methodRep : classRep.getAllMethods()) {
            methodRepresentations.add(CreateMethodRepresentation(methodRep));
        }

        return new ClassRepresentation(classRep.name, methodRepresentations);
    }

    private MethodRepresentation CreateMethodRepresentation(MethodRep methodRep) {
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

        return new MethodRepresentation(fullMethodName, methodRep.dumpPurity(), methodArguments);
    }

    private List<MethodArgument> createMethodArguments(List<String> parameterTypes, List<LocalVariableNode> localVariableNodes, boolean isStatic) {
        List<MethodArgument> methodArguments = new ArrayList<>();
        int position = isStatic ? 0 : 1;
        for(String parameterType : parameterTypes) {
            String name = localVariableNodes.get(position).name;
            methodArguments.add(new MethodArgument(position, name, parameterType));
            position++;
        }

        return methodArguments;
    }
}
