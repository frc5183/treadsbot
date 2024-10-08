package frc.robot.control;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Robot;
import frc.robot.control.command.Command;
import frc.robot.control.enumeration.Button;

public class AutonomousButtonMapper {
    public interface CommandSource {
        Command getCommand();
    }
    private Timer timer = new Timer();
    private CommandSource commandSource;
    private Button button;
    private boolean state = false;
    private XboxController xbox;
    private boolean finished;
    private double spacing;
    private double last;
    public AutonomousButtonMapper(CommandSource commandSource, XboxController xbox, Button button, double spacing) {
        this.commandSource = commandSource;
        this.xbox = xbox;
        this.button = button;
        this.spacing=spacing;
        timer.start();
    }
    public void periodic() {
        if (Button.getButtonValue(button, xbox) && !state && (timer.hasElapsed(spacing))) {
            Robot.scheduler.interrupt(commandSource.getCommand());
            state = true;
            timer.restart();
        }
        state = Button.getButtonValue(button, xbox);
    }
}
