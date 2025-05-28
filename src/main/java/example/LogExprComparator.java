package example;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;

public class LogExprComparator extends LogExpr{

    public LogExprComparator(LogicalFormula f1, LogicalOp oper, LogicalFormula f2) {
        super(f1, oper, f2);
    }

    public LogExprComparator(LogExpr expr) {
        super(expr.getLHS(), expr.getOp(), expr.getRHS());
    }

    public Literal expand(Agent ag, Unifier unifier) {
        LogicalFormula lhs = expandRecurs(this.getLHS(), ag, unifier);
        LogicalFormula rhs = expandRecurs(this.getRHS(), ag, unifier);

        return new LogExprComparator(lhs, this.getOp(), rhs);
    }

    private LogicalFormula expandRecurs(LogicalFormula f, Agent ag, Unifier unifier){
        if (f instanceof LiteralImpl) {
            return  (new LiteralImplComparator((LiteralImpl) f)).expand(ag, unifier);
        } else if (f instanceof LogExpr) {
            return  (new LogExprComparator((LogExpr) f)).expand(ag, unifier);
        } else {
            return f;
        }
    }

}
