package example;

import example.objs.LiteralNode;
import jason.asSemantics.*;
import jason.asSyntax.*;
import org.logicng.io.parsers.ParserException;

import java.util.*;
import java.util.stream.Collectors;


public class TestAgent extends Agent {
    
    @Override
    public Option selectOption(List<Option> options) throws NoOptionException {
        //Here that is what we wanna select
        if (options.size() > 1) {
            LogicalFormulaHandler handler = new LogicalFormulaHandler();
            //Return a list of pair foreach option's context (guard) associated with his atomic logical formulaoptions = {LinkedList@4792}  size = 7

            int size = options.size();
            for(int i = 0; i < size; i++) {
                LogicalFormula ctx1 = options.get(i).getPlan().getContext();
                LiteralNode node1 = handler.lattice.findOrCreateLiteralNode((Literal) ctx1);
                for (int j = i+1; j < size; j++) {
                    LogicalFormula ctx2 = options.get(j).getPlan().getContext();
                    LiteralNode node2 = handler.lattice.findOrCreateLiteralNode((Literal) ctx2);

                    boolean f1impliesf2 = false;
                    boolean f2impliesf1 = false;
                    try {
                        f1impliesf2 = handler.isMoreSpecific(this, ctx1, ctx2);
                        f2impliesf1 = handler.isMoreSpecific(this, ctx2, ctx1);
                    } catch (ParserException e) {
                        throw new RuntimeException(e);
                    }
                    if(f1impliesf2 & !f2impliesf1) {
                        node1.addMoreGeneral(node2);
                        node2.addMoreSpecific(node1);
                        System.out.println(ctx1 + " more specific than " + ctx2);
                    }
                    if(f2impliesf1 & !f1impliesf2) {
                        node2.addMoreGeneral(node1);
                        node1.addMoreSpecific(node2);
                        System.out.println(ctx2 + " more specific than " + ctx1);
                    }
                }
            }

            handler.lattice.sortLiteralNodesBySpecificity();
            options = options.stream()
                    // Compare each node of lattice by moreSpecificNumber and keeping initial order if equality
                    .sorted(Comparator.comparingInt((Option option) -> {
                        Literal literal = (Literal) option.getPlan().getContext();
                        LiteralNode node = handler.lattice.findLiteralNode(literal);
                        return (node != null) ? node.getMoreSpecific().size() : Integer.MAX_VALUE;
                    }).thenComparingInt(options::indexOf))
                    .collect(Collectors.toList());
        }

        return super.selectOption(options);
    }
}


