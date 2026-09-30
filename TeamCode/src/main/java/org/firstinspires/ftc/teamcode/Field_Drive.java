package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;


@TeleOp(name = "Field Drive - Pinpoint")
public class Field_Drive extends LinearOpMode {

    private static final double TICKS_PER_REV = 28.0;

    @Override
    public void runOpMode() throws InterruptedException {
        // Drive motors
        DcMotor frontLeftMotor = hardwareMap.get(DcMotor.class, "frontLeftMotor");
        DcMotor backLeftMotor = hardwareMap.get(DcMotor.class, "backLeftMotor");
        DcMotor frontRightMotor = hardwareMap.get(DcMotor.class, "frontRightMotor");
        DcMotor backRightMotor = hardwareMap.get(DcMotor.class, "backRightMotor");

        // Other motors and servos
        DcMotor intake = hardwareMap.get(DcMotor.class, "intake");
        DcMotorEx flyWheel = hardwareMap.get(DcMotorEx.class, "FlyWheel");
        Servo blocker = hardwareMap.get(Servo.class, "blocker");

        // Pinpoint
        GoBildaPinpointDriver pinpoint = hardwareMap.get(GoBildaPinpointDriver.class, "pinpoint");
        pinpoint.resetPosAndIMU();

        // Motor directions
        frontLeftMotor.setDirection(DcMotor.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotor.Direction.FORWARD);
        frontRightMotor.setDirection(DcMotor.Direction.REVERSE);
        backRightMotor.setDirection(DcMotor.Direction.FORWARD);

        intake.setDirection(DcMotor.Direction.FORWARD);
        flyWheel.setDirection(DcMotorEx.Direction.FORWARD);
        blocker.setDirection(Servo.Direction.REVERSE);

        // Motor modes
        frontLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeftMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRightMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        intake.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        flyWheel.setMode(DcMotorEx.RunMode.RUN_USING_ENCODER);

        blocker.setPosition(0.35);

        telemetry.addLine("Pinpoint Field Drive Ready");
        telemetry.update();

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            pinpoint.update();

            // Driver controls
            double y = -gamepad1.left_stick_y * 0.5;
            double x = gamepad1.left_stick_x * 0.5;
            double rx = gamepad1.right_stick_x * 0.5;

            // Reset field heading
            if (gamepad1.y) {
                pinpoint.resetPosAndIMU();
            }

            // Pinpoint heading
            double botHeading = pinpoint.getHeading(AngleUnit.RADIANS);

            // Field-centric transformation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            // Strafing compensation
            rotX *= 1.1;

            // Mecanum motor powers
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1.0);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            frontLeftMotor.setPower(frontLeftPower);
            backLeftMotor.setPower(backLeftPower);
            frontRightMotor.setPower(frontRightPower);
            backRightMotor.setPower(backRightPower);

            // Intake
            if (gamepad2.left_bumper || gamepad1.left_bumper) {
                intake.setPower(0.5);
            } else if (gamepad2.left_trigger > 0.1 || gamepad1.left_trigger > 0.1) {
                intake.setPower(-0.5);
            } else {
                intake.setPower(0);
            }

            // Flywheel
            double rpm = 5000.0;
            if (gamepad2.right_bumper && gamepad2.b) {
                flyWheel.setVelocity(getTickSpeed(rpm + 500));
            } else if (gamepad2.right_bumper || gamepad1.right_bumper) {
                flyWheel.setVelocity(getTickSpeed(rpm));
            } else if (gamepad2.left_trigger > 0.1 || gamepad1.left_trigger > 0.1) {
                flyWheel.setVelocity(getTickSpeed(-rpm));
            } else {
                flyWheel.setVelocity(0);
            }

            // Blocker
            if (gamepad2.a || gamepad1.right_bumper) {
                blocker.setPosition(0.075);
            } else {
                blocker.setPosition(0.35);
            }

            // Telemetry
            telemetry.addData("Heading", "%.2f degrees", pinpoint.getHeading(AngleUnit.DEGREES));
            telemetry.addData("Pinpoint X", "%.2f", pinpoint.getPosX(DistanceUnit.INCH));
            telemetry.addData("Pinpoint Y", "%.2f", pinpoint.getPosY(DistanceUnit.INCH));
            telemetry.addData("FL Power", "%.2f", frontLeftPower);
            telemetry.addData("BL Power", "%.2f", backLeftPower);
            telemetry.addData("FR Power", "%.2f", frontRightPower);
            telemetry.addData("BR Power", "%.2f", backRightPower);
            telemetry.update();
        }
    }

    public double getTickSpeed(double rpm) {
        return rpm * TICKS_PER_REV / 60.0;
    }
}