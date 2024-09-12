package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

// Anthony, 8/28/2024
// This code was copied from RobotAutoDriveByTime_Linear in the FTC SDK samples and modified
// to have four motors (for mecanum drive) and for the Cyber Tiger robot motor names
@Autonomous(name="Cyber Tigers: Basic Autonomous A", group="Robot")
public class BasicAutonomousControlTeamA extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor frontLeftMotor;
    private DcMotor backLeftMotor;
    private DcMotor frontRightMotor;
    private DcMotor backRightMotor;

    private ElapsedTime     runtime = new ElapsedTime();

    static final double     FORWARD_SPEED = 0.6;
    static final double     TURN_SPEED    = 0.5;

    @Override
    public void runOpMode() {
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");

        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.REVERSE);

        // Send telemetry message to signify robot waiting;
        telemetry.addData("Status", "Ready to run");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // Step through each leg of the path, ensuring that the Auto mode has not been stopped along the way

        // Step 1:  Drive forward for 1 seconds
        driveRobotForward(FORWARD_SPEED, 1.0);

        // Step 2: Drive backward for 1 seconds
        driveRobotBackward(FORWARD_SPEED, 0.4);

        // Step 3: Spin right
        spinRobotRight(TURN_SPEED, 0.8);

        driveRobotForward(FORWARD_SPEED,1.0);

        strafeRobotLeft(FORWARD_SPEED, 0.5);

        driveRobotForward(FORWARD_SPEED, 0.9);

        // Step 7:  Stop
        frontLeftMotor.setPower(0);
        backLeftMotor.setPower(0);
        frontRightMotor.setPower(0);
        backRightMotor.setPower(0);

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);
    }

    private void driveRobot(double frontLeftSpeed, double backLeftSpeed, double frontRightSpeed,
                            double backRightSpeed, double runtimeSeconds) {
        frontLeftMotor.setPower(frontLeftSpeed);
        frontRightMotor.setPower(frontRightSpeed);
        backLeftMotor.setPower(backLeftSpeed);
        backRightMotor.setPower(backRightSpeed);
        runtime.reset();
        while (opModeIsActive() && (runtime.seconds() < runtimeSeconds)) {
            telemetry.addData("Path", "Moved:%4.1f S Elapsed", runtime.seconds());
            telemetry.update();
        }
    }
    private void driveRobotForward(double speed, double runtimeSeconds) {
        driveRobot(speed, speed, speed, speed, runtimeSeconds);

    }
    private void driveRobotBackward(double speed, double runtimeSeconds) {
        driveRobot(-speed,-speed,-speed,-speed,runtimeSeconds);

    }
    private void spinRobotLeft(double speed, double runtimeSeconds) {
        driveRobot(-speed,-speed,speed,speed,runtimeSeconds);

    }

    private void spinRobotRight(double speed, double runtimeSeconds){
        driveRobot(speed,-speed,speed,-speed,runtimeSeconds);
    }
    private void strafeRobotLeft(double speed, double runtimeSeconds){
        driveRobot(speed,speed, speed, speed, runtimeSeconds);
    }

}