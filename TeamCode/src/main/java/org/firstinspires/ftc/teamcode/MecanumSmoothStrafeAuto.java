package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous(name="MecanumSmoothStrafeAuto")
public class MecanumSmoothStrafeAuto extends LinearOpMode {
    private DcMotor frontLeftMotor, frontRightMotor, backLeftMotor, backRightMotor;
    private ElapsedTime runtime = new ElapsedTime();

    @Override
    public void runOpMode() {
        frontLeftMotor = hardwareMap.dcMotor.get("frontLeftMotor");
        backLeftMotor = hardwareMap.dcMotor.get("backLeftMotor");
        frontRightMotor = hardwareMap.dcMotor.get("frontRightMotor");
        backRightMotor = hardwareMap.dcMotor.get("backRightMotor");
        
        frontRightMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backRightMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        frontLeftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftMotor.setDirection(DcMotorSimple.Direction.FORWARD);

        waitForStart();

        // --- STEP 1: STRAFE RIGHT FOR 3.5 SECONDS ---
        runtime.reset();
        setPower(1.0, -1.0, -1.0, 1.0);
        while (opModeIsActive() && runtime.seconds() < 3.5) {
            idle(); // Safely yields the thread to keep the robot alive
        }

        // --- STEP 2: PAUSE FOR 0.3 SECONDS ---
        runtime.reset();
        setPower(0, 0, 0, 0);
        while (opModeIsActive() && runtime.seconds() < 0.3) {
            idle(); 
        }

        // --- STEP 3: STRAFE LEFT FOR 3.5 SECONDS ---
        runtime.reset();
        setPower(-1.0, 1.0, 1.0, -1.0);
        while (opModeIsActive() && runtime.seconds() < 3.5) {
            idle();
        }

        // --- FINAL STEP: STOP ---
        setPower(0, 0, 0, 0);
    }

    private void setPower(double lf, double rf, double lb, double rb) {
        frontLeftMotor.setPower(lf);   
        frontRightMotor.setPower(rf);
        backLeftMotor.setPower(lb);    
        backRightMotor.setPower(rb);
    }
}
