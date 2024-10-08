package frc.robot.hardware.motor;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
/**
 * A wrapper class around WPI_VictorSPX to make it compatible with other motor types
 * @apiNote This class is ONLY available with Phoenix5, and will not be available with Phoenix6.
 */
public class VictorSPXMotor extends PhoenixMotor {
    final WPI_VictorSPX motor;
    public VictorSPXMotor(int id) {
        motor = new WPI_VictorSPX(id);
        super.motor = motor;
    }

    public void setVoltage(double outputVolts) {
        motor.setVoltage(outputVolts);
    }
    public WPI_VictorSPX getRawMotor() {
        return motor;
    }
    public WPI_VictorSPX getRawMasterMotor() {
        return getRawMotor();
    }
    public void periodic() {
        motor.feed();
    }
    public void setSafety(boolean on) {
        motor.setSafetyEnabled(on);
    }
    @Override
    public void disable() {
        motor.disable();
    }
    @Override
    public void stopMotor() {
        motor.stopMotor();
    }

    public void setInverted(boolean inverted) {
        motor.setInverted(inverted);
    }
    public void setInverted(InvertType inverted) {
        motor.setInverted(inverted);
    }
}
