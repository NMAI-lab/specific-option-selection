package example;

import example.types.LiteralNode;
import jason.asSemantics.Agent;
import jason.asSemantics.NoOptionException;
import jason.asSemantics.Option;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import org.logicng.io.parsers.ParserException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;


public class TestAgent extends Agent {

    @Override
    public void initAg(){
        super.initAg();
        //we load this class to reduce the execution time of the first call to our selectOption function
        new LogicalFormulaHandler();
    }


    @Override
    public Option selectOption(List<Option> options) throws NoOptionException {
        //Here that is what we wanna select
        if (options.size() > 1) {
            long start = System.nanoTime();
            //Return a list of pair foreach option's context (guard) associated with his atomic logical formulaoptions = {LinkedList@4792}  size = 7
            Boolean parametersGiven = this.getTS().getC().getSelectedEvent().getTrigger().getLiteral().hasTerm();
            Unifier originalUn = new Unifier();
            if (parametersGiven) {
                // if parameters are given to the function we take those as the original base unifier for our function
                // which is different than the unifier that satisfies the context of the plan (can be a longer version of it)
                originalUn = options.get(0).getPlan().isRelevant(this.getTS().getC().getSelectedEvent().getTrigger(), new Unifier());
            }

            LogicalFormulaHandler handler = new LogicalFormulaHandler();

            int size = options.size();
            for(int i = 0; i < size; i++) {
                LogicalFormula ctx1 = cloneContext(options.get(i).getPlan().getContext());
                LiteralNode node1 = handler.getLattice().findOrCreateLiteralNode((Literal) ctx1);
                for (int j = i+1; j < size; j++) {
                    LogicalFormula ctx2 = cloneContext(options.get(j).getPlan().getContext());
                    if(ctx1.equals(ctx2)) continue;

                    LiteralNode node2 = handler.getLattice().findOrCreateLiteralNode((Literal) ctx2);

                    try {
                        if(handler.isALogicalConsequenceof(this, ctx1, ctx2, originalUn.clone())) {
                            node1.addMoreGeneral(node2);
                            node2.addMoreSpecific(node1);
//                            System.out.println(ctx1 + " is more specific than " + ctx2);
                        } else {
                            if(handler.isALogicalConsequenceof(this, ctx2, ctx1, originalUn.clone())) {
                                node2.addMoreGeneral(node1);
                                node1.addMoreSpecific(node2);
//                                System.out.println(ctx2 + " is more specific than " + ctx1);
                            }
                        }
                    } catch (ParserException e) {
                        throw new RuntimeException(e);
                    }


                }
            }

            handler.getLattice().sortLiteralNodesBySpecificity();
            options = options.stream()
                    // Compare each node of lattice by moreSpecificNumber and keeping initial order if equality
                    .sorted(Comparator.comparingInt((Option option) -> {
                        Literal literal = (Literal) option.getPlan().getContext();
                        LiteralNode node = handler.getLattice().findLiteralNode(literal);
                        return (node != null) ? node.getMoreSpecific().size() : Integer.MAX_VALUE;
                    }).thenComparingInt(options::indexOf))
                    .collect(Collectors.toList());
            long end = System.nanoTime();
            long duration = end - start; // en nanosecondes
            System.out.println("Durée : " + (duration / 1_000_000.0) + " ms");
        }

        return super.selectOption(options);
    }


    public LogicalFormula cloneContext(LogicalFormula ctx) {
        if(ctx == null) {
            return null;
        } else {
            return (LogicalFormula) ctx.clone();
        }
    }
}


