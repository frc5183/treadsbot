package frc.robot.hardware.motor;

import edu.wpi.first.wpilibj.motorcontrol.Spark;
/**
 * A wrapper class around Spark to make it compatible with other motor types
 */
public class SparkMotor extends Motor{
    private final Spark sparkMotor;
    public SparkMotor(int port) {
        sparkMotor = new Spark(port);
    }
    public void set(double speed) {
        sparkMotor.set(speed);
    }
    @Override
    public void setVoltage(double outputVolts) {
        sparkMotor.setVoltage(outputVolts);
    }
    public void periodic() {
        sparkMotor.feed();
    }
    public double get() {
        return sparkMotor.get();
    }
    public void setSafety(boolean on) {
        sparkMotor.setSafetyEnabled(on);
    }
    public void setInverted(boolean inverted) {
        sparkMotor.setInverted(inverted);
    }
    public boolean getInverted() {
        return sparkMotor.getInverted();
    }
    @Override
    public void stopMotor() {
        sparkMotor.disable();
    }
    @Override
    public void disable() {
        sparkMotor.disable();
    }
    public Spark getRawMotor() {
        return sparkMotor;
    }
}
