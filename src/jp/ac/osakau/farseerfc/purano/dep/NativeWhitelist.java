package jp.ac.osakau.farseerfc.purano.dep;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class NativeWhitelist {
    private static final Set<String> whitelist = new HashSet<>(Arrays.asList(
            "Iterator.next",
            "Iterator.hasNext",
            "HashSet",
            "Set.add",
            "StringBuilder.toString",
            "StringBuilder.append",
            "List.add",
            "List.get",
            "Arrays.",
            "ArrayList.",
            "HashSet.",
            "Object.",
            "List.size"
    ));

    public static boolean filterOut(String fullIdentifier) {
        for (String value : whitelist) {
            if (fullIdentifier.contains(value)) {
                return true;
            }
        }

        return false;
    }
}
