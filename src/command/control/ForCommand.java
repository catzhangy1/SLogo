package command.control;

import java.util.List;
import model.TreeInterpreter;
import model.Variable;
import parser.CommandTreeNode;
import command.Command;


/**
 * runs command(s) for each value specified in the range, i.e., from (start - end), going by
 * increment
 * returns the value of the final command executed
 * 
 * @author OWNER
 *
 */
public class ForCommand extends Command {
    private List<CommandTreeNode> parameters;
    private List<CommandTreeNode> subCommands;
    private TreeInterpreter tree;

    public ForCommand (CommandTreeNode node1, CommandTreeNode node2, TreeInterpreter t) {
        parameters = node1.getChildren();
        subCommands = node2.getChildren();
        tree = t;
    }

    public double calculateValue () {
        Variable var = tree.getVariableList().get(parameters.get(0).getName());

        for (int j = 1; j < parameters.size(); j++) {
            tree.interpretTree(parameters.get(j));
        }
        int start = (int) parameters.get(1).getValue();
        int end = (int) parameters.get(2).getValue();
        int increment = (int) parameters.get(3).getValue();

        for (int i = start; i < end; i += increment) {
            for (int j = 0; j < subCommands.size(); j++) {
                tree.interpretTree(subCommands.get(j));
            }
            tree.getVariableList().get(var.getName())
                    .setValue(subCommands.get(subCommands.size() - 1).getValue());// Is this a
                                                                                  // correct
                                                                                  // interpretation?
        }
        return var.getValue();
    }
}
