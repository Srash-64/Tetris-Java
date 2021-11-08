package tetris;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.studiohartman.jamepad.ControllerManager;
import com.studiohartman.jamepad.ControllerState;

public class GamepadInput {
    private final ControllerManager controllers;

    public GamepadInput() {
        controllers = new ControllerManager();
        controllers.initSDLGamepad();
    }

    Set<InputAction> actions() {
        ControllerState currState = controllers.getState(0);
        if (!currState.isConnected) {
            return Collections.emptySet();
        }

        Set<InputAction> actions = new HashSet<>();
        if (currState.dpadLeft) {
            actions.add(InputAction.MOVE_LEFT);
        }
        if (currState.dpadRight) {
            actions.add(InputAction.MOVE_RIGHT);
        }
        if (currState.dpadUp) {
            actions.add(InputAction.MOVE_UP);
        }
        if (currState.dpadDown) {
            actions.add(InputAction.MOVE_DOWN);
        }
        if (currState.x) {
            actions.add(InputAction.ROTATE);
        }
        if (currState.a) {
            actions.add(InputAction.ROTATE_A);
        }
        return actions;
    }
}
