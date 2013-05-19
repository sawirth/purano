// Generated by delombok at Mon May 20 05:22:39 JST 2013
package jp.ac.osakau.farseerfc.purano.effect;

import jp.ac.osakau.farseerfc.purano.dep.DepSet;
import jp.ac.osakau.farseerfc.purano.reflect.MethodRep;
import jp.ac.osakau.farseerfc.purano.util.Types;

public class OtherFieldEffect extends FieldEffect implements Cloneable {
	
	public OtherFieldEffect(String desc, String owner, String name, DepSet deps, DepSet leftValueDeps, MethodRep from) {
		super(desc, owner, name, null, from);
//		this.leftValueDeps = leftValueDeps;
	}
	
//	
//	private final @Getter DepSet leftValueDeps;
	@Override
	public String getKey() {
		return getDesc() + getOwner() + getName();
	}
	
	@Override
	public Effect clone() {
		return new OtherFieldEffect(getDesc(), getOwner(), getName(), getDeps(), null, getFrom());
	}
	
	@Override
	protected String dumpEffect(MethodRep rep, Types table) {
		return String.format("%s %s#%s", table.desc2full(getDesc()), table.fullClassName(getOwner()), getName());
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public boolean equals(final java.lang.Object o) {
		if (o == this) return true;
		if (!(o instanceof OtherFieldEffect)) return false;
		final OtherFieldEffect other = (OtherFieldEffect)o;
		if (!other.canEqual((java.lang.Object)this)) return false;
		if (!super.equals(o)) return false;
		return true;
	}
	
	@java.lang.SuppressWarnings("all")
	public boolean canEqual(final java.lang.Object other) {
		return other instanceof OtherFieldEffect;
	}
	
	@java.lang.Override
	@java.lang.SuppressWarnings("all")
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = result * PRIME + super.hashCode();
		return result;
	}
}