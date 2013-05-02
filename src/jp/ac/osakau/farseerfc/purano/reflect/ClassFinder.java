package jp.ac.osakau.farseerfc.purano.reflect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.ac.osakau.farseerfc.purano.table.TypeNameTable;
import jp.ac.osakau.farseerfc.purano.table.Types;
import lombok.Getter;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.MethodInsnNode;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class ClassFinder {
	private @Getter Map<String, ClassRep> classMap= new HashMap<>();
	private final String prefix;
	
	public ClassFinder(String prefix){
		this.prefix = prefix;
		findClasses(prefix);
		Set<String> toLoadClass;
		Set<String> loadedClass = new HashSet<>();
		int pass=0;
		do{
			toLoadClass = new HashSet<>(Sets.difference(classMap.keySet(),loadedClass));
			loadedClass = classMap.keySet();
			for(String clsName : toLoadClass){
				findMethods(classMap.get(clsName));
			}
			pass++;
			System.out.println("Loaded Class :"+loadedClass.size());
			System.out.println("Passes :"+pass);
		}while(loadedClass.size() < classMap.size());
		
	}
	
	private void findClasses(String prefix){
		Reflections reflections = new Reflections( prefix ,new SubTypesScanner(false));

        Set<Class<? extends Object>> allClasses = 
        	    reflections.getSubTypesOf(Object.class);
        for(Class<? extends Object> cls : allClasses){
        	loadClass(cls);
        }
	}
	
	public void loadClass(Class<? extends Object> cls){
		classMap.put(cls.getName(), new ClassRep(cls));
	}
	
	private void findMethods(ClassRep cls){
		for (MethodRep m : cls.getMethodMap().values()) {
			Class<? extends Object> decl = m.getReflect().getDeclaringClass();
			if (!decl.equals(cls.getReflect())) {
				System.err.printf("Decl %s, cls %s\n",decl.getName(),cls.getName());
				MethodRep rep;
				try {
					rep = new MethodRep(
							cls.getReflect().getDeclaredMethod(
								m.getReflect().getName(),
								m.getReflect().getParameterTypes()),
							cls.getName());

					getClass(decl.getName())
						.getMethodMap()
						.get(rep.getId())
						.getOverrides().add(m);
				} catch (NoSuchMethodException | SecurityException | ClassNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
			for(MethodInsnNode call : m.getCalls()){
				System.out.println("Loading :"+call.owner);
				loadClass(MethodRep.forName(Types.binaryName2NormalName(call.owner)));
			}
		}
	}
	
	public List<String> dump(TypeNameTable table){
		List<String> result = new ArrayList<>();
		for(ClassRep cls : classMap.values()){
			result.addAll(cls.dump(table));
			//result.add(cls.getName());
		}
		return result;
	}
	
	public ClassRep getClass(String className) throws ClassNotFoundException{
		if(!classMap.containsKey(className)){
			loadClass(Class.forName(className));
		}
		return classMap.get(className);
	}

	public static void main(String [] argv){
		long start=System.currentTimeMillis();
		String target="jp.ac.osakau.farseerfc.purano";
		ClassFinder cf = new ClassFinder(target);
        //MethodRep rep=cf.getClassMap().get("jp.ac.osakau.farseerfc.purano.test.TargetA").getMethodMap().get("staticAdd(II)I");
        //rep.resolve(1);
        //System.out.println(rep.getEffects().dump(rep.getMethodNode(), new TypeNameTable()));
        TypeNameTable table = new TypeNameTable();
        System.out.println(Joiner.on("\n").join(cf.dump(table)));
        System.out.println(table.dumpImports());
        
        System.out.println("Runtime :"+(System.currentTimeMillis() - start));
	}
}
