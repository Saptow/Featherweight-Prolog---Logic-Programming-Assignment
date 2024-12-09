package utils;

import parser.ast.FPBody;
import parser.ast.FPClause;
import parser.ast.FPHead;
import parser.ast.FPTerm;

import java.util.ArrayList;
import java.util.HashMap;

public class KnowledgeBase {
    private final HashMap<String, ArrayList<FPHead>> facts;
    private final HashMap<String, ArrayList<FPBody>> rules;

    public KnowledgeBase() {
        this.facts = new HashMap<>();
        this.rules = new HashMap<>();
    }

    public void addFact(FPClause c) {
        String functor = c.head.name; // Functor of the fact
        facts.putIfAbsent(functor, new ArrayList<>());
        facts.get(functor).add(c.head);
    }

    public void addRule(FPClause c) {
        FPHead head = c.head;
        FPBody body = c.body;


        rules.putIfAbsent(head.name, new ArrayList<>());
        rules.get(head.name).add(body);
    }
    public ArrayList<FPHead> getFacts(String functor) {
        return facts.getOrDefault(functor, new ArrayList<>());
    }

    public ArrayList<FPBody> getRules(String s) {
        return rules.getOrDefault(s, new ArrayList<>());
    }
}
