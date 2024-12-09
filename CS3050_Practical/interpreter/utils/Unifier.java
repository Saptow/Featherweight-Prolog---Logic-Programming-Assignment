package utils;

import parser.ast.FPTerm;
import parser.ast.TKind;
public class Unifier {

    public static Substitution unify(FPTerm term1, FPTerm term2, Substitution subs) {
        // applying substitution to both terms
        term1 = subs.apply(term1);
        term2 = subs.apply(term2);

        // identical terms aft substitution
        if (term1.equals(term2)) {
            return subs;
        }

        // unifying variables/constants with a term
        if (term1.kind == TKind.IDENT) {
            return unifyVariable(term1, term2, subs);
        }
        if (term2.kind == TKind.IDENT) {
            return unifyVariable(term2, term1, subs); // swap
        }

        // unifying compound terms (CTERM)
        if (term1.kind == TKind.CTERM && term2.kind == TKind.CTERM) {
            if (!term1.name.equals(term2.name) || term1.args.size() != term2.args.size()) {
                return null; // functor mismatch or different arities
            }

            for (int i = 0; i < term1.args.size(); i++) {
                subs = unify(term1.args.get(i), term2.args.get(i), subs);
                if (subs == null) {
                    return null; // failed unification
                }
            }
            return subs;
        }

        // no unification
        return null;
    }

    private static Substitution unifyVariable(FPTerm var, FPTerm term, Substitution subs) {
        if (occursCheck(var, term)) {
            return null; // var occurs; unification fails
        }

        // add or update substitution
        subs.add(var.name, term);
        return subs;
    }

    // occurscheck to ensure a variable does not unify with a term containing itself
    private static boolean occursCheck(FPTerm var, FPTerm term) {
        if (term.kind == TKind.IDENT) {
            return var.name.equals(term.name);
        }
        if (term.kind == TKind.CTERM) {
            for (FPTerm arg : term.args) {
                if (occursCheck(var, arg)) {
                    return true; // check recursively
                }
            }
        }
        return false;
    }

}
