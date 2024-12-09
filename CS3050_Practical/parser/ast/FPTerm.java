package parser.ast;

import java.util.ArrayList;

import parser.ast.TKind;

public class FPTerm {
  public final TKind kind;
  public final String name;
  public final ArrayList<FPTerm> args;

  public FPTerm() {
    this.kind = TKind.BANG;
    this.name = "!";
    this.args = null;
  }
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    FPTerm other = (FPTerm) obj;

    // Check name equality (handling nulls)
    if (this.name == null) {
      if (other.name != null) return false;
    } else if (!this.name.equals(other.name)) {
      return false;
    }

    // Check kind equality (handling nulls)
    if (this.kind != other.kind) return false;

    // Check args equality (handling nulls)
    if (this.args == null) {
      if (other.args != null) return false; // One null, the other not
    } else if (other.args == null || this.args.size() != other.args.size()) {
      return false; // Mismatch in size or null status
    } else {
      // Recursively check each argument
      for (int i = 0; i < this.args.size(); i++) {
        FPTerm thisArg = this.args.get(i);
        FPTerm otherArg = other.args.get(i);

        if (thisArg == null) {
          if (otherArg != null) return false; // One null, the other not
        } else if (!thisArg.equals(otherArg)) {
          return false; // Argument mismatch
        }
      }
    }

    return true; // All checks passed
  }


  public FPTerm(FPTerm t) {
    this.kind = TKind.NOT;
    this.name = "\\+";
    this.args = new ArrayList<FPTerm>();
    this.args.add(t);
  }

  public FPTerm(TKind k, String n) {
    this.kind = k;
    this.name = n;
    this.args = null;
  }

  public FPTerm(TKind k, String n, ArrayList<FPTerm> as) {
    this.kind = k;
    this.name = n;
    this.args = as;
  }

  public String toString() {
    if (this.kind == TKind.CONST || this.kind == TKind.CTERM) {
      StringBuilder sb = new StringBuilder(name);
      if (args != null && args.size() > 0) {
        sb.append("(");
        for (int i = 0; i < args.size(); i++) {
          if (i > 0) { sb.append(", "); };
          sb.append(args.get(i));
        }
        return sb.append(")").toString();
      } else {
        return sb.toString();
      }
    } else if (this.kind == TKind.NOT) {
      return name + " " + args.get(0);
    } else {
      return name;
    }
  }
}