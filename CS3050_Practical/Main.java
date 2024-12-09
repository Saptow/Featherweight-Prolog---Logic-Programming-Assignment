package interpreter;
import parser.ast.*;
import utils.*;
import java.io.FileReader;
import java.io.StringReader;
import java.util.*;
import parser.FProlog;

public class Main {
  public static String formatResult(HashMap<String, FPTerm> substitutions) {
    if (substitutions.isEmpty()) {
      return "yes"; // no substitutions mean success
    }

    StringBuilder sb = new StringBuilder();
    for (String variable : substitutions.keySet()) {
      if (sb.length() > 0) {
        sb.append(", "); // separator
      }
      sb.append(variable).append(" = ").append(substitutions.get(variable));
    }
    return sb.toString();
  }

  public static void main(String[] args) {
    boolean trace = args.length > 1 && args[1].equals("--trace");
      try(Scanner s = new Scanner(new FileReader(args[0]));) {
      StringBuilder sb = new StringBuilder();
      while (s.hasNext()) {
        sb.append(s.next());
      }
      String str = sb.toString();

      FPProg ast = new FProlog(new StringReader(str)).P();
      System.out.println(ast.toString());

      interpret(ast,trace);

    } catch (Exception e) {
      System.err.println(e.toString());
    }
  }
  public static void interpret(FPProg program, boolean trace) {
    KnowledgeBase kb = new KnowledgeBase();
    ArrayList<FPClause> clauses = program.cs;

    for (FPClause clause : clauses) {
      if (clause.head == null) { // query
        FPBody queryBody = clause.body;

        if (queryBody == null) {
          // empty query (special case)
          System.out.println("Empty query body.");
        } else {
          utils.resetUniqueNamePool();
          List<Substitution> results = SLDResolver.resolveQuery(queryBody, kb, trace);

          if (results.isEmpty()) {
            System.out.println("no");
          } else {
            // check if results empty
            boolean allEmpty = results.stream()
                    .allMatch(result -> result.getMap().isEmpty());

            if (allEmpty) {
              System.out.println("yes");
            } else {
              // print each non-empty substitution result
              for (Substitution result : results) {
                if (!result.getMap().isEmpty()) {
                  System.out.println(formatResult(result.getMap()));
                }
              }
            }
          }
        }
      } else {
        // handling of knowledge base
        if (clause.body == null) {
          kb.addFact(clause); // fact
        } else {
          kb.addRule(clause); // rule
        }
      }
    }
  }
  }