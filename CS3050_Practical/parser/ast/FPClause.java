package parser.ast;

import parser.ast.FPHead;
import parser.ast.FPBody;

public class FPClause {
  public final FPHead head;
  public final FPBody body; 

  public FPClause(FPHead hd) {
    head = hd;
    body = null;
  }

  public FPClause(FPHead hd, FPBody bd) {
    head = hd;
    body = bd;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (head != null) {
      sb.append(head.toString());
    } else {
      sb.append("?- ");
    }
    //handling rules
    if (head != null && body != null) {
      sb.append(" :- ");
    }
    //handling facts
    if (body != null) { 
      sb.append(body.toString());
    };
    //all clauses are terminated by a full stop
    return sb.append(".").toString();
  }
}