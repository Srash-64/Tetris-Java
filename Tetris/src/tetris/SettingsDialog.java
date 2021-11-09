package tetris;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import net.java.games.input.Component.Identifier;

public class SettingsDialog extends JDialog {

	private KeyTextField upTf = new KeyTextField("", null);
	private KeyTextField downTf  = new KeyTextField("", null);
	private KeyTextField leftTf  = new KeyTextField("", null);
	private KeyTextField rightTf  = new KeyTextField("", null);
	private KeyTextField rotateTf  = new KeyTextField("", null);
	private KeyTextField rotateTfGamePad  = new KeyTextField("", null);
	private KeyTextField rotateATf  = new KeyTextField("", null);
	private KeyTextField rotateATfGamePad  = new KeyTextField("", null);
	
  public SettingsDialog(Window owner, Tetris tetris) {
    new JDialog(owner);

    setTitle("Settings");
    setLayout(new GridBagLayout());

    int row = 0;

    JSpinner spinnerLevel = new JSpinner(new SpinnerNumberModel(tetris.getScoreBoard().getFirstLevel(), 0, 29, 1));

    add(new JLabel("Level start : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(spinnerLevel, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    manageAddButton("Up", row, upTf, UsedKeys.UP, tetris, false, null);
    row++;
    manageAddButton("Down", row, downTf, UsedKeys.DOWN, tetris, false, null);
    row++;
    manageAddButton("Left", row, leftTf, UsedKeys.LEFT, tetris, false, null);
    row++;
    manageAddButton("Right", row, rightTf, UsedKeys.RIGHT, tetris, false, null);
    row++;
    manageAddButton("Rotate", row, rotateTf, UsedKeys.ROTATE, tetris, true, rotateTfGamePad);
    row++;
    manageAddButton("Rotate (anti)", row, rotateATf, UsedKeys.ROTATE_A, tetris, true, rotateATfGamePad);
    row++;
  
    JCheckBox classicColorCB = new JCheckBox();
    classicColorCB.setSelected(tetris.isClassicColor());

    add(new JLabel("Classic Color : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(classicColorCB, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    JCheckBox upActiveCB = new JCheckBox();
    upActiveCB.setSelected(tetris.isUpActive());

    add(new JLabel("Instant down active : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(upActiveCB, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    JButton applyButton = new JButton("apply");
    JButton cancelButton = new JButton("cancel");

    applyButton.addActionListener(l -> {
      tetris.getScoreBoard().setFirstLevel((int) spinnerLevel.getValue());
      tetris.identifierToKeyDeviceMap.put(upTf.getId(), new KeyDevice(UsedKeys.UP, DeviceType.KEYBOARD));
      tetris.identifierToKeyDeviceMap.put(downTf.getId(), new KeyDevice(UsedKeys.DOWN, DeviceType.KEYBOARD));
      tetris.identifierToKeyDeviceMap.put(leftTf.getId(), new KeyDevice(UsedKeys.LEFT, DeviceType.KEYBOARD));
      tetris.identifierToKeyDeviceMap.put(rightTf.getId(), new KeyDevice(UsedKeys.RIGHT, DeviceType.KEYBOARD));
      tetris.identifierToKeyDeviceMap.put(rotateTf.getId(), new KeyDevice(UsedKeys.ROTATE, DeviceType.KEYBOARD));
      tetris.identifierToKeyDeviceMap.put(rotateATf.getId(), new KeyDevice(UsedKeys.ROTATE_A, DeviceType.KEYBOARD));
      
      tetris.identifierToKeyDeviceMap.put(rotateTfGamePad.getId(), new KeyDevice(UsedKeys.ROTATE, DeviceType.GAMEPAD));
      tetris.identifierToKeyDeviceMap.put(rotateATfGamePad.getId(), new KeyDevice(UsedKeys.ROTATE_A, DeviceType.GAMEPAD));
      
      tetris.setClassicColor(classicColorCB.isSelected());
      tetris.setUpActive(upActiveCB.isSelected());
      setVisible(false);
    });
    cancelButton.addActionListener(l -> setVisible(false));

    add(applyButton, new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(cancelButton, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    pack();
    setMinimumSize(getSize());
  }
  
  private void manageAddButton(String label, int row, KeyTextField tf,  UsedKeys uk, Tetris tetris, boolean controllerVal, KeyTextField gamePadTf) {
	  
	    Identifier idCode = tetris.getIdentifierToKeyDeviceMap(uk, DeviceType.KEYBOARD).orElse(null);
	    String text = idCode == null ? "" : idCode.toString();  
	    tf.setText(text);
	    tf.setId(idCode);

	    JButton buttonModify = new JButton("Modify");    
	    buttonModify.addActionListener(l -> manageNewInput(tf, Controller.Type.KEYBOARD));
	    
	    add(new JLabel(label + " : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
	    add(tf, new GridBagConstraints(1, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));
	    add(buttonModify, new GridBagConstraints(2, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));
	    
	    if(controllerVal) {
	        Identifier idControllerCode = tetris.getIdentifierToKeyDeviceMap(uk, DeviceType.GAMEPAD).orElse(null);
		    String controllerText = idControllerCode == null ? "" : idControllerCode.toString();    
		    gamePadTf.setText("Button " + controllerText);
		    gamePadTf.setId(idControllerCode);

		    JButton controllerButtonModify = new JButton("Modify");    
		    controllerButtonModify.addActionListener(l -> manageNewInput(gamePadTf, Controller.Type.GAMEPAD));
	    	
		    add(gamePadTf, new GridBagConstraints(3, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));
		    add(controllerButtonModify, new GridBagConstraints(4, row, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));	   
	    }
  }
  
  private void manageNewInput(KeyTextField tf, Controller.Type type) {
  	JDialog dialg = new JDialog();

  	dialg.setVisible(true);
  	
  	/* Get the available controllers */
		Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
		
		if (controllers.length == 0) {
			System.out.println("Found no controllers.");
			System.exit(0);
		}

		boolean newEntry = false;
		boolean escape = false;
		while(!newEntry && !escape){
			for (int i = 0; i < controllers.length; i++) {
				/* Remember to poll each one */
				controllers[i].poll();
				
				/* Get the controllers event queue */
				EventQueue queue = controllers[i].getEventQueue();

				/* Create an event object for the underlying plugin to populate */
				Event event = new Event();

				/* For each object in the queue */
				while (queue.getNextEvent(event)) {
					Component comp = event.getComponent();
					Identifier id = comp.getIdentifier();
					
					Controller.Type t =controllers[i].getType();
					
	
					
					if(id == Identifier.Key.ESCAPE) escape = true;
					else{
						
						
						if(t.equals(type)) {
							if(t.equals(Controller.Type.GAMEPAD)) {
								if(id instanceof Identifier.Button) {
	
									tf.setText(id.toString());
									tf.setId(id);
									newEntry = true;
								}
							}												
						}						
					}		
				}		
			}
		}
		
		
		dialg.setVisible(false);
		
		
  	
  }
  
}
