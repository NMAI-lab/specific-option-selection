package example;

import jason.asSyntax.Term;

public class PriorityTerm {
    private Term term;
    private int priority;

    public PriorityTerm(Term term, int priority) {
        this.term = term;
        this.priority = priority;
    }

    public Term getTerm() {
        return term;
    }

    public int getPriority() {
        return priority;
    }
}
