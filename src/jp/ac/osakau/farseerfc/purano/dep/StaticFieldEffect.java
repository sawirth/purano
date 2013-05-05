package jp.ac.osakau.farseerfc.purano.dep;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper=true)
public class StaticFieldEffect extends FieldEffect{
	public StaticFieldEffect(String desc,String owner,String name, DepSet deps) {
		super(desc,owner,name,deps);
	}
}
