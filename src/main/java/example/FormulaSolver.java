package example;

import jason.asSyntax.*;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.formulas.Variable;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;

import java.util.*;

public class FormulaSolver {

    private final FormulaFactory f = new FormulaFactory();
    private final PropositionalParser p = new PropositionalParser(f);

    public FormulaSolver() {
    }

    public Formula expressionToFormula(LogicalFormula logicalFormula) throws ParserException {
        if (logicalFormula instanceof LogExpr) {
            return expressionToFormula((LogExpr) logicalFormula);
        } else if (logicalFormula instanceof RelExpr) {
            return expressionToFormula((RelExpr) logicalFormula);
        } else {
            return p.parse(logicalFormula.toString()).cnf();
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
                // X == Y -> equivalence(X, Y)
                return f.equivalence(f.variable(String.valueOf(term1)), f.variable(String.valueOf(term2)));
            case dif:
                // X \\== Y -> not(equivalence(X, Y))
                return f.not(f.equivalence(f.variable(String.valueOf(term1)), f.variable(String.valueOf(term2))));
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

    public Formula substituteVariables(Formula formula) {
        SortedSet<Variable> vars = formula.variables();
        int varIndex = 0;
        for (Variable var : vars) {
            if (startsWithUppercase(var)){
                formula = formula.substitute(var, f.variable("X"+varIndex));
                varIndex++;
            }
        }
        return formula;
    }

    public static boolean startsWithUppercase(Variable var) {
        String varName = var.name();  // Obtenir le nom de la variable
        return !varName.isEmpty() && Character.isUpperCase(varName.charAt(0));
    }

}
