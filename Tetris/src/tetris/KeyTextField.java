package tetris;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

public class KeyTextField extends JTextField {
  private int keyCode;

  public KeyTextField(String text, int keyCode) {
    super(text);
    this.keyCode = keyCode;

    addKeyListener(new KeyAdapter() {
      @Override
      public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        setText(KeyEvent.getKeyText(key));
        setKeyCode(key);
      }
    });
  }

  public int getKeyCode() {
    return keyCode;
  }

  public void setKeyCode(int keyCode) {
    this.keyCode = keyCode;
  }

}
