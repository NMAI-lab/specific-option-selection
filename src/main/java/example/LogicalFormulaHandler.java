package example;

import example.objs.Lattice;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;
import jason.util.Pair;
import org.logicng.formulas.Formula;
import org.logicng.io.parsers.ParserException;

import java.util.*;

public class LogicalFormulaHandler {

    List<Pair<Literal, Literal>> allContexts;
    List<Pair<Literal, Literal>> contextWithTerms;
    List<Pair<Literal, Literal>> contextWithoutTerms;
    List<Pair<Literal, Literal>> contextVars;

    Lattice lattice;

    public LogicalFormulaHandler() {
        allContexts = new ArrayList<>();
        contextWithTerms = new ArrayList<>();
        contextWithoutTerms = new ArrayList<>();
        contextVars = new ArrayList<>();
        lattice = new Lattice();
    }


    public Boolean isMoreSpecific(Agent ag, LogicalFormula f1, LogicalFormula f2) throws ParserException {
        FormulaSolver solver = new FormulaSolver();

        LogicalFormula f1Expanded = expandFormula(f1, ag);
        LogicalFormula f2Expanded = expandFormula(f2, ag);

        Pair<LogicalFormula, LogicalFormula> p = alignVariables(f1Expanded,f2Expanded);

        LogicalFormula f1ExpandedAligned = p.getFirst();
        LogicalFormula f2ExpandedAligned = p.getSecond();

        Unifier unifier = new Unifier();

        Iterator<Unifier> f2Unifs = f2ExpandedAligned.logicalConsequence(ag, unifier);

        while (f2Unifs.hasNext()) {
            Unifier f2Unif = f2Unifs.next();

            Term term1 = f1ExpandedAligned.capply(f2Unif);
            Term term2 = f2ExpandedAligned.capply(f2Unif);

            Formula formula1 = solver.expressionToFormula((LogicalFormula) term1);
            Formula formula2 = solver.expressionToFormula((LogicalFormula) term2);

            if (solver.implies(formula1, formula2) ) {
                return true;
            }
        }
        return false;
    }


    public LogicalFormula expandFormula(LogicalFormula logicalFormula, Agent ag ){
        if (logicalFormula instanceof NumberTerm) {
            return logicalFormula;
        } else if (logicalFormula instanceof LogExpr) {
            return expandFormula((LogExpr) logicalFormula, ag);
        } else if(logicalFormula instanceof Literal) {
            return expandFormula((Literal) logicalFormula, ag);
        } else {
            return logicalFormula;
        }
    }


    public LogicalFormula expandFormula(Literal literal, Agent ag) {
        Unifier unifier = new Unifier();
        final Iterator<Literal> il = ag.getBB().getCandidateBeliefs(literal, unifier);
        if (il != null) {
            while(il.hasNext()) {
                Literal belInBB = il.next();
                belInBB.clearAnnots();
                if (belInBB.isRule()) {
                    Rule rule = (Rule) belInBB;
                    Literal cloneAnnon = (Literal) literal.capply(unifier);
                    cloneAnnon.makeVarsAnnon();
                    Unifier ruleUn = new Unifier();
                    if (ruleUn.unifiesNoUndo(cloneAnnon, rule)) {
                        LogicalFormula body = rule.getBody();
                        return expandFormula(body, ag);
                    }
                }
            }
        }
        return literal;
    }


    public LogicalFormula expandFormula(LogExpr logExpr, Agent ag) {
        LogicalFormula lhs = expandFormula(logExpr.getLHS(), ag);
        LogicalFormula rhs = expandFormula(logExpr.getRHS(), ag);

        return new LogExpr(lhs, logExpr.getOp(), rhs);
    }


    public Pair<LogicalFormula, LogicalFormula> alignVariables(LogicalFormula f1, LogicalFormula f2) {
        LogicalFormula form1 = (LogicalFormula) f1.clone();
        LogicalFormula form2 = (LogicalFormula) f2.clone();

        List<Literal> literals1 = extractLiterals(form1);
        List<Literal> literals2 = extractLiterals(form2);


        Map<String,String> mappingVars1 = new HashMap<>();
        Map<String,String> mappingVars2 = new HashMap<>();

        //first we want to create the mapping for both
        int indexVarASCII = 0;
        for (Literal lit1 : literals1) {
            for (Literal lit2 : literals2) {
                if(lit1.getPredicateIndicator().equals(lit2.getPredicateIndicator())){
                    char var = (char) ('A' + indexVarASCII);

                    createMappingVariables(lit1, var, mappingVars1);
                    createMappingVariables(lit2, var, mappingVars2);
                    indexVarASCII++;

                }
            }
        }
        // now that we did the mapping, we just want to replace the vars as unifiers won't work for var to var
        form1 = deepReplaceVariables(form1, mappingVars1);
        form2 = deepReplaceVariables(form2, mappingVars2);

        return new Pair<>(form1, form2);
    }

    public List<Literal> extractLiterals(LogicalFormula formula){
        List<Literal> extractedLiterals = new ArrayList<>();
        Literal literal = (Literal) formula;
        if(literal.hasTerm()) {
            List<Term> termList = literal.getTerms();
            boolean lastLiteral = true;
            for (Term term : termList) {
                if(literal instanceof LogExpr || literal instanceof RelExpr){
                    if(((Literal) literal.getTerm(0)).getArity() == 0 && ((Literal) literal.getTerm(1)).getArity() == 0){
                        // in this case, it is the last LogExpr/RelExpr
                        break;
                    } else if(term instanceof VarTerm) {
                        extractedLiterals.add((Literal) term);
                    }
                }
                if(term instanceof Literal){
                    if(((Literal) term).getArity() > 0){
                        lastLiteral = false;
                        extractedLiterals.addAll(extractLiterals((LogicalFormula) term));
                    }
                }
            }
            if(lastLiteral){
                extractedLiterals.add(literal);
            }
        }
        return extractedLiterals;
    }


    public void createMappingVariables(Literal literal, char newVarASCII, Map<String, String> mappingVars){
        //here we can't use Unifier, as capply(un) isn't working for a VarTerm to another VarTerm.
        // we will build a Mapping
        List<Term> termList = literal.getTerms();

        for (int i = 0; i < termList.size(); i++) {
            Term term = termList.get(i);
            if (term.isVar()) {
                if(!mappingVars.containsKey(term.toString())){
                    mappingVars.put(term.toString(), String.valueOf(newVarASCII + String.valueOf(i)));
                }
            }
        }
    }


    public LogicalFormula deepReplaceVariables(LogicalFormula formula, Map<String, String> mappingVars) {
        if (formula instanceof LogExpr) {
            LogExpr logExpr = (LogExpr) formula;
            LogicalFormula t0 = deepReplaceVariables((LogicalFormula) logExpr.getTerm(0), mappingVars);
            LogicalFormula t1 = deepReplaceVariables((LogicalFormula) logExpr.getTerm(1), mappingVars);
            return new LogExpr(t0, logExpr.getOp(), t1);
        }

        if (formula instanceof RelExpr) {
            RelExpr relExpr = (RelExpr) formula;
            LogicalFormula t0 = deepReplaceVariables((LogicalFormula) relExpr.getTerm(0), mappingVars);
            LogicalFormula t1 = deepReplaceVariables((LogicalFormula) relExpr.getTerm(1), mappingVars);
            return new RelExpr(t0, relExpr.getOp(), t1);
        }

        if (formula instanceof Literal) {
            Literal lit = (Literal) formula;
            if (lit instanceof VarTerm) {
                String replacement = mappingVars.get(lit.toString());
                if (replacement != null) {
                    return ASSyntax.createVar(replacement);
                } else {
                    return lit;
                }
            }

            if (lit.hasTerm()) {
                List<Term> newTerms = new ArrayList<>();
                for (Term t : lit.getTerms()) {
                    if (t instanceof Literal) {
                        newTerms.add(deepReplaceVariables((LogicalFormula) t, mappingVars));
                    } else if (t.isVar()) {
                        String replacement = mappingVars.get(t.toString());
                        if (replacement != null) {
                            newTerms.add(ASSyntax.createVar(replacement));
                        } else {
                            newTerms.add(t);
                        }
                    } else {
                        newTerms.add(t);
                    }
                }
                return ASSyntax.createLiteral(lit.getFunctor(), newTerms.toArray(new Term[0]));
            }

            return lit;
        }
        return formula;
    }

}
