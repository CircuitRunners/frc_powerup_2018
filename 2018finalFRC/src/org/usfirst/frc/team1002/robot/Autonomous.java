package org.usfirst.frc.team1002.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Autonomous {

	private static int posIndex;
	private static int targIndex;
	private static String FMSString; // three character string from FMS.
	private static int sideSwitch; // side switch is on.
	private static int sideScale; // side scale is on.
	private static final double SCALESPEED = 0.8; // code.
	private static final double SWITCHSPEED = 0.6;
	private static boolean switchNear;
	private static boolean scaleNear;
	private static boolean debug = false;

	int step = 1;
	int turnDir = 1;

	// private static MarioDrive drive = new MarioDrive();
	// private static EightBitElev elev = new EightBitElev();
	// private static RobotArm arm = new RobotArm();
	// private static Grabber grab = new Grabber();

	static final int LEFT = 1;
	static final int CENTER = 2;
	static final int RIGHT = 3;
	static final int SWITCH = 10;
	static final int SCALE = 11;
	static final int SWITCHORSCALE = 22;
	static final int SWITCHANDSCALE = 23;
	static final int NONE = 12;

	public void init() {
		if (debug) {
			posIndex = Robot.posSelected;
			targIndex = Robot.targSelected;
			return;
		}
		switch (Robot.posSelected) {
		case (LEFT):
			posIndex = LEFT;
			break;
		case (CENTER):
			posIndex = CENTER;
			break;
		case (RIGHT):
			posIndex = RIGHT;
			break;
		}
		switch (Robot.targSelected) {
		case SCALE:
			targIndex = SCALE;
			break;
		case SWITCH:
			targIndex = SWITCH;
			break;
		case SWITCHORSCALE:
			targIndex = SWITCHORSCALE;
		case SWITCHANDSCALE:
			targIndex = SWITCHANDSCALE;
		}
		if (posIndex == LEFT) {
			turnDir = 1;
		} else if (posIndex == RIGHT) {
			turnDir = -1;
		} else {
			turnDir = 0;
		}
		SmartDashboard.putNumber("Robot Position", posIndex);
		SmartDashboard.putNumber("Target Selected: ", targIndex);

	}

	static boolean called = false;

	public void run() {
		switch (posIndex) {
		case RIGHT:

		case LEFT:
			if (targIndex == SCALE) {
				if (scaleNear) {
					sameSideScale();
				} else {
					farSideScale();
				}
			} else if (targIndex == SWITCH) {
				if (switchNear) {
					sameSideSwitch();
				} else {
					farSideSwitch();
				}
			} else if (targIndex == SWITCHORSCALE) {
				if (switchNear)
					sameSideSwitch();
				else if (scaleNear) {
					sameSideScale();
				} else {
					farSideScale();
				}

			} else if(targIndex == SWITCHANDSCALE) {
				if(switchNear) {
					if(scaleNear) {
						scaleAndSwitch();
					}
				} else if(scaleNear) {
					sameSideScale();
				} else {
					farSideScale();
				}
			}
			break;
		case CENTER:
			if (targIndex == SWITCH) {
				if (sideScale == RIGHT) {
					turnDir = -1;
					centerSwitch();
				} else {
					turnDir = 1;
					centerSwitch();
				}

			} else {
				driveForward();
			}
			break;

		}
	}

	public boolean getAutoRoutine() {

		if (DriverStation.getInstance().getGameSpecificMessage().length() != 3)
			return false;

		// The field will take about a second to send the fms string so you cant grab
		// it in init; instead, keep looping this until you get the string, then go
		// through the algorithm.
		FMSString = DriverStation.getInstance().getGameSpecificMessage();
		SmartDashboard.putString("FMS String", FMSString);
		if (FMSString.substring(0, 1).equalsIgnoreCase("L"))
			sideSwitch = LEFT;
		else
			sideSwitch = RIGHT;
		if (FMSString.substring(1, 2).equalsIgnoreCase("L"))
			sideScale = LEFT;
		else
			sideScale = RIGHT;
		SmartDashboard.putNumber("sideScale", sideScale);
		SmartDashboard.putNumber("sideSwitch", sideSwitch);

		switchNear = (posIndex == sideSwitch);
		scaleNear = (posIndex == sideScale);
		SmartDashboard.putBoolean("switchNear", switchNear);
		SmartDashboard.putBoolean("scaleNear", scaleNear);

		return true;

	}

	double startingTime;

	private void farSideSwitch() {
		SmartDashboard.putString("Auto Program", "farSideSwitch");
		switch (step) {
		case 1:
			beginStep();
			startingTime = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("Time Elapsed", 0);
			Robot.drive.autoDrive(17.6, SWITCHSPEED, 6);
			RobotData.elevPositionTarget = Robot.elev.moveTo(20, 100, 5);
			step++;
			break;
		case 2:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(90 * turnDir, 4);
			step++;
			break;
		case 3:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(11, SWITCHSPEED, 6);
			RobotData.armPositionTarget = Robot.arm.moveTo(0, 100, 5);
			RobotData.elevPositionTarget = Robot.elev.moveTo(15, 100, 5);
			step++;
			break;
		case 4:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(180 * turnDir, 3);
			step++;
			break;
		case 5:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(.5, SWITCHSPEED / 2, 6);
			step++;
			break;
		case 6:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.6, 1);
			step++;
			break;
		case 7:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-6.0, SWITCHSPEED, 4);
			step++;
			break;
		case 8:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 5);
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 5);
			step++;
			break;
		case 9:
			SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
			step++;
			break;
		case 10:
			break;

		}
		// driveForward();
	}

	private void sameSideSwitch() {
		SmartDashboard.putString("Auto Program", "sameSideSwitch");

		switch (step) {
		case 1:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			startingTime = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("Time Elapsed", 0);
			Robot.drive.autoDrive(11, 0.5, 4);
			RobotData.elevPositionTarget = Robot.elev.moveTo(20, 100, 5);
			step++;
			break;
		case 2:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle())
				break;
			beginStep();

			Robot.drive.autoTurn(90 * turnDir, 4);
			RobotData.elevPositionTarget = Robot.elev.moveTo(15, 100, 3);
			RobotData.armPositionTarget = Robot.arm.moveTo(0, 100, 3);
			step++;
			break;
		case 3:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(0.5, SWITCHSPEED / 2, 6);
			step++;
			break;
		case 4:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.6, 0.6);
			step++;
			break;
		case 5:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-1.5, SWITCHSPEED / 2, 4);
			step++;
			break;
		case 6:
			if (Robot.drive.isIdle())
				break;
			beginStep();
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 5);
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 5);
			step++;
			break;
		case 7:
			SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
			step++;
			break;
		case 8:
			break;
		}

	}

	private void farSideScale() {
		SmartDashboard.putString("Auto Program", "farSideScale");
		switch (step) {
		case 1:
			startingTime = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("Time Elapsed", 0);
			Robot.drive.autoDrive(16.3, SCALESPEED, 6);
			RobotData.elevPositionTarget = Robot.elev.moveTo(20, 100, 5);
			step++;
			break;
		case 2:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(88.5 * turnDir, 6);
			step++;
			break;
		case 3:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(12.75, SCALESPEED, 6);
			RobotData.armPositionTarget = Robot.arm.moveTo(30, 100, 5);
			RobotData.elevPositionTarget = Robot.elev.moveTo(RobotData.elevMaxHeightUnits, 100, 5);
			step++;
			break;
		case 4:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(0 * turnDir, 6);
			step++;
			break;
		case 5:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(1.3, SCALESPEED / 2, 4);// drive forward to deliver 1st block
			step++;
			break;
		case 6:
			if (!Robot.drive.isIdle() || !Robot.grab.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.6, 0.4);
			step++;
			break;
		case 7:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-1.75, SCALESPEED / 2, 3);
			step++;
			break;
		case 8:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 5);
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 5);
			Robot.drive.autoTurn(-165 * turnDir, 4);
			step++;
			break;
		case 9:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();

			Robot.drive.autoDrive(1, SCALESPEED / 2, 5);// drive forward to get block
			Robot.grab.intake(1.6, 0.4);
			step++;
			break;
		case 10:
			if (!Robot.drive.isIdle() || !Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(-10 * turnDir, 5);
			Robot.elev.moveTo(RobotData.elevMaxHeightUnits, 100, 5);
			Robot.arm.moveTo(30, 100, 5);
			step++;
			break;
		case 11:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(0.3, SCALESPEED / 3, 3);
			step++;
			break;
		case 12:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.4, 0.6);
			step++;
			break;
		case 13:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-1.75, SCALESPEED, 3);
			step++;
			break;
		case 14:
			if (!Robot.drive.isIdle())
				break;
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 5);
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 5);
			step++;
			break;
		case 15:
			SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
			step++;
			break;
		case 16:
			break;
		}
		// driveForward();
	}

	public void sameSideScale() {
		SmartDashboard.putString("Auto Program", "sameSideScale");
		switch (step) {
		case 1:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			startingTime = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("Time Elapsed", 0);
			Robot.drive.autoDrive(6.0, SCALESPEED, 4);
			RobotData.elevPositionTarget = Robot.elev.moveTo(20, 100, 5);
			step++;
			break;
		case 2:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(8, SCALESPEED, 6);
			RobotData.elevPositionTarget = Robot.elev.moveTo(RobotData.elevMaxHeightUnits, 100, 5);
			RobotData.armPositionTarget = Robot.arm.moveTo(30, 100, 5);
			step++;
			break;
		case 3:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(35 * turnDir, 3);
			step++;
			break;
		case 4:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(1, SCALESPEED / 2, 4);
			step++;
			break;
		case 5:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.6, 0.4);
			step++;
			break;
		case 6:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-2, SCALESPEED / 2, 3);
			step++;
			break;
		case 7:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 5);
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 5);
			Robot.drive.autoTurn(152 * turnDir, 4);
			step++;
			break;
		case 8:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(2, SCALESPEED / 2, 5);
			Robot.grab.intake(2, 0.75);
			step++;
			break;
		case 9:
			if (!Robot.drive.isIdle() || !Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-1.5, SCALESPEED / 2, 3);
			Robot.elev.moveTo(RobotData.elevMaxHeightUnits, 100, 5);
			step++;
			break;
		case 10:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(35 * turnDir, 4);
			Robot.arm.moveTo(30, 100, 5);
			step++;
			break;
		case 11:
			if (!Robot.drive.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(1, SCALESPEED / 2, 6);
			step++;
			break;
		case 12:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.6, 0.8);
			step++;
			break;
		case 13:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-3.5, SCALESPEED / 2, 4);
			step++;
			break;
		case 14:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(90 * turnDir, 3);
			Robot.elev.moveTo(0, 100, 5);
			Robot.arm.moveTo(-25, 100, 5);
			step++;
			break;
		case 15:
			SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
			step++;
			break;
		case 16:
			break;

		}
	}

	public void driveForward() {
		switch (step) {
		case 1:
			startingTime = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("Time Elapsed", 0);
			Robot.drive.autoDrive(10.5, SCALESPEED, 5);
			RobotData.elevPositionTarget = Robot.elev.moveTo(15, 100, 5);
			step++;
			break;
		case 2:
			SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
			step++;
			break;
		case 3:
			break;
		}
	}

	public void centerSwitch() {
		switch (step) {
		case 1:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(2, SWITCHSPEED, 4);
			RobotData.elevPositionTarget = Robot.elev.moveTo(15, 100, 5);
			RobotData.armPositionTarget = Robot.arm.moveTo(0, 100, 5);
			step++;
			break;
		case 2:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(45 * turnDir, 4);
			step++;
			break;
		case 3:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(6, SWITCHSPEED, 7);
			step++;
			break;
		case 4:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(0, 4);
			step++;
			break;
		case 5:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(2, SWITCHSPEED / 2, 4);
			step++;
			break;
		case 6:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.6, 0.6);
			step++;
			break;
		case 7:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-2, SWITCHSPEED, 3);
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 5);
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 5);
			step++;
			break;
		case 8:
			SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
			step++;
			break;
		case 9:
			break;

		}
	}

	public void scaleAndSwitch() {
		SmartDashboard.putString("Auto Program", "switchAndScale");
		switch (step) {
		case 1:
			beginStep();
			startingTime = Timer.getFPGATimestamp();
			SmartDashboard.putNumber("Time Elapsed", 0);
			Robot.drive.autoDrive(11, 0.5, 4);
			RobotData.elevPositionTarget = Robot.elev.moveTo(20, 100, 5);
			step++;
			break;
		case 2:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle())
				break;
			beginStep();

			Robot.drive.autoTurn(90 * turnDir, 4);
			RobotData.elevPositionTarget = Robot.elev.moveTo(15, 100, 3);
			RobotData.armPositionTarget = Robot.arm.moveTo(0, 100, 2);
			step++;
			break;
		case 3:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(0.5, SWITCHSPEED / 2, 6);
			step++;
			break;
		case 4:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.grab.eject(0.3, 1);
			step++;
			break;
		case 5:
			if (!Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-1.5, SWITCHSPEED / 2, 4);
			step++;
			break;
		case 6:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(0, 4);
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 2);
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 3);
			step++;
			break;
		case 7:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(3, SCALESPEED, 3);
			step++;
			break;
		case 8:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(110 * turnDir, 3);
			step++;
			break;
		case 9:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(1.5, SCALESPEED / 2, 3);
			Robot.grab.intake(0.5, 0.5);
			step++;
			break;
		case 10:
			if (!Robot.drive.isIdle() || !Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-1.5, SCALESPEED / 2, 3);
			Robot.grab.intake(0.8, 0.5);
			step++;
			break;
		case 11:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(0, 3);
			RobotData.elevPositionTarget = Robot.elev.moveTo(RobotData.elevMaxHeightUnits, 100, 4);
			RobotData.armPositionTarget = Robot.arm.moveTo(70, 100, 4);
			step++;
			break;
		case 12:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(6.2, SCALESPEED, 3);
			step++;
			break;
		case 13:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoTurn(35 * turnDir, 3);

			step++;
			break;
		case 14:
			if (!Robot.drive.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(1, SCALESPEED / 2, 3);
			Robot.grab.eject(0.7, 0.5);
			step++;
			break;
		case 15:
			if (!Robot.drive.isIdle() || !Robot.grab.isIdle())
				break;
			beginStep();
			Robot.drive.autoDrive(-2, SCALESPEED, 3);
			RobotData.elevPositionTarget = Robot.elev.moveTo(0, 100, 4);
			RobotData.armPositionTarget = Robot.arm.moveTo(-25, 100, 4);
			step++;
			break;
		case 16:
			if (!Robot.drive.isIdle() || !Robot.elev.isIdle() || !Robot.arm.isIdle())
				break;
			SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
			step++;
			break;
		case 17:
			break;
		}

	}

	boolean delayed = false;

	void beginStep() {

		SmartDashboard.putNumber("Time Elapsed", Timer.getFPGATimestamp() - startingTime);
		if (delayed) {
			Timer.delay(1);
		}
	}
}
