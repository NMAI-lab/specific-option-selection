package example;

import jason.asSyntax.Term;

import java.util.ArrayList;
import java.util.List;

public class PriorityList {

    // Single Method Design pattern for this class
    private static PriorityList single_instance = null;

    private List<PriorityTerm> list;

    private PriorityList() {
        list = new ArrayList();
    }

    public static synchronized PriorityList getInstance()
    {
        if (single_instance == null)
            single_instance = new PriorityList();

        return single_instance;
    }

    public List<PriorityTerm> getList() {
        return list;
    }

    public void setList(List<PriorityTerm> list){
        this.list = list;
    }

    public void setListWithTerms(List<Term> listTerm){
        int index = 0;
        for (Term t : listTerm){
            index++;
            list.add(new PriorityTerm(t, index));
        }
    }
}
