package example.types;

import jason.asSyntax.Literal;

import java.util.HashSet;
import java.util.Set;

public class LiteralNode {

    private Literal value;
    private Set<LiteralNode> listOfMoreSpecificNodes = new HashSet<>();
    private Set<LiteralNode> listOfMoreGeneralNodes= new HashSet<>();

    public LiteralNode(Literal value) {
        this.value = value;
    }

    public LiteralNode(Set<LiteralNode> moreGeneral, Set<LiteralNode> moreSpecific) {
        this.listOfMoreGeneralNodes = moreGeneral;
        this.listOfMoreSpecificNodes = moreSpecific;
    }

    public Literal getValue() {
        return value;
    }

    public Set<LiteralNode> getMoreSpecific() {
        return listOfMoreSpecificNodes;
    }

    public Set<LiteralNode> getMoreGeneral() {
        return listOfMoreGeneralNodes;
    }

    public void addMoreGeneral(LiteralNode n) {
        listOfMoreGeneralNodes.add(n);
    }

    public void addMoreSpecific(LiteralNode n) {
        listOfMoreSpecificNodes.add(n);
    }

    public boolean hasMoreSpecific(Literal target) {
        for (LiteralNode node : listOfMoreSpecificNodes) {
            if (node.getValue().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMoreGeneral(Literal target) {
        for (LiteralNode node : listOfMoreGeneralNodes) {
            if (node.getValue().equals(target)) {
                return true;
            }
        }
        return false;
    }
}
