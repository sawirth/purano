package jp.ac.osakau.farseerfc.purano.reflect;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import jp.ac.osakau.farseerfc.purano.util.Escape;
import jp.ac.osakau.farseerfc.purano.util.Types;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ClassRep extends ClassVisitor {

	private final Map<String, MethodRep> methodMap = new HashMap<>();



	@NotNull
    public final @Getter String name;

	private final ClassFinder classFinder;

	private final @Getter List<ClassRep> supers = new ArrayList<>();
//	private final @Getter Class<? extends Object> reflect;

	public ClassRep(@NotNull String className, ClassFinder cf){
		super(Opcodes.ASM4);
		this.name = className;
//		try {
//			cls = Class.forName(className);
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		}
//		this.reflect = cls;
		this.classFinder = cf;
		try {
			new ClassReader(className).accept(this, 0);
		} catch (IOException e) {
//			e.printStackTrace();
//			throw new RuntimeException("Cannot load "+className ,e);
			Types.notFound(className,e);
		}

	}

	public MethodRep getMethodVirtual(String methodId){
		MethodRep s = methodMap.get(methodId);
		if(s == null){
			for(ClassRep sup: supers){
				s = sup.getMethodVirtual(methodId);
				if(s != null){
					return s;
				}
			}
		}
		return s;
	}

	@NotNull
    public Collection<MethodRep> getAllMethods(){
		return methodMap.values();
	}


//	public ClassRep(Class<? extends Object> reflect) throws IOException{
//		super(Opcodes.ASM4);
//		this.reflect = reflect;
//		if(reflect.isArray()){
//			this.name = ArrayStub.class.getName();
//		}else{
//			this.name = reflect.getName();
//		}
//		new ClassReader(this.name).accept(this, 0);
//	}

	@NotNull
    public List<String> dump(@NotNull Types table){
		List<String> result = new ArrayList<>();
		result.add(Escape.className( table.fullClassName(name)));
		for(MethodRep m:methodMap.values()){
			result.addAll(m.dump(classFinder, table));
		}
		return result;
	}

	@org.jetbrains.annotations.Nullable
    @Override
	public MethodVisitor visitMethod(int access, String name, String desc,
			String signature, String[] exceptions) {
//		if(name.equals("<init>")){
//			return null;
//		}

		// Ignore bridge methods
		if((access & Opcodes.ACC_BRIDGE) > 0){
			return null;
		}

		// Build method rep
		MethodRep rep = new MethodRep(new MethodInsnNode(0, this.name, name, desc), access);
		methodMap.put(rep.getId(),rep);
		for(ClassRep s : supers){
			s.override(rep.getId(),rep);
		}
		return rep;
	}

	public void override(String id, @NotNull MethodRep overrider) {
		MethodRep overridded = methodMap.get(id);
		if(overridded != null){
//			log.info("{} {} override {}",id ,overrider.getInsnNode().owner, name);
			overridded.override(overrider);
		}
		for(ClassRep s : supers){
			s.override(overrider.getId(),overrider);
		}
	}

	@Override
	public void visit(int version, int access, String name, String signature,
			@org.jetbrains.annotations.Nullable String superName, String[] interfaces) {
		//super.visit(version, access, name, signature, superName, interfaces);

//		log.info("Visiting class {} super {} interfaces {}",this.name,superName,Joiner.on(",").join(interfaces));

		if(!this.name.equals(Object.class.getName()) && superName != null){
			this.supers.add(classFinder.loadClass(Types.binaryName2NormalName(superName)));
		}

		this.supers.addAll(Lists.transform(Arrays.asList(interfaces), new Function<String,ClassRep>(){
			@Override @javax.annotation.Nullable
			public ClassRep apply(@javax.annotation.Nullable String name) {
                if (name == null) {
                    return null;
                }
                return classFinder.loadClass(Types.binaryName2NormalName(name));
            }}));
	}
}
