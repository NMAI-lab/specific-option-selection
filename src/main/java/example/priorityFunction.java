package example;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.ListTerm;
import jason.asSyntax.Term;

import java.util.List;

public class priorityFunction extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        if (args[0] instanceof ListTerm) {
            List list = (List) args[0];
            PriorityList instance = PriorityList.getInstance();
            instance.setListWithTerms(list);
        } else {
            System.err.println("ERROR: PriorityList must be a list of priorities");
        }
        // args[0] is the unattended luggage Report Number
        return true;
    }
}