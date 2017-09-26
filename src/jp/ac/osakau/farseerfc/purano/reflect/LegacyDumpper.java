package jp.ac.osakau.farseerfc.purano.reflect;

import com.google.common.base.Joiner;
import jp.ac.osakau.farseerfc.purano.ano.Purity;
import jp.ac.osakau.farseerfc.purano.util.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: farseerfc
 * Date: 9/16/13
 * Time: 5:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class LegacyDumpper implements ClassFinderDumpper {
    private final ClassFinder cf;

    public LegacyDumpper(ClassFinder cf) {
        this.cf = cf;
    }

    @Override
    public void dump(){
        Types table = new Types(true, cf.prefix.get(0));
        int method = 0, unknown = 0, stateless = 0, stateful = 0, modifier =0 ;
        int hmethod = 0, hunknown = 0, hstateless = 0, hstateful = 0, hmodifier =0, esln = 0 , esfn=0, en = 0 ;
        int emethod = 0, eunknown = 0, estateless = 0, estateful = 0, emodifier =0, hsln = 0 , hsfn=0, hn = 0 ;
        int fieldM = 0, staticM = 0, argM = 0, nativeE = 0;
        int classes = 0;

        List<String> sb = new ArrayList<>();

        for(String clsName : cf.classMap.keySet()){
            boolean isTarget = cf.classTargets.contains(clsName);
            for(String p:cf.prefix){
                if(clsName.startsWith(p)){
                    isTarget = true;
                }
            }

            if (!isTarget) {
                continue;
            }

            ClassRep cls = cf.classMap.get(clsName);
            System.out.println(Joiner.on("\n").join(cls.dump(table)));
            for(MethodRep mtd: cls.getAllMethods()){
                method++;
                int p=mtd.purity();
                if(p == Purity.Unknown){
                    unknown ++;
                }
                if(p == Purity.Stateless){
                    stateless ++;
                }else if(p == Purity.Stateful){
                    stateful ++;
                }else{
                    modifier ++;
                }
                if((p & Purity.ArgumentModifier)>0){
                    argM ++;
                }
                if((p & Purity.FieldModifier)>0){
                    fieldM ++;
                }
                if((p & Purity.StaticModifier)>0){
                    staticM ++;
                }
                if((p & Purity.Native)>0){
                    nativeE ++;
                }

            }
            classes ++;
        }

        System.out.print(table.dumpImports());


        System.out.println("class " + classes);
        System.out.println("method "+method);
        System.out.println("unknown "+unknown);
        System.out.println("stateless "+stateless);
        System.out.println("stateful "+stateful);
        System.out.println("modifier "+modifier);

        System.out.println("fieldM "+fieldM);
        System.out.println("staticM "+staticM);
        System.out.println("argM "+argM);
        System.out.println("nativeE "+nativeE);

        System.out.println(Joiner.on("\n").join(sb));
    }
}
