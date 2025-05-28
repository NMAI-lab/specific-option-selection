package example;

import jason.asSemantics.Agent;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.LogExpr;
import jason.asSyntax.VarTerm;
import jason.util.Pair;

//using Visitor Pattern
public interface LogicalFormulaHandler {
    void handle(LiteralImpl literal, Agent ag);
    void handle(LogExpr expr, Agent ag);
    void handle(VarTerm term, Agent ag);
}
