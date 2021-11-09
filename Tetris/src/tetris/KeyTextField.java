package tetris;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextField;

import net.java.games.input.Component.Identifier;

public class KeyTextField extends JTextField {
  private Identifier id;

 

public KeyTextField(String text, Identifier id) {
    super(text);
    this.id = id;
  }


public Identifier getId() {
	return id;
}

public void setId(Identifier id) {
	this.id = id;
}
}
