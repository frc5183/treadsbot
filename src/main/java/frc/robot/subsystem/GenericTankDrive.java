package frc.robot.subsystem;

import frc.robot.Tuple2;
import frc.robot.hardware.encoder.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import frc.robot.control.tuple.TupleControl;
import frc.robot.hardware.gyro.SingleAxisGyroscope;
import frc.robot.hardware.motor.Motor;
import frc.robot.hardware.motor.MotorGroup;


/**
 * A class which allows for any easy combination of a left/right tank drive.
 * Includes an Encoder and SingleAxisGyroscope for convenience
 * Takes any 5183-class Motor for left and right, allowing for MotorGroups as well.
 * @implNote if using PhoenixMotors, considering NOT using MotorGroups and using their native follow feature
 */
public class GenericTankDrive extends Subsystem {
    private final DifferentialDrive drive;
    private final Motor left;
    private final Motor right;
    // private final Encoder encoder;
    private final SingleAxisGyroscope gyroscope;

    /**
     * @param left Motor
     * @param right Motor
     * @param encoder Encoder
     * @param gyroscope Gyroscope
     * @see MotorGroup
     * @see Encoder
     * @see SingleAxisGyroscope
     */
    public GenericTankDrive(
            Motor left,
            Motor right,
            SingleAxisGyroscope gyroscope) {
        this.drive = new DifferentialDrive(left, right);
        this.left = left;
        this.right = right;
        this.gyroscope = gyroscope;
        this.left.setSafety(true);
        this.right.setSafety(true);
    }
    /**
     * @see DifferentialDrive#arcadeDrive(double, double)
     * @see TupleControl#getValue()
     * @param tupleControl The tuple control to use
     */
    public void arcadeDrive(TupleControl tupleControl) {
        Tuple2<Double> val = tupleControl.getValue();
        drive.arcadeDrive(val.getVal2(), val.getVal1());
    }

    /**
     * @see DifferentialDrive#arcadeDrive(double, double, boolean)
     * @see TupleControl#getValue()
     * @param tupleControl The tuple control to use
     * @param squared Whether to square the inputs
     */
    public void arcadeDrive(TupleControl tupleControl, boolean squared) {
        Tuple2<Double> val = tupleControl.getValue();
        drive.arcadeDrive(val.getVal2(), val.getVal1(), squared);
    }

    /**
     * @param speed The speed to set the left and right motor groups to
     */
    public void setSpeed(double speed) {
        left.set(speed);
        right.set(speed);
    }

    /**
     * @return The average speed of the left and right motor groups
     */
    public double getSpeed() {
        return (left.get() + right.get()) / 2;
    }

    /**
     * @return The Gyroscope that should be placed on the drivetrain axis
     */
    public SingleAxisGyroscope getGyroscope() {
        return gyroscope;
    }

}
