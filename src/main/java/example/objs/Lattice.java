package example.objs;

import jason.asSyntax.Literal;

import java.util.*;
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

    public boolean isMoreSpecific(LiteralNode node1, Literal node2) {
        return isMoreSpecificRecursive(node1, node2, new HashSet<>());
    }

    private boolean isMoreSpecificRecursive(LiteralNode current, Literal target, Set<LiteralNode> visited) {
        if (current.hasMoreSpecific(target)) {
            return true;
        }

        visited.add(current);

        for (LiteralNode child : current.getMoreSpecific()) {
            if (!visited.contains(child)) {
                if (isMoreSpecificRecursive(child, target, visited)) {
                    return true;
                }
            }
        }

        return false;
    }


    public boolean isMoreGeneral(LiteralNode node1, Literal node2) {
        return isMoreGeneralRecursive(node1, node2, new HashSet<>());
    }

    private boolean isMoreGeneralRecursive(LiteralNode current, Literal target, Set<LiteralNode> visited) {
        if (current.hasMoreGeneral(target)) {
            return true;
        }

        visited.add(current);

        for (LiteralNode parent : current.getMoreGeneral()) {
            if (!visited.contains(parent)) {
                if (isMoreGeneralRecursive(parent, target, visited)) {
                    return true;
                }
            }
        }

        return false;
    }

}
