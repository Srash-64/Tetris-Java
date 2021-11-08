package tetris;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class SettingsDialog extends JDialog {

  public SettingsDialog(Window owner, Tetris tetris) {
    new JDialog(owner);

    setTitle("Settings");
    setLayout(new GridBagLayout());

    int row = 0;

    JSpinner spinnerLevel = new JSpinner(new SpinnerNumberModel(tetris.getScoreBoard().getFirstLevel(), 0, 29, 1));

    add(new JLabel("Level start : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(spinnerLevel, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    Integer upCode = tetris.getMoveToKeyCodeMap().get(UsedKeys.UP);
    KeyTextField upTF = new KeyTextField(KeyEvent.getKeyText(upCode), upCode);

    add(new JLabel("Up : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(upTF, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    Integer downCode = tetris.getMoveToKeyCodeMap().get(UsedKeys.DOWN);
    KeyTextField downTF = new KeyTextField(KeyEvent.getKeyText(downCode), downCode);

    add(new JLabel("Down : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(downTF, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    Integer leftCode = tetris.getMoveToKeyCodeMap().get(UsedKeys.LEFT);
    KeyTextField leftTF = new KeyTextField(KeyEvent.getKeyText(leftCode), leftCode);

    add(new JLabel("Left : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(leftTF, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    Integer rightCode = tetris.getMoveToKeyCodeMap().get(UsedKeys.RIGHT);
    KeyTextField rightTF = new KeyTextField(KeyEvent.getKeyText(rightCode), rightCode);

    add(new JLabel("Right : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(rightTF, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    Integer rotateCode = tetris.getMoveToKeyCodeMap().get(UsedKeys.ROTATE);
    KeyTextField rotateTF = new KeyTextField(KeyEvent.getKeyText(rotateCode), rotateCode);

    add(new JLabel("Rotate : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(rotateTF, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

    Integer rotateACode = tetris.getMoveToKeyCodeMap().get(UsedKeys.ROTATE_A);
    KeyTextField rotateATF = new KeyTextField(KeyEvent.getKeyText(rotateACode), rotateACode);

    add(new JLabel("Rotate (anti) : "), new GridBagConstraints(0, row, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15, 15, 5, 15), 0, 0));
    add(rotateATF, new GridBagConstraints(1, row++, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(15, 15, 5, 15), 0, 0));

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
      tetris.moveToKeyCodeMap.put(UsedKeys.UP, upTF.getKeyCode());
      tetris.moveToKeyCodeMap.put(UsedKeys.DOWN, downTF.getKeyCode());
      tetris.moveToKeyCodeMap.put(UsedKeys.LEFT, leftTF.getKeyCode());
      tetris.moveToKeyCodeMap.put(UsedKeys.RIGHT, rightTF.getKeyCode());
      tetris.moveToKeyCodeMap.put(UsedKeys.ROTATE, rotateTF.getKeyCode());
      tetris.moveToKeyCodeMap.put(UsedKeys.ROTATE_A, rotateATF.getKeyCode());
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
}
