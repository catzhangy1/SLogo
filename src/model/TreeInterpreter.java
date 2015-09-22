package model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import command.Command;
import errors.IllegalParameterNumberException;
import parser.CommandTreeNode;

/**
 * Main "MODEL" Class that traverses through one command tree, interprets each subcommand, and 
 * updates back-end elements (turtles, variableList, etc.) accordingly
 * @author OWNER
 *
 */
public class TreeInterpreter {
    private CommandFactory factory;
    private VariableList variables;
    private int activeTurtleIndex;
    private List<Turtle> listTurtles;
    private Map<String, List<CommandTreeNode>> userCommands;

    public TreeInterpreter () {
        factory = new CommandFactory();
        variables = new VariableList();
        activeTurtleIndex = 1;
        listTurtles = new ArrayList<Turtle>();
        userCommands = new HashMap<String, List<CommandTreeNode>>();
    }

    /**
     * Recursive method that traverses through the command tree starting from parent node
     * @param node
     */
    public void interpretTree (CommandTreeNode node) {
        List<Object> paramList = new ArrayList<>();
        if (!node.getChildren().isEmpty()) { //checks for leaf nodes
            if (!(node.getType().equals("BRACKET"))) {
                for (CommandTreeNode child : node.getChildren()) {
                    interpretTree(child);
                    paramList.add(node.getType().equals("COMMAND.CONTROL") ? child : child
                            .getValue());
                }
            }
        }
        
        update(node, paramList);
    }

    public Constructor[] getConstructors (Class<?> className) {
        return className.getDeclaredConstructors();
    }

    /**
     * Creates a command using reflection, invokes calculateValue method in command class with the parameter values
     * @param node, contains information as to which command is created
     * @param paramList, list of parameter values needed for one command
     */
    public void executeCommand (CommandTreeNode node, List<Object> paramList) {
        Class<?> c = factory.createCommand(node.getType(), node.getName());
        Constructor<?> constructor = getConstructors(c)[0];

        Command command = null;
        try {
            command = (Command) constructor.newInstance(paramList.toArray()); 
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            e.printStackTrace();
            System.err.print("Error processing Command" + c.getClass().getName());
            throw new IllegalParameterNumberException();
        }
        Method method = null;
        try {
            method = c.getDeclaredMethod("calculateValue", null);
            try {
                Double value = (Double) method.invoke(command, null);
                node.setValue(value);
            }
            catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
            System.err.print("Error processing Command" + c.getClass().getName());
            e.printStackTrace();
        }
    }

    /**
     * Calls executeCommand if the node represents Command, adding appropriate elements to paramList accordingly
     * @param current node
     * @param paramList, list of parameters values needed for one command
     */
    public void update (CommandTreeNode node, List<Object> paramList) {
        switch (node.getType()) {
            case "COMMAND.TURTLE":
                paramList.add(listTurtles.get(activeTurtleIndex - 1)); //acts on the current activeTurtle
                executeCommand(node, paramList);
                break;
            case "COMMAND.CONTROL":
                paramList.add(this); 
                executeCommand(node, paramList);
                break;
            case "VARIABLE":
                if(userCommands.containsKey(node.getName())){ //assumes parser will parser the command as a variable
                    //call executeUserCommand!
                }
                node.setValue((variables.get(node.getName())).getValue());
                break;
            case "CONSTANT":
                break;
            case "BRACKET":
                break;
            default: // referring to commands that are not TURTLE type
                executeCommand(node, paramList);
        }
    }

    public List<Turtle> getTurtleList () {
        return listTurtles;
    }

    public VariableList getVariableList () {
        return variables;
    }
    

    public double getActiveTurtleIndex () {
        return (double) activeTurtleIndex;
    }
    
    public void setActiveTurtleIndex(int index){
        activeTurtleIndex = index;
    }
    
    public Map<String, List<CommandTreeNode>> getUserCommands(){
        return userCommands;
    }
    
    public void setUserCommands(Map<String, List<CommandTreeNode>> commands){
        userCommands = commands;
    }

}
