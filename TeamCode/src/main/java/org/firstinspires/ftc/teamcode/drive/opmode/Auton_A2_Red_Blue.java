package org.firstinspires.ftc.teamcode.drive.opmode;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;
import org.firstinspires.ftc.teamcode.drive.DriveConstants;
import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;

import java.util.List;

/*
 * This is a complex autonomous that will score 2 wobble goals in a complex path on square B
 */
@Autonomous(group = "drive")
public class Auton_A2_Red_Blue extends LinearOpMode {
    //We have an issue with using the same auton for both sides. The start positions are different, and that could lead to potential issues.
    private Servo wobbleDropper;
    private double slowerVelocity = 5;
    private DcMotor wobbleArm;
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
//        wobbleDropper = hardwareMap.get(Servo.class, "wobbleDropper");
//        TFObjectDetector tfod = drive.getTfod();
        wobbleArm = drive.getWobbleArm();
        TFObjectDetector tfod = drive.getTfod();
        /**
         * Activate TensorFlow Object Detection before we wait for the start command.
         * Do it here so that the Camera Stream window will have the TensorFlow annotations visible.
         **/
        if (tfod != null) {
            tfod.activate();

            // The TensorFlow software will scale the input images from the camera to a lower resolution.
            // This can result in lower detection accuracy at longer distances (> 55cm or 22").
            // If your target is at distance greater than 50 cm (20") you can adjust the magnification value
            // to artificially zoom in to the center of image.  For best results, the "aspectRatio" argument
            // should be set to the value of the images used to create the TensorFlow Object Detection model
            // (typically 16/9).
            tfod.setZoom(1.0, 16.0/9.0);
            telemetry.addLine("TFod Activated!");
            telemetry.update();
        }

            while (!opModeIsActive()) {
                if (tfod != null) {
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        // step through the list of recognitions and display boundary info.
                        int i = 0;
                        for (Recognition recognition : updatedRecognitions) {
                            telemetry.addData(String.format("label (%d)", i), recognition.getLabel());
                            telemetry.addData(String.format("  left,top (%d)", i), "%.03f , %.03f",
                                    recognition.getLeft(), recognition.getTop());
                            telemetry.addData(String.format("  right,bottom (%d)", i), "%.03f , %.03f",
                                    recognition.getRight(), recognition.getBottom());
                        }
                        telemetry.update();
                    }
                }
            }
            waitForStart();

        if (isStopRequested()) return;
//        drive.moveTo("Away");
        // We want to start the bot at x: 10, y: -8, heading: 90 degrees
        Pose2d startPose = new Pose2d(-60, 48, Math.toRadians(0));

        drive.setPoseEstimate(startPose);

        Trajectory trajA1 = drive.trajectoryBuilder(startPose)
                .lineTo(new Vector2d(12, 48))
                .build();

        Trajectory trajA2 = drive.trajectoryBuilder(trajA1.end())
                .lineToConstantHeading(new Vector2d(12, 19))
                .build();

        Trajectory trajA3 = drive.trajectoryBuilder(trajA2.end())
                .lineToConstantHeading(new Vector2d(-18, 19))
                .build();

        Trajectory trajA4 = drive.trajectoryBuilder(trajA3.end())
                .lineTo(new Vector2d(-27, 19),
                        SampleMecanumDrive.getVelocityConstraint(slowerVelocity, DriveConstants.MAX_ANG_VEL, DriveConstants.TRACK_WIDTH),
                        SampleMecanumDrive.getAccelerationConstraint(DriveConstants.MAX_ACCEL)
                )
                .build();

        Trajectory trajA5 = drive.trajectoryBuilder(trajA4.end())
                .lineToConstantHeading(new Vector2d(12, 19))
                .build();

        Trajectory trajA6 = drive.trajectoryBuilder(trajA5.end())
                .lineToLinearHeading(new Pose2d(12, 36, Math.toRadians(-90)))
                .build();

        Trajectory trajA7 = drive.trajectoryBuilder(trajA6.end(), false)
                .forward(6)
                .build();

//      Move to block b
        drive.followTrajectory(trajA1);
        drive.wobbleDrop();
        drive.moveTo("Down");
        sleep(2000);
        drive.followTrajectory(trajA2);
        drive.followTrajectory(trajA3);
        drive.followTrajectory(trajA4);
        drive.wobbleGrab();
        sleep(2000);
        drive.moveTo("Carry");
        drive.followTrajectory(trajA5);
        drive.followTrajectory(trajA6);
        drive.moveTo("Down");
        drive.wobbleRelease();
        sleep(2000);
        drive.followTrajectory(trajA7);
        sleep(2000);
        drive.moveTo("Away");


//        drive.moveTo("Away");
//        sleep(2000);

//        wobbleDropper.setPosition(1);
//        drive.followTrajectory(
//                drive.trajectoryBuilder(traj.end(), true)
//                        .splineTo(new Vector2d(0, 0), Math.toRadians(180))
//                        .build()
//        );
    }
}