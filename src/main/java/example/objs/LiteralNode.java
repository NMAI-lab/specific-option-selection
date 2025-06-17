package example.objs;

import jason.asSyntax.Literal;

import java.util.HashSet;
import java.util.Set;

public class LiteralNode {

    private Literal value;
    private Set<LiteralNode> moreSpecific = new HashSet<>();
    private Set<LiteralNode> moreGeneral= new HashSet<>();

    public LiteralNode(Literal value) {
        this.value = value;
    }

    public LiteralNode(Set<LiteralNode> moreGeneral, Set<LiteralNode> moreSpecific) {
        this.moreGeneral = moreGeneral;
        this.moreSpecific = moreSpecific;
    }

    public Literal getValue() {
        return value;
    }

    public Set<LiteralNode> getMoreSpecific() {
        return moreSpecific;
    }

    public Set<LiteralNode> getMoreGeneral() {
        return moreGeneral;
    }

    public void addMoreGeneral(LiteralNode n) {
        moreGeneral.add(n);
    }

    public void addMoreSpecific(LiteralNode n) {
        moreSpecific.add(n);
    }

    public boolean hasMoreSpecific(Literal target) {
        for (LiteralNode node : moreSpecific) {
            if (node.getValue().equals(target)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasMoreGeneral(Literal target) {
        for (LiteralNode node : moreGeneral) {
            if (node.getValue().equals(target)) {
                return true;
            }
        }
        return false;
    }
}
