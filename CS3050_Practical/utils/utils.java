package utils;

import parser.ast.FPTerm;
import parser.ast.TKind;

import java.util.ArrayList;
import java.util.HashMap;

// FOR PREVENTING VARIABLE CAPTURE //
public class utils {

    public static HashMap<String,Integer> availableNames= new HashMap<>();
    public static String uniqueName(String name){
        StringBuilder s=new StringBuilder();
        if (availableNames.containsKey(name)){
            Integer temp=availableNames.get(name);
            //update value
            availableNames.put(name,temp+1);
            return name+temp.toString();
        }else{
            availableNames.put(name,1);
            return name+"0";
        }
    }
    public static FPTerm renameVariables(FPTerm term, HashMap<String, String> renamingMap) {
        if (term.kind == TKind.IDENT) {
            String newName = renamingMap.computeIfAbsent(term.name, utils::uniqueName);
            return new FPTerm(TKind.IDENT,newName,term.args);
        } else if (term.kind == TKind.CTERM) {
            ArrayList<FPTerm> newArgs = new ArrayList<>();
            for (FPTerm arg : term.args) {
                newArgs.add(renameVariables(arg, renamingMap));
            }
            return new FPTerm(TKind.CTERM,term.name, newArgs);
        }
        return term;
    }
    // just in case
    public static void resetUniqueNamePool() {
        availableNames.clear();
    }
}

