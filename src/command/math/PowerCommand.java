package command.math;

import command.Command;


/**
 * Returns the n where n = op1 raised to the power of op2
 * 
 * @author OWNER
 *
 */

public class PowerCommand extends Command {
    private double double1;
    private double double2;

    public PowerCommand (double op1, double op2) {
        double1 = op1;
        double2 = op2;
    }

    public double calculateValue () {
        return Math.pow(double1, double2);
    }
}
