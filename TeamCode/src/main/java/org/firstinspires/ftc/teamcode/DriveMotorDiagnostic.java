package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Drive Motor Diagnostic")
public class DriveMotorDiagnostic extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        DcMotor frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        DcMotor backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");

        // Use the same directions as the current robot code.
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);

        frontLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backLeftMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        backRightMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        telemetry.addLine("DRIVE MOTOR DIAGNOSTIC");
        telemetry.addLine("");
        telemetry.addLine("Robot OFF the ground.");
        telemetry.addLine("");
        telemetry.addLine("A = Front Left");
        telemetry.addLine("B = Back Left");
        telemetry.addLine("X = Front Right");
        telemetry.addLine("Y = Back Right");
        telemetry.addLine("");
        telemetry.addLine("Right Stick = Rotation Test");
        telemetry.addLine("Left Stick = Translation Test");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {

            // -----------------------------------------------------
            // Individual motor tests
            // -----------------------------------------------------

            if (gamepad1.a) {
                frontLeftMotor.setPower(0.5);
            } else {
                frontLeftMotor.setPower(0);
            }

            if (gamepad1.b) {
                backLeftMotor.setPower(0.5);
            } else {
                backLeftMotor.setPower(0);
            }

            if (gamepad1.x) {
                frontRightMotor.setPower(0.5);
            } else {
                frontRightMotor.setPower(0);
            }

            if (gamepad1.y) {
                backRightMotor.setPower(0.5);
            } else {
                backRightMotor.setPower(0);
            }

            // -----------------------------------------------------
            // Rotation test
            // Right stick X overrides individual motor testing.
            // -----------------------------------------------------

            double rotation = gamepad1.right_stick_x;

            if (Math.abs(rotation) > 0.1) {
                frontLeftMotor.setPower(rotation);
                backLeftMotor.setPower(rotation);
                frontRightMotor.setPower(-rotation);
                backRightMotor.setPower(-rotation);
            }

            // -----------------------------------------------------
            // Translation test
            // Left stick overrides individual motor testing.
            // -----------------------------------------------------

            double y = -gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;

            if (Math.abs(y) > 0.1 || Math.abs(x) > 0.1) {
                double frontLeftPower = y + x;
                double backLeftPower = y - x;
                double frontRightPower = y - x;
                double backRightPower = y + x;

                double denominator = Math.max(
                        Math.abs(y) + Math.abs(x),
                        1.0
                );

                frontLeftMotor.setPower(frontLeftPower / denominator);
                backLeftMotor.setPower(backLeftPower / denominator);
                frontRightMotor.setPower((frontRightPower / denominator)*0.9);
                backRightMotor.setPower((backRightPower / denominator)*0.9);
            }

            // -----------------------------------------------------
            // Telemetry
            // -----------------------------------------------------

            telemetry.addLine("DRIVE MOTOR DIAGNOSTIC");
            telemetry.addLine("");
            telemetry.addData("A", "Front Left: %s", gamepad1.a ? "ON" : "OFF");
            telemetry.addData("B", "Back Left: %s", gamepad1.b ? "ON" : "OFF");
            telemetry.addData("X", "Front Right: %s", gamepad1.x ? "ON" : "OFF");
            telemetry.addData("Y", "Back Right: %s", gamepad1.y ? "ON" : "OFF");
            telemetry.addLine("");
            telemetry.addData("Left X", "%.2f", gamepad1.left_stick_x);
            telemetry.addData("Left Y", "%.2f", gamepad1.left_stick_y);
            telemetry.addData("Right X", "%.2f", gamepad1.right_stick_x);
            telemetry.update();
        }
    }
}