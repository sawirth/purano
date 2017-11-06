package jp.ac.osakau.farseerfc.purano.dep;

import jp.ac.osakau.farseerfc.purano.effect.ArgumentEffect;
import jp.ac.osakau.farseerfc.purano.effect.CallEffect;
import jp.ac.osakau.farseerfc.purano.effect.Effect;
import jp.ac.osakau.farseerfc.purano.effect.FieldEffect;
import jp.ac.osakau.farseerfc.purano.effect.OtherFieldEffect;
import jp.ac.osakau.farseerfc.purano.effect.StaticEffect;

public interface IDepEffect {
	void addThisField(FieldEffect tfe);
	void addOtherField(OtherFieldEffect ofe);
	void addOtherEffect(Effect oe);
	void addCallEffect(CallEffect ce);
	void addStaticField(StaticEffect sfe);
	void addArgumentEffect(ArgumentEffect ae);
}
