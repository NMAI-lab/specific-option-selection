package example.objs;

import jason.asSyntax.Literal;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Lattice {
    
    List<LiteralNode> nodes;

    public Lattice(){
        nodes = new ArrayList<>();
    }

    public Boolean contains(Literal literal) {
        return nodes.stream()
                .anyMatch(node -> node.getValue().equals(literal));
    }

    public LiteralNode findLiteralNode(Literal literal) {
        return nodes.stream()
                .filter(node -> node.getValue().equals(literal))
                .findFirst()  // Récupère le premier élément trouvé
                .orElse(null); // Si aucun nœud n'est trouvé, retourne null
    }

    public LiteralNode findOrCreateLiteralNode(Literal literal) {
        LiteralNode literalNode = findLiteralNode(literal);
        if(literalNode == null){
            literalNode = new LiteralNode(literal);
            add(literalNode);
        }
        return literalNode;
    }

    public void add(LiteralNode node){
        nodes.add(node);
    }

    public void addLattices(Lattice... lattices) {
        for (Lattice lattice : lattices) {
            for (LiteralNode node : lattice.nodes) {
                if (!contains(node.getValue())) {
                    add(node);
                }
            }
        }
    }

    public List<LiteralNode> findMostSpecificLiteralNodes() {
        List<LiteralNode> result = new ArrayList<>();
        int minSize = Integer.MAX_VALUE;

        for (LiteralNode node : nodes) {
            int size = node.getMoreSpecific().size();
            if (size < minSize) {
                minSize = size;
                result.clear();  // New minimum found, clear the previous list
                result.add(node);
            } else if (size == minSize) {
                result.add(node);  // Add if equal to the current minimum
            }
        }

        return result;
    }

    public void sortLiteralNodesBySpecificity() {
        nodes = nodes.stream()
                .sorted(Comparator.comparingInt(node -> node.getMoreSpecific().size()))
                .collect(Collectors.toList());
    }
}
