package example;

import jason.asSyntax.*;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;


public class FormulaSolver {

    private final FormulaFactory f = new FormulaFactory();
    private final PropositionalParser p = new PropositionalParser(f);

    public FormulaSolver() {
        new PropositionalParser(new FormulaFactory());
    }

    /*
    This function returns a LogicNG Formula corresponding to a given Jason LogicalFormula
     */
    public Formula expressionToFormula(LogicalFormula logicalFormula) throws ParserException {
        if (logicalFormula instanceof LogExpr) {
            return expressionToFormula((LogExpr) logicalFormula);
        } else if (logicalFormula instanceof RelExpr) {
            return expressionToFormula((RelExpr) logicalFormula);
        } else {
            return expressionToFormula((Literal) logicalFormula);
        }
    }

    public Formula expressionToFormula(LogExpr logExpr) throws ParserException {
        LogicalFormula term1 = logExpr.getLHS();
        LogicalFormula term2 = logExpr.getRHS();
        LogExpr.LogicalOp op = logExpr.getOp();

        switch (op) {
            case none:
                break;
            case not:
                return f.not(expressionToFormula(term1));
            case or:
                return f.or(expressionToFormula(term1), expressionToFormula(term2));
            case and:
                return f.and(expressionToFormula(term1), expressionToFormula(term2));

            default:
                return f.variable("");
        }

        return null;
    }

    public Formula expressionToFormula(RelExpr relExpr) {
        Term term1 = relExpr.getTerm(0);
        Term term2 = relExpr.getTerm(1);
        RelExpr.RelationalOp op = relExpr.getOp();

        switch (op) {
            case eq:
                return f.variable(encodeEquality(term1.toString(), term2.toString()));
            case dif:
                return f.not(f.variable(encodeEquality(term1.toString(), term2.toString())));
            case unify:
                // X = Y -> equivalence(X, Y)
                return f.equivalence(f.variable(String.valueOf(term1)), f.variable(String.valueOf(term2)));
            case gt:
            case gte:
            case lt:
            case lte:
                String operatorSymbol = op.toString().trim();
                String relationalVar = "(" + term1 + operatorSymbol + term2 + ")";
                return f.variable(relationalVar);

            case literalBuilder:
                // Cas particulier pour " =.. ", considéré comme une équivalence
                return f.equivalence(f.variable(String.valueOf(term1)), f.variable(String.valueOf(term2)));

            default:
                // Si l'opérateur est "none" ou inconnu, retourner un littéral vide
                return f.variable("");
        }
    }

    public Formula expressionToFormula(Literal literal) throws ParserException {
        Literal literalCleared = literal.clearAnnots();
        return p.parse(encodeLiteral(literalCleared.toString())).cnf();
    }

    /*
    This function allows to get the string corresponding to a given Jason Literal
     */
    public static String encodeLiteral(String predicate) {
        return predicate.replaceAll("[(). ,]", "_").replaceAll("_+", "_").replaceAll("^_+", "").replaceAll("_$", "");
    }

    /*
    This function allows to get the string corresponding to an equality between two strings
     */
    public static String encodeEquality(String t1, String t2) {
        return encodeLiteral(t1) + "_eq_" + encodeLiteral(t2);
    }

    /*
    This function determines if the formula f1 implies f2 using LogicNG
     */
    public Boolean implies(Formula f1, Formula f2) {
        Formula implication = f.implication(f1, f2); // builds A => B
        // builds ¬(A => B) equivalent to A ∧ ¬B
        Formula negated = implication.negate();

        SATSolver solver = MiniSat.miniSat(f);
        solver.add(negated);

        return solver.sat() == Tristate.FALSE;
    }
}
