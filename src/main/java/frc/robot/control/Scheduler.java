package frc.robot.control;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.util.sendable.SendableBuilder;
import frc.robot.control.command.Command;
import frc.robot.subsystem.Subsystem;

import java.util.ArrayList;
import java.util.LinkedList;

public class Scheduler {
    public interface SubsystemRemover {
        abstract boolean remove(Subsystem s);
    }
    /**
     * Simply a list used to prevent ConcurrentModificationExceptions
     */
    private final ArrayList<Command> temp = new ArrayList<>();

    /**
     * A list to contain all actively used Subsystems generated by the activeCommands list
     */
    private final ArrayList<Subsystem> activeSubsystems = new ArrayList<>();

    /**
     * A queue to be used for the commands needing to be scheduled.
     * Generally will be used as a first-come first-server basis,
     * however, a command will be scheduled before an earlier command
     * if it shares no requirements with that command and its requirements
     * are available
     */
    private final LinkedList<Command> commandQueue = new LinkedList<>();
    /**
     * A list to hold all the currently active Commands
     */
    private final ArrayList<Command> activeCommands = new ArrayList<>();
    private final ArrayList<Command> interruptQueue = new ArrayList<>();

    /**
     * Adds a command to the end of the queue.
     * @param command the command to add
     */
    public void scheduleCommand(Command command) {
        commandQueue.add(command);
    }

    /**
     * The main method of the Scheduler, to be run every periodic loop.
     */
    public void run() {
        // Clear Lists
        activeSubsystems.clear();
        temp.clear();
        // Iterate over every Active Command
        // Run it; If it's finished, End it, Mark it for removal by adding to temp
        // Else count its used subsystems into activeSubsystems
        internal_interrupt();
        for (Command c : activeCommands) {
            c.run();
            if (c.isFinished()) {
                System.out.println(c.getName() + " Finishing!");
                c.clean();
                temp.add(c);
            } else {
                for (Subsystem s : c.getRequiredSubsystems()) {
                    // Prevent Duplicate Entries
                    if (!activeSubsystems.contains(s)) {
                        activeSubsystems.add(s);
                    }
                }
            }
        }
        // Remove finished commands from activeCommand
        for (Command c : temp) {
            activeCommands.remove(c);
        }
        // Clear temp again
        temp.clear();
        // Iterate over every command in the queue
        for (Command c: commandQueue) {
            boolean okay = true;
            // Strict checking for used subsystems. A command MUST not run if it
            // Needs subsystems that are currently being used.
            for (Subsystem s : c.getRequiredSubsystems()) {
                if (activeSubsystems.contains(s)) {
                    okay = false;
                }
            }
            if (okay) {
                temp.add(c);
                for (Subsystem s : c.getRequiredSubsystems()) {
                    if (!activeSubsystems.contains(s)) {
                        activeSubsystems.add(s);
                    }
                }
            }
        }
        for (Command c: temp) {
            commandQueue.remove(c);
            activeCommands.add(c);
            if (!c.started) {
                System.out.println(c.getName() + " Starting!");
                c.start();
                c.started = true;
            }

        }
    }

    /**
     * Can be used to clear the queue and end all running commands prematurely.
     * This method is better than just not running run() again because it calls all needed clean() commands
     * And prevents reuse of "dirtied" Commands
     */
    public void forceEnd() {
        commandQueue.clear();
        temp.clear();
        activeSubsystems.clear();
        for (Command c: activeCommands) {
            c.clean();
        }
        activeCommands.clear();
    }

    private void internal_interrupt() {
        for (Command command : interruptQueue) {
            for (Command c : activeCommands) {
                if (c != command) {
                    commandQueue.add(0, c);
                }
            }
            activeCommands.clear();
            commandQueue.add(0, command);
        }
        interruptQueue.clear();
    }

    /**
     * Interrupts the current queue with a single command.
     * The current active commands and pushed back to the front of the queue.
     * @param command the command to skip the que with
     */
    public void interrupt(Command command) {
        interruptQueue.add(command);
    }

    /**
     * Overrides the entire command scheduler with a single command.
     * The queue and active commands are all cleared
     * @param command the command to override the entire Scheduler with
     */
    public void override(Command command) {
        forceEnd();
        commandQueue.add(command);
    }

    public Sendable getBasicSendable() {
        return new Sendable() {
            @Override
            public void initSendable(SendableBuilder builder) {
                builder.setSmartDashboardType("Scheduler");
                builder.addBooleanProperty("isRunning", () -> !activeCommands.isEmpty(), null);
                builder.addBooleanProperty("isQueued", () -> !commandQueue.isEmpty(), null);
            }
        };
    }
    public Sendable getQueueSendable() {
        return new Sendable() {
            @Override
            public void initSendable(SendableBuilder builder) {
                builder.setSmartDashboardType("Scheduler");
                builder.addStringProperty("Queue", () -> {
                    String out = "";
                    for (int i = 0; i < commandQueue.size(); i++) {
                        out += commandQueue.get(i).getName() + ",\n";
                    }
                    return out;
                }, null);
            }
        };
    }
    public Sendable getActiveSendable() {
        return new Sendable() {
            @Override
            public void initSendable(SendableBuilder builder) {
                builder.setSmartDashboardType("Scheduler");
                builder.addStringProperty("Active", () -> {
                        String out = "";
                        for (int i=0; i<activeCommands.size(); i++) {
                            out+= activeCommands.get(i).getName() + ",\n";
                        }
                    return out;
                }, null);
            }
        };
    }
    public void runClear(SubsystemRemover remover) {
        for (Subsystem s : activeSubsystems) {
            if (remover.remove(s)) {
                activeSubsystems.remove(s);
            }
        }
    }
}
