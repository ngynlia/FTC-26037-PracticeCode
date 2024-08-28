package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

public class FieldCentric_TeleOp extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        DcMotor backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        float hsvValues[] = {0F,0F,0F};
        final float values[] = hsvValues;

        ColorSensor color;
        color = hardwareMap.get(ColorSensor.class, "Color");

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            if ((hsvValues[0] > 159 ) && (hsvValues[0] < 164)) {
                telemetry.addData("Detected Color", "Tiles");
            }
            else if ((hsvValues[0] > 344 ) && (hsvValues[0] < 354)) {
                telemetry.addData("Detected Color", "Pink");
            }
            else if ((hsvValues[0] > 228 ) && (hsvValues[0] < 238)) {
                telemetry.addData("Detected Color", "Purple");
            }
            else if ((hsvValues[0] > 198 ) && (hsvValues[0] < 208)) {
                telemetry.addData("Detected Color", "Blue");
            }
            else if ((hsvValues[0] > 143 ) && (hsvValues[0] < 153)) {
                telemetry.addData("Detected Color", "Green");
            }
            else if ((hsvValues[0] > 81 ) && (hsvValues[0] < 91)) {
                telemetry.addData("Detected Color", "Yellow");
            }
            else if ((hsvValues[0] > 33 ) && (hsvValues[0] < 43)) {
                telemetry.addData("Detected Color", "Orange");
            }
            else if ((hsvValues[0] > 15 ) && (hsvValues[0] < 25)) {
                telemetry.addData("Detected Color", "Red");
            }
            else {
                telemetry.addData("Detected Color", "No Color Dectected");
            }

            telemetry.addData("Red", color.red());
            telemetry.addData("Green", color.green());
            telemetry.addData("Blue", color.blue());
            Color.RGBToHSV(color.red(), color.green(), color.blue(), hsvValues);
            telemetry.addData("Hue", hsvValues[0]);
            telemetry.update();
        }
    }
}
