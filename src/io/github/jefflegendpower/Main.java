package io.github.jefflegendpower;

// TODO
// REFACTOR AND MAKE RAEDABLE

import com.j4ev3.core.Brick;
import com.j4ev3.core.LED;
import com.j4ev3.desktop.BluetoothComm;
import io.github.jefflegendpower.ev3.MotorControl;
import net.java.games.input.*;
import sun.security.util.ArrayUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    static {
        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "io/github/jefflegendpower/natives/";
        System.setProperty("net.java.games.input.librarypath", new File(path).getAbsolutePath());
    }

    public static void main(String[] args) {
        // write your code here
        String mac_id1 = "0016535F1F1F";
        String mac_id2 = "insert mac id here";
        long startTime = System.nanoTime();

        List<Brick> bricks = new ArrayList<Brick>();
        List<Controller> controllers = Arrays.asList(ControllerEnvironment.getDefaultEnvironment().getControllers());

        bricks.add(new Brick(new BluetoothComm(mac_id1)));
//        bricks.add(new Brick(new BluetoothComm(mac_id2)));
        for (Brick brick : bricks) {
            brick.getLED().setPattern(LED.LED_RED);

            for (Controller controller : controllers) {
                if (controller.getType() == Controller.Type.STICK) {

                    Thread thread = new Thread(new MotorControl(brick, controller));
                    thread.start();
                    controllers.remove(controller);
                }
            }
        }
        while (true) {
            System.out.println(System.nanoTime() - startTime);
            System.out.println(30000000000L);
            if ((System.nanoTime() - startTime) == 30000000000L) {
                for (Brick brick : bricks)
                    brick.disconnect();
                System.exit(0);
            }
        }


    }
}

