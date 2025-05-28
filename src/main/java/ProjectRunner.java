import jason.JasonException;
import jason.infra.local.RunLocalMAS;
import jason.util.Config;

public class ProjectRunner {
    public static void main(String[] args) throws JasonException {
        Config.get().setProperty(Config.START_WEB_MI, "false");

        // default to epistemic-agents.mas2j
        if(args.length == 0)
            args = new String[] {"subclass.mas2j"};

        RunLocalMAS.main(args);
    }
}
