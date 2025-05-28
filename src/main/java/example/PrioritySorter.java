package example;

import example.objs.Lattice;
import example.objs.LiteralNode;
import jason.asSemantics.Agent;
import jason.asSemantics.Option;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LiteralImpl;
import jason.asSyntax.Term;
import jason.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PrioritySorter {
    private PriorityList priorityList;

    public PrioritySorter() {
        this.priorityList = PriorityList.getInstance();
    }

    public void sortWithPriorityInternalFunction(Agent ag, Lattice lattice, List<Option> options) {
        Unifier unifier = new Unifier();

        List<PriorityTerm> priorityList = this.priorityList.getList();

        List<Literal> listGroundGuards = options.stream()
                .map(option -> option.getPlan().getContext())
                .filter(ctx -> ctx.isGround())
                .map(LiteralImpl.class::cast)
                .collect(Collectors.toList());

        List<Literal> listVarsGuards = options.stream()
                .map(option -> option.getPlan().getContext())
                .filter(ctx -> !ctx.isGround())
                .map(LiteralImpl.class::cast)
                .collect(Collectors.toList());

        HashMap<Literal, Pair<Term, Unifier>> mapPriorityUnif = new HashMap<>();
        List<Pair<Literal, Unifier>> unifList = new ArrayList<>();

        int priorityListSize = priorityList.size();
        for (int i = 0; i < priorityListSize; i++) {
            Term term = priorityList.get(i).getTerm();

            final Iterator<Unifier> iu = ((Literal) term).logicalConsequence(ag,unifier);
            while(iu.hasNext()){
                Unifier un = iu.next();
                unifList.add(new Pair<>((Literal) term, un));
                mapPriorityUnif.put((Literal) term.capply(un), new Pair<>((Literal) term, un));
            }
        }

        for (int i = 0; i < listGroundGuards.size(); i++) {
            for (int j = i + 1; j < listGroundGuards.size(); j++) {
                Literal lit1 = listGroundGuards.get(i);
                Literal lit2 = listGroundGuards.get(j);
                if (mapPriorityUnif.containsKey(lit1) && mapPriorityUnif.containsKey(lit2)) {
                    Term t1 = mapPriorityUnif.get(lit1).getFirst();
                    Unifier u1 = mapPriorityUnif.get(lit1).getSecond();
                    Term t2 = mapPriorityUnif.get(lit2).getFirst();

                    Term matchT2 = t2.capply(u1);
                    if(matchT2 != null && lit2.equals(matchT2)){
                        LiteralNode node1 = lattice.findOrCreateLiteralNode(lit1);
                        LiteralNode node2 = lattice.findOrCreateLiteralNode(lit2);

                        PriorityTerm pt1 = priorityList.stream()
                                .filter(pt -> pt.getTerm().equals(t1))
                                .findFirst()
                                .orElse(null);

                        PriorityTerm pt2 = priorityList.stream()
                                .filter(pt -> pt.getTerm().equals(t2))
                                .findFirst()
                                .orElse(null);

                        if(pt1.getPriority() > pt2.getPriority()){
                            node1.addMoreSpecific(node2);
                            node2.addMoreGeneral(node1);
                        } else if(pt1.getPriority() < pt2.getPriority()){
                            node1.addMoreGeneral(node2);
                            node2.addMoreSpecific(node1);
                        }
                    }
                }
            }
        }

    }


}
