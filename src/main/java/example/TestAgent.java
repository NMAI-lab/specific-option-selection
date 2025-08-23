package example;

import example.types.LiteralNode;
import jason.asSemantics.Agent;
import jason.asSemantics.NoOptionException;
import jason.asSemantics.Option;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.LogicalFormula;
import org.logicng.io.parsers.ParserException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TestAgent extends Agent {

    private BufferedWriter logWriter;
    private String logFileName;

    @Override
    public void initAg(){
        super.initAg();
        new LogicalFormulaHandler(); // Préchargement

        // Création du dossier log s’il n’existe pas
        File logDir = new File("log");
        if (!logDir.exists()) {
            logDir.mkdir();
        }

        // Création du fichier avec horodatage
        String timestamp = "stclaus";
        logFileName = "log/selectOptionLog_" + timestamp + ".txt";

        try {
            logWriter = new BufferedWriter(new FileWriter(logFileName, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Option selectOption(List<Option> options) throws NoOptionException {
        if (options.size() > 1) {
            long start = System.nanoTime();
            StringBuilder logBuilder = new StringBuilder();

            Boolean parametersGiven = this.getTS().getC().getSelectedEvent().getTrigger().getLiteral().hasTerm();
            Unifier originalUn = new Unifier();
            if (parametersGiven) {
                originalUn = options.get(0).getPlan().isRelevant(
                        this.getTS().getC().getSelectedEvent().getTrigger(), new Unifier());
            }

            LogicalFormulaHandler handler = new LogicalFormulaHandler();

            int size = options.size();
            logBuilder.append("===== New selectOption call =====\n");
            logBuilder.append("Agent : ").append(this.getTS().getAgArch().getAgName()).append("\n");
            logBuilder.append("Event : ").append(options.get(0).getPlan().getTrigger().toString()).append("\n");
            logBuilder.append("Number of options: ").append(size).append("\n");

            for (int i = 0; i < size; i++) {
                LogicalFormula ctx1 = cloneContext(options.get(i).getPlan().getContext());
                logBuilder.append("Option ").append(i).append(" context: ").append(ctx1).append("\n");

                LiteralNode node1 = handler.getLattice().findOrCreateLiteralNode((Literal) ctx1);

                for (int j = i + 1; j < size; j++) {
                    LogicalFormula ctx2 = cloneContext(options.get(j).getPlan().getContext());
                    if(ctx1 != null && ctx1.equals(ctx2)) continue;

                    LiteralNode node2 = handler.getLattice().findOrCreateLiteralNode((Literal) ctx2);

                    try {
                        if (handler.isALogicalConsequenceof(this, ctx1, ctx2, originalUn.clone())) {
                            node1.addMoreGeneral(node2);
                            node2.addMoreSpecific(node1);
                        } else if (handler.isALogicalConsequenceof(this, ctx2, ctx1, originalUn.clone())) {
                            node2.addMoreGeneral(node1);
                            node1.addMoreSpecific(node2);
                        }
                    } catch (ParserException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            handler.getLattice().sortLiteralNodesBySpecificity();
            options = options.stream()
                    .sorted(Comparator.comparingInt((Option option) -> {
                        Literal literal = (Literal) option.getPlan().getContext();
                        LiteralNode node = handler.getLattice().findLiteralNode(literal);
                        return (node != null) ? node.getMoreSpecific().size() : Integer.MAX_VALUE;
                    }).thenComparingInt(options::indexOf))
                    .collect(Collectors.toList());

            long end = System.nanoTime();
            long durationMs = (end - start) / 1_000_000;

            LogicalFormula selectedContext = options.get(0).getPlan().getContext();
            logBuilder.append("Option sélectionnée : ")
                    .append(selectedContext != null ? selectedContext.toString() : "null")
                    .append("\n");
            logBuilder.append("Durée de la sélection : ").append(durationMs).append(" ms\n\n");

            try {
                logWriter.write(logBuilder.toString());
                logWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Durée : " + durationMs + " ms");
        }

        return super.selectOption(options);
    }

    public LogicalFormula cloneContext(LogicalFormula ctx) {
        if (ctx == null) {
            return null;
        } else {
            return (LogicalFormula) ctx.clone();
        }
    }
}
