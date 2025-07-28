package example;

import example.types.Lattice;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;
import jason.util.Pair;
import org.logicng.formulas.Formula;
import org.logicng.io.parsers.ParserException;

import java.util.*;

public class LogicalFormulaHandler {

    private final Lattice lattice;

    private final FormulaSolver solver;

    public LogicalFormulaHandler() {
        lattice = new Lattice();
        solver = new FormulaSolver();
    }

    public Lattice getLattice() {
        return lattice;
    }

    /*
    Returns true if the formula f2 is a logicalConsequence of f1
    */
    public Boolean isALogicalConsequenceof(Agent ag, LogicalFormula f1, LogicalFormula f2, Unifier originalUn) throws ParserException {
        if(f1 == null) return false;
        if(f2 == null) return true;

        LogicalFormula f1Expanded = expandFormula(f1, ag);
        LogicalFormula f2Expanded = expandFormula(f2, ag);

        Pair<LogicalFormula, LogicalFormula> p = alignVariables(f1Expanded,f2Expanded, originalUn);
        LogicalFormula f1ExpandedAligned = p.getFirst();
        LogicalFormula f2ExpandedAligned = p.getSecond();

        Iterator<Unifier> f2Unifs = f2ExpandedAligned.logicalConsequence(ag, originalUn);

        FormulaSolver solver = new FormulaSolver();
        while (f2Unifs.hasNext()) {
            Unifier f2Unif = f2Unifs.next();
            for (VarTerm var : f2Unif) {
                if(f2Unif.get(var) instanceof Literal) {
                    ((Literal) f2Unif.get(var)).clearAnnots();
                }
            }
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

    /*
    For rules, we need to expand the formula to have the fully expanded form of this one
     */
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

    /*
    This function will align the variables of two given formulas.
    Given f1 = pred(a,B,C,D) and pred(T,m,n,YES), it will return the following pair : <pred(a,A1,A2,A3), pred(A0,m,n,A3)>
     */
    public Pair<LogicalFormula, LogicalFormula> alignVariables(LogicalFormula f1, LogicalFormula f2, Unifier originalUn) {
        LogicalFormula form1 = (LogicalFormula) f1.clone();
        LogicalFormula form2 = (LogicalFormula) f2.clone();

        List<Literal> literals1 = extractLiterals(form1);
        List<Literal> literals2 = extractLiterals(form2);

        Map<String,String> mappingVars1 = new HashMap<>();
        Map<String,String> mappingVars2 = new HashMap<>();

        //first we want to create the mapping for both
        // we start mapping by Internal Actions because they have tye on their parameters
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
        mappingVars1.putAll(mappingVars2);
        originalUn = deepReplaceVariablesUnifier(originalUn, mappingVars1);

        return new Pair<>(form1, form2);
    }


    /*
    This function extract of a formula all the Structures (by structures,we mean all literals that have a predicateIndicator and terms).
     */
    public List<Literal> extractLiterals(LogicalFormula logicalFormula){
        List<Literal> extractedLiterals = new ArrayList<>();
        if(logicalFormula instanceof VarTerm) {
            extractedLiterals.add((Literal) logicalFormula);
            return extractedLiterals;
        }

        Literal literal = (Literal) logicalFormula;
        if(!literal.hasTerm()) {
            return extractedLiterals;
        }
        List<Term> termList = literal.getTerms();

        if(literal instanceof LiteralImpl || literal instanceof InternalActionLiteral){
            extractedLiterals.add(literal);
        }
        for(Term term : termList){
            extractedLiterals.addAll(extractLiteralsNested((Literal) term));
        }

        return extractedLiterals;
    }

    public List<Literal> extractLiteralsNested(LogicalFormula logicalFormula){
        List<Literal> extractedLiterals = new ArrayList<>();

        if(logicalFormula instanceof NumberTerm) {
            return extractedLiterals;
        }

        Literal literal = (Literal) logicalFormula;
        if(!literal.hasTerm()) {
            return extractedLiterals;
        }
        List<Term> termList = literal.getTerms();

        if(literal instanceof LiteralImpl || literal instanceof InternalActionLiteral){
            extractedLiterals.add(literal);
        }
        for(Term term : termList){
            extractedLiterals.addAll(extractLiteralsNested((LogicalFormula) term));
        }
        return extractedLiterals;
    }

    /*
    This function will create the mapping of variables contained in a literal given for a char given.
    for example, for createMappingVariables(pred(a,B,C,d), 'H', null), we will have the following mapping :
    {
        B -> H1,
        C -> H2
    }
     */
    public void createMappingVariables(Literal literal, char newVarASCII, Map<String, String> mappingVars){
        //here we can't use Unifier, as capply(un) isn't working for a VarTerm to another VarTerm.
        // we will build a Mapping
        if(literal.hasTerm()) {
            List<Term> termList = literal.getTerms();

            for (int i = 0; i < termList.size(); i++) {
                Term term = termList.get(i);
                if (term.isVar()) {
                    if(!mappingVars.containsKey(term.toString())){
                        mappingVars.put(term.toString(), String.valueOf(newVarASCII + String.valueOf(i)));
                    }
                }
            }
        } else {
            if (literal.isVar()) {
                if(!mappingVars.containsKey(literal.toString())){
                    mappingVars.put(literal.toString(), String.valueOf(newVarASCII));
                }
            }
        }

    }

    /*
    Given an formula and a mapping, this function will replace the variables contained in the formula by their value in the mapping
    and return the new formula.
     */
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

        if(formula instanceof InternalActionLiteral){
            InternalActionLiteral ia = (InternalActionLiteral) formula;
            if (ia.hasTerm()) {
                List<Term> newTerms = new ArrayList<>();
                int termSize = ia.getTerms().size();
                for(int i = 0; i < termSize; i++) {
                    Term term = ia.getTerms().get(i);
                    LogicalFormula newTerm = deepReplaceVariables((LogicalFormula) term, mappingVars);
                    ia.setTerm(i, newTerm);
                }
            }
            return ia;
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

    public Unifier deepReplaceVariablesUnifier(Unifier originalUn, Map<String, String> mappingVars) {
        Iterator<VarTerm> iv = originalUn.iterator();
        Map<VarTerm, Term> newFunc = new HashMap<>();
        while(iv.hasNext()) {
            VarTerm term = iv.next();
            String replacement = mappingVars.get(term.toString());
            if (replacement != null) {
                newFunc.put(ASSyntax.createVar(replacement), originalUn.get(term));
            } else {
                newFunc.put(term, originalUn.get(term));
            }
        }
        originalUn.setMap(newFunc);
        return originalUn;
    }

}
