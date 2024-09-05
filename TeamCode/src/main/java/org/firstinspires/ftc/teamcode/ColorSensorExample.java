package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

// Anthony, 9/5/2024
// This code was copied from RobotAutoDriveToAprilTagOmni in the FTC SDK samples and modified
// for the Cyber Tiger robot motor names
@TeleOp(name = "Cyber Tigers: Color Sensor Example", group = "Robot")
public class ColorSensorExample extends LinearOpMode {
  NormalizedColorSensor colorSensor;
  private DcMotor leftFrontDrive;
  private DcMotor leftBackDrive;
  private DcMotor rightFrontDrive;
  private DcMotor rightBackDrive;

  private void initialize() {
    colorSensor = hardwareMap.get(NormalizedColorSensor.class, "colorV3");

    leftFrontDrive  = hardwareMap.get(DcMotor.class, "frontLeftMotor");
    leftBackDrive  = hardwareMap.get(DcMotor.class, "backLeftMotor");
    rightFrontDrive = hardwareMap.get(DcMotor.class, "frontRightMotor");
    rightBackDrive = hardwareMap.get(DcMotor.class, "backRightMotor");

    leftFrontDrive.setDirection(DcMotor.Direction.REVERSE);
    leftBackDrive.setDirection(DcMotor.Direction.REVERSE);

    telemetry.addData("Status", "Initialized");
    telemetry.update();
  }

  @Override public void runOpMode() {
    initialize();

    // Wait for the start button to be pressed.
    waitForStart();

    // Loop until we are asked to stop
    while (opModeIsActive()) {
      readAndDisplayColorSensorValues();

      moveRobotBasedOnGamepadInput();

      telemetry.update();
    }
  }

  private void readAndDisplayColorSensorValues() {
    final float[] hsvValues = new float[3];

    // Get the normalized colors from the sensor
    NormalizedRGBA colors = colorSensor.getNormalizedColors();

    // Update the hsvValues array by passing it to Color.colorToHSV()
    Color.colorToHSV(colors.toColor(), hsvValues);

    telemetry.addLine()
            .addData("Red", "%.3f", colors.red)
            .addData("Green", "%.3f", colors.green)
            .addData("Blue", "%.3f", colors.blue);
    telemetry.addLine()
            .addData("Hue", "%.3f", hsvValues[0])
            .addData("Saturation", "%.3f", hsvValues[1])
            .addData("Value", "%.3f", hsvValues[2]);

    if (hsvValues[0] < 10) {
      telemetry.addData("Found Color", "GRAY FIELD");
    }
  }

  private void moveRobotBasedOnGamepadInput() {
    double max;

    // POV Mode uses left joystick to go forward & strafe, and right joystick to rotate.
    double axial   = -gamepad1.left_stick_y;  // Note: pushing stick forward gives negative value
    double lateral =  gamepad1.left_stick_x;
    double yaw     =  gamepad1.right_stick_x;

    // Combine the joystick requests for each axis-motion to determine each wheel's power.
    // Set up a variable for each drive wheel to save the power level for telemetry.
    double leftFrontPower  = axial + lateral + yaw;
    double rightFrontPower = axial - lateral - yaw;
    double leftBackPower   = axial - lateral + yaw;
    double rightBackPower  = axial + lateral - yaw;

    // Normalize the values so no wheel power exceeds 100%
    // This ensures that the robot maintains the desired motion.
    max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
    max = Math.max(max, Math.abs(leftBackPower));
    max = Math.max(max, Math.abs(rightBackPower));

    if (max > 1.0) {
      leftFrontPower  /= max;
      rightFrontPower /= max;
      leftBackPower   /= max;
      rightBackPower  /= max;
    }

    // Send calculated power to wheels
    leftFrontDrive.setPower(leftFrontPower);
    rightFrontDrive.setPower(rightFrontPower);
    leftBackDrive.setPower(leftBackPower);
    rightBackDrive.setPower(rightBackPower);
  }
}
