package example;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;

import java.util.Iterator;

public class LiteralImplComparator extends LiteralImpl{


    public LiteralImplComparator(Literal lit) {
        super(lit);
    }

    public Literal expand(Agent ag, Unifier unifier) {
        final Iterator<Literal> il = ag.getBB().getCandidateBeliefs(this, unifier);
        if (il != null) {
            while(il.hasNext()) {
                Literal belInBB = il.next();
                belInBB.clearAnnots();
                if (belInBB.isRule()) {
                    Rule rule = (Rule)belInBB;
                    LogExprComparator body = new LogExprComparator((LogExpr) rule.getBody());
                    return body.expand(ag, unifier);
                }
                else {
                    return belInBB;
                }
            }
        }
        return this;
    }
}
