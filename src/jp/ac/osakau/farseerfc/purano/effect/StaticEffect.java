package jp.ac.osakau.farseerfc.purano.effect;

import jp.ac.osakau.farseerfc.purano.dep.DepSet;
import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;
import jp.ac.osakau.farseerfc.purano.util.Types;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EqualsAndHashCode(callSuper=true)
public final class StaticEffect extends AbstractFieldEffect<StaticEffect> implements Cloneable{
	public StaticEffect(String desc, String owner, String name, DepSet deps, MethodRep from) {
		super(desc,owner,name,deps, from);
//        super("","","",deps, from);
	}
	
	@NotNull
    @Override
	public StaticEffect clone(){
		return new StaticEffect(getDesc(), getOwner(), getName(), getDeps(), getFrom());
	}

	@NotNull
    @Override
	protected List<String> dumpEffect(@NotNull MethodRep rep, @NotNull Types table) {
        if(getName().equals("")){
            ArrayList<String> result = new ArrayList<>();
            result.addAll(getDeps().dumpDeps(rep, table));
            return result;
        }else{
            ArrayList<String> result = new ArrayList<>(Arrays.asList(
    //                ""
                    "type=" + table.desc2full(getDesc())+".class",
                    "owner=" + table.fullClassName(getOwner())+".class",
                    "name=\"" + getName() + "\""
            ));
            result.addAll(getDeps().dumpDeps(rep, table));
            return result;
        }
	}
}
