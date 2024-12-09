package utils;

import parser.ast.FPTerm;
import parser.ast.TKind;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Substitution {
    private final HashMap<String, FPTerm> mapping;

    public Substitution() {
        this.mapping = new HashMap<>();
    }

    public void add(String var, FPTerm term) {
        mapping.put(var, term);
    }

    public boolean isEmpty() {
        return mapping.isEmpty();
    }
    public FPTerm get(String var) {
        return mapping.get(var);
    }

    // recursive substitution to a term
    public FPTerm apply(FPTerm term) {
        if (term.kind == TKind.IDENT) {
            FPTerm substitution = this.get(term.name);
            return substitution != null ? apply(substitution) : term;
        } else if (term.kind == TKind.CTERM) {
            ArrayList<FPTerm> newArgs = new ArrayList<>();
            for (FPTerm arg : term.args) {
                newArgs.add(apply(arg));
            }
            return new FPTerm(term.kind, term.name, newArgs);
        }
        return term; // constant/unmatched term
    }

    public static Substitution createFrom(Substitution original) {
        Substitution newSubstitution = new Substitution();

        // copy each entry from original
        for (Map.Entry<String, FPTerm> entry : original.getMap().entrySet()) {
            // deep copy
            FPTerm copiedTerm = deepCopyFPTerm(entry.getValue());
            newSubstitution.add(entry.getKey(), copiedTerm);
        }

        return newSubstitution;
    }

    // Helper method for deep copy
    private static FPTerm deepCopyFPTerm(FPTerm term) {
        if (term.args == null) {
            return new FPTerm(term.kind, term.name);
        } else {
            ArrayList<FPTerm> newArgs = new ArrayList<>();
            for (FPTerm arg : term.args) {
                newArgs.add(deepCopyFPTerm(arg));
            }
            return new FPTerm(term.kind, term.name, newArgs);
        }
    }

    public HashMap<String, FPTerm> getMap() {
        return mapping;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, FPTerm> entry : mapping.entrySet()) {
            if (!first) sb.append(", ");
            sb.append(entry.getKey()).append(" = ").append(entry.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
