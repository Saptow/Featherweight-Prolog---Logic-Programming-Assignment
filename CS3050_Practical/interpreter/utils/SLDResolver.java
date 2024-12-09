package utils;

import parser.ast.*;
import java.util.*;

public class SLDResolver {

    public static List<Substitution> resolveQuery(FPBody queryBody, KnowledgeBase kb, boolean trace) {
        List<Substitution> solutions = new ArrayList<>();
        Stack<Substitution> substitutionStack = new Stack<>();
        Substitution initialSubstitution = new Substitution();

        //wrap everything in the backtracking logic
        resolveQueryWithBacktracking(queryBody, kb, initialSubstitution, substitutionStack, trace, solutions);
        return solutions;
    }

    public static boolean resolveQueryWithBacktracking(FPBody queryBody, KnowledgeBase kb,
                                                       Substitution currentSubstitutions,
                                                       Stack<Substitution> substitutionStack,
                                                       boolean trace,
                                                       List<Substitution> solutions) {
        if (queryBody.ts.isEmpty()) {
            // base case: sub-goals are all resolved
            if (trace) System.out.println("Substitution: "+currentSubstitutions);
            solutions.add(Substitution.createFrom(currentSubstitutions)); // save sol
            return true;
        }

        // process first goal
        FPTerm currentGoal = queryBody.ts.get(0);
        FPBody remainingGoals = new FPBody(new ArrayList<>(queryBody.ts.subList(1, queryBody.ts.size())));
        boolean anyResolution = false;

        if (trace) System.out.println("Call: " + currentGoal);

        // handling of write
        if (currentGoal.name.equals("write") && currentGoal.args.size() == 1) {
            FPTerm arg = currentSubstitutions.apply(currentGoal.args.get(0));
            System.out.println(arg);
            return resolveQueryWithBacktracking(remainingGoals, kb, currentSubstitutions, substitutionStack, trace, solutions);
        }

        // resolving through facts
        ArrayList<FPHead> possibleFacts = kb.getFacts(currentGoal.name);
        for (FPHead fact : possibleFacts) {
            // clone current substitution just in case
            Substitution tempSubstitutions = Substitution.createFrom(currentSubstitutions);

            // turn into fpterm for function handling
            FPTerm newFact = fact.toTerm();

            // unify current goal with fact with substitutions
            Substitution newSubstitutions = Unifier.unify(currentGoal, newFact, tempSubstitutions);

            if (newSubstitutions != null) {

                // resolve remaining goals w/ updated substitutions
                if (resolveQueryWithBacktracking(remainingGoals, kb, newSubstitutions, substitutionStack, trace, solutions)) {
                    anyResolution = true;
                }
            }
        }

        // resolving through rules
        ArrayList<FPBody> possibleRules = kb.getRules(currentGoal.name);
        for (FPBody ruleBody : possibleRules) {
            HashMap<String, String> renamingMap = new HashMap<>();

            FPBody renamedRuleBody = new FPBody(new ArrayList<>());
            for (FPTerm ruleTerm : ruleBody.ts) {
                renamedRuleBody.ts.add(utils.renameVariables(ruleTerm, renamingMap));
            }

            Substitution tempSubstitutions = Substitution.createFrom(currentSubstitutions);

            if (resolveQueryWithBacktracking(renamedRuleBody, kb, tempSubstitutions, new Stack<>(), trace, new ArrayList<>())) {
                if (resolveQueryWithBacktracking(remainingGoals, kb, tempSubstitutions, substitutionStack, trace, solutions)) {
                    anyResolution = true;
                }
            }
        }

        // no resolution is possible
        if (!anyResolution && trace) System.out.println("Fail: " + currentGoal);
        return anyResolution;

    }
}
