package ch.sawirth.utils;

import jp.ac.osakau.farseerfc.purano.dep.DepEffect;
import jp.ac.osakau.farseerfc.purano.dep.DepSet;
import jp.ac.osakau.farseerfc.purano.dep.DepValue;
import jp.ac.osakau.farseerfc.purano.dep.FieldDep;
import jp.ac.osakau.farseerfc.purano.effect.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FindDynamicOnlyEffectsHelper {

    /**
     * Returns a DepEffect that only contains effects that are not in staticEffects
     * @param staticEffects static DepEffect
     * @param dynamicEffects dynamic DepEffect
     * @return DepEffect with dynamic only effects
     */
    public static DepEffect getDynamicDepEffect(DepEffect staticEffects, DepEffect dynamicEffects) {
        DepEffect dynamicOnlyEffects = new DepEffect();

        //Static effects
        Map<String,FieldEffect> thisField = staticEffects.getThisField();
        Map<String,OtherFieldEffect> otherField = staticEffects.getOtherField();
        Map<String,StaticEffect> staticField = staticEffects.getStaticField();
        Set<ArgumentEffect> argumentEffects = staticEffects.getArgumentEffects();
        Set<LocalVariableEffect> localVariableEffects = staticEffects.getLocalVariableEffects();
        Set<CallEffect> callEffects = staticEffects.getCallEffects();
        Set<Effect> otherEffects = staticEffects.getOtherEffects();
        DepValue depSet = staticEffects.getReturnDep();

        //Dynamic effects
        Map<String,FieldEffect> thisFieldDynamic = dynamicEffects.getThisField();
        Map<String,OtherFieldEffect> otherFieldDynamic = dynamicEffects.getOtherField();
        Map<String,StaticEffect> staticFieldDynamic = dynamicEffects.getStaticField();
        Set<ArgumentEffect> argumentEffectsDynamic = dynamicEffects.getArgumentEffects();
        Set<LocalVariableEffect> localVariableEffectsDynamic = dynamicEffects.getLocalVariableEffects();
        Set<CallEffect> callEffectsDynamic = dynamicEffects.getCallEffects();
        Set<Effect> otherEffectsDynamic = dynamicEffects.getOtherEffects();
        DepValue depSetDynamic = dynamicEffects.getReturnDep();

        for (Map.Entry<String, FieldEffect> effectEntry : thisFieldDynamic.entrySet()) {
            if (!thisField.containsKey(effectEntry.getKey())) {
                dynamicOnlyEffects.addThisField(effectEntry.getValue());
            }
        }

        for (Map.Entry<String, OtherFieldEffect> effectEntry : otherFieldDynamic.entrySet()) {
            if (!otherField.containsKey(effectEntry.getKey())) {
                dynamicOnlyEffects.addOtherField(effectEntry.getValue());
            }
        }

        for (Map.Entry<String, StaticEffect> effectEntry : staticFieldDynamic.entrySet()) {
            if (!staticField.containsKey(effectEntry.getKey())) {
                dynamicOnlyEffects.addStaticField(effectEntry.getValue());
            }
        }

        for (ArgumentEffect argumentEffect : argumentEffectsDynamic) {
            if (!argumentEffects.contains(argumentEffect)) {
                dynamicOnlyEffects.addArgumentEffect(argumentEffect);
            }
        }

        for (LocalVariableEffect localVariableEffect : localVariableEffectsDynamic) {
            if (!localVariableEffects.contains(localVariableEffect)) {
                dynamicOnlyEffects.addLocalVariableEffect(localVariableEffect);
            }
        }

        for (CallEffect callEffect : callEffectsDynamic) {
            if (!callEffects.contains(callEffect)) {
                dynamicOnlyEffects.addCallEffect(callEffect);
            }
        }

        for (Effect effect : otherEffectsDynamic) {
            if (!otherEffects.contains(effect)) {
                dynamicOnlyEffects.addOtherEffect(effect);
            }
        }

        Set<FieldDep> onlyDynamicStaticFieldDeps = new HashSet<>();
        for (FieldDep fieldDep : depSetDynamic.getDeps().getStatics()) {
            if (!depSet.getDeps().getStatics().contains(fieldDep)) {
                onlyDynamicStaticFieldDeps.add(fieldDep);
            }
        }

        Set<FieldDep> onlyDynamicFieldDeps = new HashSet<>();
        for (FieldDep fieldDep : depSetDynamic.getDeps().getFields()) {
            if (!depSet.getDeps().getFields().contains(fieldDep)) {
                onlyDynamicFieldDeps.add(fieldDep);
            }
        }

        Set<Integer> onlyDynamicLocalDeps = new HashSet<>();
        for (Integer fieldDep : depSetDynamic.getDeps().getLocals()) {
            if (!depSet.getDeps().getFields().contains(fieldDep)) {
                onlyDynamicLocalDeps.add(fieldDep);
            }
        }

        DepSet onlyDynamicReturnDepSet = new DepSet(onlyDynamicStaticFieldDeps, onlyDynamicFieldDeps, onlyDynamicLocalDeps);
        dynamicOnlyEffects.getReturnDep().setDeps(onlyDynamicReturnDepSet);

        return dynamicOnlyEffects;
    }
}
