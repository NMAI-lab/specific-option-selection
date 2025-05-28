package example;

import example.objs.Lattice;
import example.objs.LiteralNode;
import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.*;
import jason.util.Pair;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

import java.util.*;

public class LogicalFormulaHandlerImpl implements LogicalFormulaHandler {

    List<Pair<Literal, Literal>> allContexts;
    List<Pair<Literal, Literal>> contextWithTerms;
    List<Pair<Literal, Literal>> contextWithoutTerms;
    List<Pair<Literal, Literal>> contextVars;

    Lattice lattice;

    public LogicalFormulaHandlerImpl() {
        allContexts = new ArrayList<>();
        contextWithTerms = new ArrayList<>();
        contextWithoutTerms = new ArrayList<>();
        contextVars = new ArrayList<>();
        lattice = new Lattice();
    }

    @Override
    public void handle(LiteralImpl literal, Agent ag) {
        if (literal.hasTerm()) {
            Pair<Literal, Literal> pair = new Pair<Literal, Literal>(literal, literal);
            contextWithTerms.add(pair);
        } else {
            Unifier unifier = new Unifier();
            Literal expanded = (new LiteralImplComparator(literal)).expand(ag, unifier);
            Pair<Literal, Literal> pair = new Pair<Literal, Literal>(literal, expanded);
            contextWithoutTerms.add(pair);
        }
    }

    @Override
    public void handle(LogExpr logExpr, Agent ag) {
        Unifier unifier = new Unifier();
        Literal expanded = (new LogExprComparator(logExpr)).expand(ag, unifier);
        Pair<Literal, Literal> pair = new Pair<Literal, Literal>(logExpr, expanded);
        if(!logExpr.isGround()) {
            contextVars.add(pair);
        } else {
            contextWithoutTerms.add(pair);
        }
    }

    @Override
    public void handle(VarTerm varTerm, Agent ag) {
        contextVars.add(new Pair<>(varTerm, varTerm));
    }

    // For the guards that contain terms, we want to compare them only if they have the same predicate &
    // at least 1 common belief they can unifie with.
    // For example : test(a,b,c,d) is comparable with test(a,X,c,Y) as they have the same predicate "test/4"
    // and 1 common belief : test(a,b,c,d) (with the Unifier : {X -> b, Y -> d})
    // but test(a,b,c,d) won't be comparable with test(a,X,X,X) as no unifier can satisfy a common belief.
    public void sortContextWithTerms(Agent ag) {
        Unifier unifier = new Unifier();
        // We want to create a HashMap in which one we will have as key the literal and in value
        // a list of pairs  with the beliefs that verify this literal and the unifier that corresponds.
        HashMap<Literal, List<Pair<Literal, Unifier>>> mapUnifiers = new HashMap<>();
        contextWithTerms.forEach( pair -> {
                    Literal ctx = pair.getFirst();
                    final Iterator<Unifier> iu = ctx.logicalConsequence(ag,unifier);
                    List<Pair<Literal, Unifier>> pairList = new ArrayList<>();
                    if(iu != null){
                        while(iu.hasNext()){
                            Unifier un = iu.next();
                            pairList.add(new Pair<>((Literal) ctx.capply(un), un));
                        }
                    }
                    mapUnifiers.put(ctx, pairList);
                }
        );

        // Now we want to compare every literal together
        for (Literal key1 : mapUnifiers.keySet()) {
            for (Literal key2 : mapUnifiers.keySet()) {
                if(key1.equals(key2)) continue;

                // If they don't have the same predicate, we don't compare them
                if(!key1.getPredicateIndicator().equals(key2.getPredicateIndicator())) continue;

                // We get all the beliefs that respectively satisfy those literals from the BB
                List<Pair<Literal, Unifier>> listPair1 = mapUnifiers.get(key1);
                List<Pair<Literal, Unifier>> listPair2 = mapUnifiers.get(key2);
                // We get the node for this literal from the lattice
                LiteralNode literalNode1 = lattice.findOrCreateLiteralNode(key1);
                LiteralNode literalNode2 = lattice.findOrCreateLiteralNode(key2);

                // If they have at least one common belief that satisfies them
                if(listInOtherList(listPair1, listPair2)){
                    //then we assume that the one with the more terms is the more general
                    long nbVarTerms1 = literalNode1.getValue().getTerms().stream().filter(term -> term.isVar()).count();
                    long nbVarTerms2 = literalNode2.getValue().getTerms().stream().filter(term -> term.isVar()).count();
                    if (nbVarTerms1 > nbVarTerms2) {
                        literalNode1.addMoreSpecific(literalNode2); // literalNode1 is more specific than literalNode2
                        literalNode2.addMoreGeneral(literalNode1); // literalNode2 is more general than literalNode1
                    } else if (nbVarTerms1 < nbVarTerms2) {
                        literalNode1.addMoreGeneral(literalNode2);
                        literalNode2.addMoreSpecific(literalNode1);
                    }
                }
            }
        }

    }

    public void sortContextWithoutTerms(Agent ag) {
        FormulaFactory factory = new FormulaFactory();
        PropositionalParser parser = new PropositionalParser(factory);

        for (int i = 0; i < contextWithoutTerms.size(); i++) {
            for (int j = i+1; j < contextWithoutTerms.size(); j++) {
                if (i != j){
                    try {
                        Literal l1 = contextWithoutTerms.get(j).getFirst();
                        Literal k1 = contextWithoutTerms.get(i).getFirst();
                        Literal l2 = contextWithoutTerms.get(j).getSecond();
                        Literal k2 = contextWithoutTerms.get(i).getSecond();

                        Formula f1= parser.parse(l2.toString()).cnf();
                        Formula f2= parser.parse(k2.toString()).cnf();

                        SortedSet<Variable> vars1 = f1.variables();
                        SortedSet<Variable> vars2 = f2.variables();

                        LiteralNode lLiteralNode = lattice.findOrCreateLiteralNode(l1);
                        LiteralNode kLiteralNode = lattice.findOrCreateLiteralNode(k1);

                        // check if they have at least one variable in common then they are comparable
                        if(vars1.stream().anyMatch(vars2::contains) || vars2.stream().anyMatch(vars1::contains)) {
                            if(implies(f1,f2)){
                                lLiteralNode.addMoreGeneral(kLiteralNode); // lLiteralNode is more general than kLiteralNode
                                kLiteralNode.addMoreSpecific(lLiteralNode); // kLiteralNode is more specific than lLiteralNode
                            } else if (implies(f2,f1)) {
                                lLiteralNode.addMoreSpecific(kLiteralNode); // lLiteralNode is more specific than kLiteralNode
                                kLiteralNode.addMoreGeneral(lLiteralNode);
                            }
                        }
                    } catch (ParserException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }
    }

    public void sortContextWithVars(Agent ag){
        FormulaFactory factory = new FormulaFactory();
        PropositionalParser parser = new PropositionalParser(factory);

        FormulaSolver solver = new FormulaSolver();

        for (int i = 0; i < contextVars.size(); i++) {
            for (int j = i+1; j < contextVars.size(); j++) {
                if (i != j){
                    try {
                        Literal l1 = contextVars.get(j).getFirst();
                        Literal k1 = contextVars.get(i).getFirst();
                        Literal l2 = contextVars.get(j).getSecond();
                        Literal k2 = contextVars.get(i).getSecond();

                        Formula f1= solver.expressionToFormula(l2);
                        Formula f2= solver.expressionToFormula(k2);

                        f1 = solver.substituteVariables(f1);
                        f2 = solver.substituteVariables(f2);

                        SortedSet<Variable> vars1 = f1.variables();
                        SortedSet<Variable> vars2 = f2.variables();

                        LiteralNode lLiteralNode = lattice.findOrCreateLiteralNode(l1);
                        LiteralNode kLiteralNode = lattice.findOrCreateLiteralNode(k1);

                        // check if they have at least one variable in common then they are comparable
                        if(vars1.stream().anyMatch(vars2::contains) || vars2.stream().anyMatch(vars1::contains)) {
                            if(implies(f1,f2)){
                                lLiteralNode.addMoreGeneral(kLiteralNode); // lLiteralNode is more general than kLiteralNode
                                kLiteralNode.addMoreSpecific(lLiteralNode); // kLiteralNode is more specific than lLiteralNode
                            } else if (implies(f2,f1)) {
                                lLiteralNode.addMoreSpecific(kLiteralNode); // lLiteralNode is more specific than kLiteralNode
                                kLiteralNode.addMoreGeneral(lLiteralNode);
                            }
                        }
                    } catch (ParserException e) {
                        System.err.println(e.getMessage());
                    }
                }
            }
        }

        HashMap<Literal, List<Unifier>> mapUnifiers = new HashMap<>();
        Unifier unifier = new Unifier();
        contextVars.forEach(pair -> {
            Literal ctx = pair.getFirst();
            final Iterator<Unifier> iu = ctx.logicalConsequence(ag, unifier);
            Set<Unifier> uniqueUnifiers = new LinkedHashSet<>();
            if (iu != null) {
                iu.forEachRemaining(uniqueUnifiers::add);  // Ajoute uniquement des éléments uniques
            }
            mapUnifiers.put(ctx, new ArrayList<>(uniqueUnifiers));  // Convertit en liste
        });

        for (Map.Entry<Literal, List<Unifier>> entry : mapUnifiers.entrySet()) {
            Literal l = entry.getKey();
            List<Unifier> listValue = entry.getValue();

            LiteralNode node = lattice.findOrCreateLiteralNode(l);

            for (Unifier u : listValue) {
                if(l instanceof VarTerm){
                    Literal lit = (Literal) u.get(l.toString());
                    if (lit != null) {
                        lit.clearAnnots();
                    }

                    LiteralNode eLiteralNode = lattice.findLiteralNode(lit);
                    if (eLiteralNode != null) {
                        eLiteralNode.addMoreGeneral(node);
                        node.addMoreSpecific(eLiteralNode);
                    }
                } else {
                    Set<VarTerm> varTermList = getVarTerms(l);
                    for (VarTerm varTerm : varTermList) {
                        Literal lit = (Literal) u.get(varTerm.toString());
                        if (lit != null) {
                            lit.clearAnnots();
                        }

                        // check that the literal corresponding to the unifier is in the lattice
                        // the goal of this is to guaranty that the literal corresponding to the unifier is a guard.
                        LiteralNode eLiteralNode = lattice.findLiteralNode(lit);
                        if (eLiteralNode != null) {
                            eLiteralNode.addMoreGeneral(node);
                            node.addMoreSpecific(eLiteralNode);
                        }
                    }
                }
            }
        }
    }


    public Boolean listInOtherList(List<Pair<Literal, Unifier>> listPair1, List<Pair<Literal, Unifier>> listPair2) {
        for (Pair<Literal, Unifier> pair1 : listPair1) {
            for (Pair<Literal, Unifier> pair2 : listPair2) {
                if (pair1.getFirst().equals(pair2.getFirst())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Boolean implies(Formula f1, Formula f2) throws ParserException {
        FormulaFactory f = new FormulaFactory();
        PropositionalParser p = new PropositionalParser(f);

        // formula to cnf
        Formula A = p.parse(f1.toString()).cnf();
        Formula B = p.parse(f2.toString()).cnf();

        Formula implication = f.implication(A, B);
        Formula negated = implication.negate();

        SATSolver solver = MiniSat.miniSat(f);
        solver.add(negated);

        if(solver.sat() == Tristate.FALSE){
            return true;
        }
        return false;
    }

    public Set<VarTerm> getVarTerms(Literal literal) {
        Set<VarTerm> varTerms = new HashSet<>();
        if (literal instanceof VarTerm) {
            varTerms.add((VarTerm) literal);
        } else if (!literal.hasTerm()) {
            return Collections.emptySet();
        } else {
            literal.getTerms().forEach(term -> {
                Set<VarTerm> tempTerm = getVarTerms((Literal) term);
                varTerms.addAll(tempTerm);  // Ajout direct, sans doublons
            });
        }
        return varTerms;
    }
}
