package example;

import example.objs.LiteralNode;
import jason.asSemantics.*;
import jason.asSyntax.*;

import java.util.*;
import java.util.stream.Collectors;


public class TestAgent extends Agent {


    @Override
    public Option selectOption(List<Option> options)  {
        //Here that is what we wanna select
        if (options.size() > 1) {
            LogicalFormulaHandlerImpl handler = new LogicalFormulaHandlerImpl();
            //Return a list of pair foreach option's context (guard) associated with his atomic logical formula
            options.stream()
                    .map(option -> option.getPlan().getContext())
                    .forEach(ctx -> {
                        if (ctx instanceof VarTerm varTerm) {
                            handler.handle(varTerm, this);
                        } else if (ctx instanceof LiteralImpl literalImpl) {
                            handler.handle(literalImpl, this);
                        } else  if(ctx instanceof LogExpr logExpr) {
                            handler.handle(logExpr, this);
                        }
                    });

            handler.sortContextWithTerms(this);
            handler.sortContextWithoutTerms(this);
            handler.sortContextWithVars(this);

            handler.lattice.sortLiteralNodesBySpecificity();
            options = options.stream()
                    // Compare each node of lattice by moreSpecificNumber and keeping initial order if equality
                    .sorted(Comparator.comparingInt((Option option) -> {
                        Literal literal = (Literal) option.getPlan().getContext();
                        LiteralNode node = handler.lattice.findLiteralNode(literal);
                        return (node != null) ? node.getMoreSpecific().size() : Integer.MAX_VALUE;
                    }).thenComparingInt(options::indexOf))
                    .collect(Collectors.toList());
//
//            PrioritySorter sorter = new PrioritySorter();
//            sorter.sortWithPriorityInternalFunction(this, handler.lattice, options);
            List<LogicalFormula> listContexts = options.stream()
                    .map(option -> option.getPlan().getContext())
                    .collect(Collectors.toList());



        }

        return super.selectOption(options);
    }
}
