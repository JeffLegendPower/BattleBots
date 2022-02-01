package io.github.jefflegendpower.ev3;

import com.j4ev3.core.Brick;
import com.j4ev3.core.Motor;
import com.sun.istack.internal.NotNull;
import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

public class MotorControl implements Runnable {

    private Brick brick;
    private Controller controller;

public MotorControl(@NotNull Brick brick, Controller controller) {
        this.brick = brick;
        this.controller = controller;
    }
    public void run() {
        Event event = new Event();
        EventQueue queue = controller.getEventQueue();
        boolean stopped = false;
        Action lastAction = Action.STOP;
        Action lastActionA = Action.STOP;
        Action lastActionD = Action.STOP;
        Motor motor = brick.getMotor();
        byte drivingPorts = (byte) (Motor.PORT_B + Motor.PORT_C);
        byte power = 50;

        while (!stopped) {
            controller.poll();
            queue.getNextEvent(event);

            @NotNull Component component = event.getComponent();
            Component.Identifier identifier = component.getIdentifier();
            float data = component.getPollData();

            if (data == 1.0) {
                if (identifier == Component.Identifier.Axis.Y && lastAction != Action.BACKWARD) {
                    motor.turnAtPower(drivingPorts, -power);
                    lastAction = Action.BACKWARD;
                }
                if (identifier == Component.Identifier.Axis.X && lastAction != Action.TURN_RIGHT) {
                    motor.turnAtPower(Motor.PORT_C, power);
                    motor.turnAtPower(Motor.PORT_B, -power);
                    lastAction = Action.TURN_RIGHT;
                }

                // For ports A and D
                if (identifier == Component.Identifier.Button._4 && lastActionA != Action.FORWARD) {
                    motor.turnAtPower(Motor.PORT_A, power);
                    lastActionA = Action.FORWARD;
                } else if (identifier == Component.Identifier.Button._6 && lastActionA != Action.BACKWARD) {
                    motor.turnAtPower(Motor.PORT_A, -power);
                    lastActionA = Action.BACKWARD;
                }

                if (identifier == Component.Identifier.Button._5 && lastActionD != Action.FORWARD) {
                    motor.turnAtPower(Motor.PORT_D, power);
                    lastActionD = Action.FORWARD;
                } else if (identifier == Component.Identifier.Button._7 && lastActionD != Action.BACKWARD) {
                    motor.turnAtPower(Motor.PORT_D, -power);
                    lastActionD = Action.BACKWARD;
                }

                // Power control
                if (identifier == Component.Identifier.Button._0) power = 25;
                else if (identifier == Component.Identifier.Button._1) power = 50;
                else if (identifier == Component.Identifier.Button._2) power = 75;
                else if (identifier == Component.Identifier.Button._3) power = 100;

            } else if (data == -1.0) {
                if (identifier == Component.Identifier.Axis.Y && lastAction != Action.FORWARD) {
                    motor.turnAtPower(drivingPorts, power);
                    lastAction = Action.FORWARD;
                }
                if (identifier == Component.Identifier.Axis.X && lastAction != Action.TURN_LEFT) {
                    motor.turnAtPower(Motor.PORT_B, power);
                    motor.turnAtPower(Motor.PORT_C, -power);
                    lastAction = Action.TURN_LEFT;
                }
            }
            else if (lastAction != Action.STOP) {
                motor.stopMotor(drivingPorts, true);
                lastAction = Action.STOP;
            } else if (lastActionA != Action.STOP) {
                motor.stopMotor(Motor.PORT_A, true);
                lastActionA = Action.STOP;
            } else if (lastActionD != Action.STOP) {
                motor.stopMotor(Motor.PORT_D, true);
                lastActionD = Action.STOP;
            }
        }
    }

    private enum Action {
        FORWARD,
        BACKWARD,
        STOP,
        TURN_LEFT,
        TURN_RIGHT;
    }
}