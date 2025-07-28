package example;

import jason.asSyntax.*;
import org.logicng.datastructures.Tristate;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;
import org.logicng.io.parsers.ParserException;
import org.logicng.io.parsers.PropositionalParser;
import org.logicng.solvers.MiniSat;
import org.logicng.solvers.SATSolver;

public class TryLogicNG {

    public static void main(String[] args) throws ParserException {

        /** First test : only LogicNG **/

        FormulaFactory fact = new FormulaFactory();
        PropositionalParser parser = new PropositionalParser(fact);


        long start = System.nanoTime();

        Formula formulaA = parser.parse(
                "(a | b | c | d) & (e | f | g | h) & (i | j | k | l) & (m | n | o | p) & " +
                        "(q | r | s | t) & (u | v | w | x) & (y | z | a1 | b1) & (c1 | d1 | e1 | f1) & " +
                        "(g1 | h1 | i1 | j1) & (k1 | l1 | m1 | n1) & (o1 | p1 | q1 | r1) & (s1 | t1 | u1 | v1)"
        ).cnf();


        Formula formulaB = parser.parse(
                "(p & q & r & s) | (t & u & v & w) | (x & y & z & a1) | (b1 & c1 & d1 & e1) | " +
                        "(f1 & g1 & h1 & i1) | (j1 & k1 & l1 & m1) | (n1 & o1 & p1 & q1) | (r1 & s1 & t1 & u1) | " +
                        "(v1 & w1 & x1 & y1) | (z1 & a2 & b2 & c2) | (d2 & e2 & f2 & g2) | (h2 & i2 & j2 & k2)"
        ).cnf();

        long secondstart = System.nanoTime();

        Formula implication = fact.implication(formulaA, formulaB);
        Formula negated = implication.negate();


        Formula implicationReverse = fact.implication(formulaB, formulaA);
        Formula negatedReverse = implicationReverse.negate();


        SATSolver solver = MiniSat.miniSat(fact);
        solver.add(negated);

        SATSolver solverRev = MiniSat.miniSat(fact);
        solverRev.add(negatedReverse);


        if(solver.sat() == Tristate.FALSE){
            System.out.println(true);
        }

        if(solverRev.sat() == Tristate.FALSE){
            System.out.println(true);
        }

        long end = System.nanoTime();
        long duration = end - start; // en nanosecondes
        long durationsecond = end - secondstart; // en nanosecondes
        System.out.println("Premier test : seulement LogicNG");
        System.out.println("Durée totale: " + (duration / 1_000_000.0) + " ms");
        System.out.println("Durée juste implication : " + (durationsecond / 1_000_000.0) + " ms");


        /** Second test : only LogicNG **/


        Literal a = ASSyntax.createLiteral("a");
        Literal b = ASSyntax.createLiteral("b");
        Literal c = ASSyntax.createLiteral("c");
        Literal d = ASSyntax.createLiteral("d");

        Literal e = ASSyntax.createLiteral("e");
        Literal f = ASSyntax.createLiteral("f");
        Literal g = ASSyntax.createLiteral("g");
        Literal h = ASSyntax.createLiteral("h");

        Literal i = ASSyntax.createLiteral("i");
        Literal j = ASSyntax.createLiteral("j");
        Literal k = ASSyntax.createLiteral("k");
        Literal l = ASSyntax.createLiteral("l");

        Literal m = ASSyntax.createLiteral("m");
        Literal n = ASSyntax.createLiteral("n");
        Literal o = ASSyntax.createLiteral("o");
        Literal p = ASSyntax.createLiteral("p");

        Literal q = ASSyntax.createLiteral("q");
        Literal r = ASSyntax.createLiteral("r");
        Literal s = ASSyntax.createLiteral("s");
        Literal t = ASSyntax.createLiteral("t");

        Literal u = ASSyntax.createLiteral("u");
        Literal v = ASSyntax.createLiteral("v");
        Literal w = ASSyntax.createLiteral("w");
        Literal x = ASSyntax.createLiteral("x");

        Literal y = ASSyntax.createLiteral("y");
        Literal z = ASSyntax.createLiteral("z");
        Literal a1 = ASSyntax.createLiteral("a1");
        Literal b1 = ASSyntax.createLiteral("b1");

        Literal c1 = ASSyntax.createLiteral("c1");
        Literal d1 = ASSyntax.createLiteral("d1");
        Literal e1 = ASSyntax.createLiteral("e1");
        Literal f1 = ASSyntax.createLiteral("f1");

        Literal g1 = ASSyntax.createLiteral("g1");
        Literal h1 = ASSyntax.createLiteral("h1");
        Literal i1 = ASSyntax.createLiteral("i1");
        Literal j1 = ASSyntax.createLiteral("j1");

        Literal k1 = ASSyntax.createLiteral("k1");
        Literal l1 = ASSyntax.createLiteral("l1");
        Literal m1 = ASSyntax.createLiteral("m1");
        Literal n1 = ASSyntax.createLiteral("n1");

        Literal o1 = ASSyntax.createLiteral("o1");
        Literal p1 = ASSyntax.createLiteral("p1");
        Literal q1 = ASSyntax.createLiteral("q1");
        Literal r1 = ASSyntax.createLiteral("r1");

        Literal s1 = ASSyntax.createLiteral("s1");
        Literal t1 = ASSyntax.createLiteral("t1");
        Literal u1 = ASSyntax.createLiteral("u1");
        Literal v1 = ASSyntax.createLiteral("v1");

        Literal w1 = ASSyntax.createLiteral("w1");
        Literal x1 = ASSyntax.createLiteral("x1");
        Literal y1 = ASSyntax.createLiteral("y1");

        Literal z1 = ASSyntax.createLiteral("z1");
        Literal a2 = ASSyntax.createLiteral("a2");
        Literal b2 = ASSyntax.createLiteral("b2");
        Literal c2 = ASSyntax.createLiteral("c2");

        Literal d2 = ASSyntax.createLiteral("d2");
        Literal e2 = ASSyntax.createLiteral("e2");
        Literal f2 = ASSyntax.createLiteral("f2");
        Literal g2 = ASSyntax.createLiteral("g2");

        Literal h2 = ASSyntax.createLiteral("h2");
        Literal i2 = ASSyntax.createLiteral("i2");
        Literal j2 = ASSyntax.createLiteral("j2");
        Literal k2 = ASSyntax.createLiteral("k2");

        LogExpr clause1 = orLits(a, b, c, d);
        LogExpr clause2 = orLits(e, f, g, h);
        LogExpr clause3 = orLits(i, j, k, l);
        LogExpr clause4 = orLits(m, n, o, p);
        LogExpr clause5 = orLits(q, r, s, t);
        LogExpr clause6 = orLits(u, v, w, x);
        LogExpr clause7 = orLits(y, z, a1, b1);
        LogExpr clause8 = orLits(c1, d1, e1, f1);
        LogExpr clause9 = orLits(g1, h1, i1, j1);
        LogExpr clause10 = orLits(k1, l1, m1, n1);
        LogExpr clause11 = orLits(o1, p1, q1, r1);
        LogExpr clause12 = orLits(s1, t1, u1, v1);

        Term formulaAL = andLits(
                clause1,
                clause2,
                clause3,
                clause4,
                clause5,
                clause6,
                clause7,
                clause8,
                clause9,
                clause10,
                clause11,
                clause12
        );

        Literal group1 = andLits(p, q, r, s);
        Literal group2 = andLits(t, u, v, w);
        Literal group3 = andLits(x, y, z, a1);
        Literal group4 = andLits(b1, c1, d1, e1);
        Literal group5 = andLits(f1, g1, h1, i1);
        Literal group6 = andLits(j1, k1, l1, m1);
        Literal group7 = andLits(n1, o1, p1, q1);
        Literal group8 = andLits(r1, s1, t1, u1);
        Literal group9 = andLits(v1, w1, x1, y1);
        Literal group10 = andLits(z1, a2, b2, c2);
        Literal group11 = andLits(d2, e2, f2, g2);
        Literal group12 = andLits(h2, i2, j2, k2);

        // Créer la disjonction OR de tous ces groupes
        Term formulaD = orLits(
                group1,
                group2,
                group3,
                group4,
                group5,
                group6,
                group7,
                group8,
                group9,
                group10,
                group11,
                group12
        );

        long start2 = System.nanoTime();

        FormulaSolver solvi = new FormulaSolver();

        Formula formula1 = solvi.expressionToFormula((LogicalFormula) formulaAL);
        Formula formula2 = solvi.expressionToFormula((LogicalFormula) formulaD);

        long secondstart2 = System.nanoTime();

        if (solvi.implies(formula1, formula2) ) {
            System.out.println("f1 implies f2");
        }
        if (solvi.implies(formula2, formula1) ) {
            System.out.println("f2 implies f1");
        }

        long end2 = System.nanoTime();
        long duration2 = end2 - start2; // en nanosecondes
        long durationsecond2 = end2 - secondstart2; // en nanosecondes
        System.out.println("Deuxieme test : Avec nos fonctions");
        System.out.println("Durée totale: " + (duration2 / 1_000_000.0) + " ms");
        System.out.println("Durée juste implication : " + (durationsecond2 / 1_000_000.0) + " ms");
    }


    public static LogExpr orLits(LogicalFormula... atoms) {
        if (atoms.length == 2) {
            return new LogExpr(atoms[0], LogExpr.LogicalOp.or, atoms[1]);
        }
        LogExpr rest = orLits(java.util.Arrays.copyOfRange(atoms, 1, atoms.length));
        return new LogExpr(atoms[0], LogExpr.LogicalOp.or, rest);
    }

    // Crée and(a,b,c,d) = and(a, and(b, and(c, d))) récursivement
    public static LogExpr andLits(LogicalFormula... atoms) {
        if (atoms.length == 2) {
            return new LogExpr(atoms[0], LogExpr.LogicalOp.and, atoms[1]);
        }
        LogExpr rest = andLits(java.util.Arrays.copyOfRange(atoms, 1, atoms.length));
        return new LogExpr(atoms[0], LogExpr.LogicalOp.and, rest);
    }
}
