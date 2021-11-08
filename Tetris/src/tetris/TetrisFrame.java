package tetris;

import java.awt.BorderLayout;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

public class TetrisFrame extends JFrame {
  Tetris tetris;
  SettingsDialog dialog;
  Thread tetrisMain;

  public TetrisFrame(Tetris tetris) {
    super();
    this.tetris = tetris;
    tetrisMain = new Thread(tetris);
    tetrisMain.start();
    dialog = new SettingsDialog(this, tetris);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("Tetris");
    setResizable(false);
    add(tetris, BorderLayout.CENTER);

    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Menu");
    JMenuItem settings = new JMenuItem("Settings");
    JMenuItem restart = new JMenuItem("Restart");
    KeyStroke r = KeyStroke.getKeyStroke(KeyEvent.VK_R, 0);
    restart.setAccelerator(r);

    menu.add(settings);
    menu.add(restart);
    menuBar.add(menu);
    setJMenuBar(menuBar);
    settings.addActionListener(l -> dialog.setVisible(true));
    restart.addActionListener(l -> tetris.startNewGame());
    pack();
    setLocationRelativeTo(null);
  }

}
