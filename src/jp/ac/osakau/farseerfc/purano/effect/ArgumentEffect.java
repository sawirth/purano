package jp.ac.osakau.farseerfc.purano.effect;

import jp.ac.osakau.farseerfc.purano.ano.Field;
import jp.ac.osakau.farseerfc.purano.dep.DepSet;
import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;
import jp.ac.osakau.farseerfc.purano.util.Types;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.tree.FieldInsnNode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper=true)
public class ArgumentEffect extends Effect<ArgumentEffect> {

	private final @Getter int argPos;
	private final @Getter String fieldName;
	
	public ArgumentEffect(int argPos, DepSet deps, MethodRep from, String fieldName) {
		super(deps,from);
		this.argPos = argPos;
		this.fieldName = fieldName;
	}

	@NotNull
    @Override
	public ArgumentEffect clone() {
		return new ArgumentEffect(argPos, getDeps(), getFrom(), fieldName);
	}

	@NotNull
    @Override
	public List<String> dumpEffect(@NotNull MethodRep rep, @NotNull Types table) {
		if(getArgPos() < rep.getMethodNode().localVariables.size()){
            ArrayList<String> result = new ArrayList<>();
            result.add("name=\""+rep.getMethodNode().localVariables.get(getArgPos()).name+ "\", fieldName=\"" + fieldName +"\"");
            result.addAll(getDeps().dumpDeps(rep, table));
			return result;

		}else{
            ArrayList<String> result = new ArrayList<>();
            result.add("name=#\""+getArgPos()+ ", fieldName=" + fieldName + "\"");
            result.addAll(getDeps().dumpDeps(rep, table));
            return result;
		}
	}

}
