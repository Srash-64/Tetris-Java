package tetris;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;
import tetris.Scoreboard;
import tetris.Shape;
import tetris.Tetris.SuperTimer;
import tetris.UsedKeys;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line.Info;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Port;
public class Tetris extends JPanel implements Runnable {

  private final static Color[] colors = { Color.RED, Color.GREEN, Color.CYAN,
      new Color(182, 39, 246), Color.YELLOW, new Color(255, 127, 0), new Color(0, 64, 255) };

  private final static Font mainFont = new Font("Monospaced", Font.BOLD, 48);
  private final static Font smallFont = mainFont.deriveFont(Font.BOLD, 18);

  private final static Dimension dim = new Dimension(640, 800);

  private final static Rectangle gridRect = new Rectangle(46, 47, 300, 600);
  private final static Rectangle previewRect = new Rectangle(387, 47, 200, 200);
  private final static Rectangle titleRect = new Rectangle(100, 85, 252, 100);
  private final static Rectangle clickRect = new Rectangle(50, 375, 252, 40);

  private final static int blockSize = 30;
  private final static int nRows = 21;
  private final static int nCols = 12;
  private final static int topMargin = 50;
  private final static int leftMargin = 20;
  private final static int scoreX = 400;
  private final static int scoreY = 330;
  private final static int titleX = 130;
  private final static int titleY = 150;
  private final static int clickX = 120;
  private final static int clickY = 400;
  private final static int previewCenterX = 467;
  private final static int previewCenterY = 97;

  private final static int Z_SHAPE_IDX = 0;
  private final static int S_SHAPE_IDX = 1;
  private final static int I_SHAPE_IDX = 2;
  private final static int T_SHAPE_IDX = 3;
  private final static int SQUARE_SHAPE_IDX = 4;
  private final static int L_SHAPE_IDX = 5;
  private final static int J_SHAPE_IDX = 6;

  private final static Stroke largeStroke = new BasicStroke(6, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);
  private final static Stroke smallStroke = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

  private final static Color squareBorder = Color.white;
  private final static Color titlebgColor = Color.GRAY;
  private final static Color textColor = Color.white;
  private final static Color bgColor = new Color(0xDDEEFF);
  private final static Color gridColor = Color.black;
  private final static Color WhiteNes = new Color(0xfcfcfc);

  private final static Color gridBorderColor = new Color(0x7788AA);
  
  private final static   List<UsedKeys> ukToCheck = new ArrayList<>(Arrays.asList( UsedKeys.LEFT, UsedKeys.DOWN,  UsedKeys.RIGHT,  UsedKeys.UP));	  

  private final Map<Integer, Color> levelToColor1 = new HashMap<>();
  private final Map<Integer, Color> levelToColor2 = new HashMap<>();

  private final Map<UsedKeys, SuperTimer> keyTimers = new EnumMap<>(UsedKeys.class);
  private final Map<UsedKeys, Boolean> keyState = new EnumMap<>(UsedKeys.class);

  public final Map<Identifier, KeyDevice> identifierToKeyDeviceMap = new HashMap<>();
  
private long currentMilli = System.currentTimeMillis();

  private boolean classicColor = true;
  private boolean upActive = true;

  private boolean instantDrop = false;
  private boolean removingLine = false;
  
  private boolean highStack = false;

  List<Integer> linesToRemove = new ArrayList<>();

  private boolean drawFallingShapeControl = true;
  private int addARE = 0;

  private SuperTimer masterTimer = null;
  private SuperTimer testRemove = null;
  
  private Float masterVolume = 0.5f;

  

ReentrantLock lock = new ReentrantLock();
  ReentrantLock lockRepaint = new ReentrantLock();
  ReentrantLock lockGrid = new ReentrantLock();
  
  private MusicPlayer leftRightFX = new MusicPlayer("change-letter.mp3");
	private MusicPlayer landFX = new MusicPlayer("Land.mp3");
	private MusicPlayer lineRemoveFX = new MusicPlayer("Line-delete.mp3");
	private MusicPlayer tetrisFX = new MusicPlayer("Tetris.mp3");
	private MusicPlayer gameOverFX = new MusicPlayer("gameover.mp3");
	
	private MusicPlayer musicFX = new MusicPlayer("1 - Music 1.mp3");
	private MusicPlayer musicFastFX = new MusicPlayer("8 - Track 8.mp3");

  enum Dir {
    right(1, 0),
    down(0, 1),
    left(-1, 0);

    Dir(int x, int y) {
      this.x = x;
      this.y = y;
    }

    final int x, y;
  }

  public static final int EMPTY = -1;
  public static final int BORDER = -2;

  Shape fallingShape;
  Shape nextShape;

  // position of falling shape
  int fallingShapeRow;
  int fallingShapeCol;

  final int[][] grid = new int[nRows][nCols];

  Thread fallingThread;
  final Scoreboard scoreboard = new Scoreboard();
  static final Random rand = new Random();

  public Tetris() {
	 
	  setMasterVolume();
	  
	  initKeyBind();
    initColorMap();

    setPreferredSize(dim);
    setBackground(bgColor);
    setFocusable(true);
    setDoubleBuffered(true);
    initGrid();
    selectShape();

    
    
    

    
    
    
    
    
    addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        if (scoreboard.isGameOver()) {
          startNewGame();
        }
      }
    });

    
    keyState.put(UsedKeys.LEFT, false);
    keyState.put(UsedKeys.RIGHT, false);
    keyState.put(UsedKeys.DOWN, false);
  }
  
  private void checkReleaseUsedKeys(UsedKeys uk) {
	  releaseUsedKeys(uk);	
    }
  

  private void  releaseUsedKeys(UsedKeys uk) {
	  SuperTimer currentTimer = keyTimers.get(uk);
		
	    if (currentTimer != null) {
	      currentTimer.destroyTimer();
	
	    }
	
	    if (keyState.get(uk) != null) {
	      keyState.put(uk, false);
	    }	
  }
  
  
  public void managePressedKeys( UsedKeys usedKeys) {
	  long initialDelay = 250L;
	    long delay = 75L;
	  
	  boolean alreadyPressed = keyState.getOrDefault(usedKeys, false);
	  
	  
	  
	   SuperTimer myTimer = null;
      switch (usedKeys) {

        case ROTATE:
          if (instantDrop) return;
          if (!alreadyPressed) {
            keyState.put(usedKeys, true);
            lock.lock();
            try {
              rotate();
            }
            finally {
              lock.unlock();
            }
          }
          break;

        case ROTATE_A:
          if (instantDrop) return;
          if (!alreadyPressed) {
            keyState.put(usedKeys, true);
            lock.lock();
            try {
              rotateAnti();
            }
            finally {
              lock.unlock();
            }
          }
          break;

        case LEFT:
          if (instantDrop) return;
          if (!alreadyPressed) {        	  
            myTimer = new SuperTimer(l -> moveLeft(), initialDelay, delay);
            myTimer.setRepeat(true);
            myTimer.start();

            keyState.put(usedKeys, true);
            moveLeft();

            keyTimers.put(usedKeys, myTimer);
          }
          break;

        case RIGHT:
          if (instantDrop) return;
          if (!alreadyPressed) {
            myTimer = new SuperTimer(l -> moveRight(), initialDelay, delay);
            myTimer.setRepeat(true);
            myTimer.start();

            keyState.put(usedKeys, true);
            moveRight();

            keyTimers.put(usedKeys, myTimer);
          }
          break;

        case DOWN:
          if (instantDrop) return;
          if (!alreadyPressed) {
            myTimer = new SuperTimer(l -> moveDown(), initialDelay, delay);
            myTimer.setRepeat(true);
            myTimer.start();

            keyState.put(usedKeys, true);
            moveDown();

            keyTimers.put(usedKeys, myTimer);
          }
          break;

        case UP:
          if (instantDrop) return;
          if (!alreadyPressed) {
            keyState.put(usedKeys, true);

            if (!upActive || fallingShape.isNew()) break;

            int nbMove = nbMove(fallingShape, Dir.down);

            lock.lock();
            try {
              fallingShapeRow += Dir.down.y * nbMove;
              fallingShapeCol += Dir.down.x * nbMove;
            }
            finally {
              lock.unlock();
            }

            instantDrop = true;
            scoreboard.addScore(nbMove);
            
            masterTimer.setDelay((int) computeARE(fallingShapeRow));
            shapeHasLanded();  

          }
      }
  }
  

  private void initColorMap() {
    levelToColor1.put(0, new Color(60, 188, 252));
    levelToColor2.put(0, new Color(0, 88, 248));

    levelToColor1.put(1, new Color(184, 248, 24));
    levelToColor2.put(1, new Color(0, 168, 0));

    levelToColor1.put(2, new Color(248, 120, 248));
    levelToColor2.put(2, new Color(216, 0, 204));

    levelToColor1.put(3, new Color(88, 216, 84));
    levelToColor2.put(3, new Color(0, 88, 248));

    levelToColor1.put(4, new Color(88, 248, 152));
    levelToColor2.put(4, new Color(228, 0, 88));

    levelToColor1.put(5, new Color(104, 132, 252));
    levelToColor2.put(5, new Color(88, 248, 152));

    levelToColor1.put(6, new Color(124, 124, 124));
    levelToColor2.put(6, new Color(248, 56, 0));

    levelToColor1.put(7, new Color(168, 0, 32));
    levelToColor2.put(7, new Color(104, 68, 252));

    levelToColor1.put(8, new Color(248, 56, 0));
    levelToColor2.put(8, new Color(0, 88, 248));

    levelToColor1.put(9, new Color(252, 160, 68));
    levelToColor2.put(9, new Color(248, 56, 0));
  }

  public Scoreboard getScoreBoard() {
    return scoreboard;
  }

  private void initKeyBind() {
	  identifierToKeyDeviceMap.put(Identifier.Key.UP, new KeyDevice( UsedKeys.UP, DeviceType.KEYBOARD));
	  identifierToKeyDeviceMap.put(Identifier.Key.DOWN, new KeyDevice( UsedKeys.DOWN, DeviceType.KEYBOARD));
	  identifierToKeyDeviceMap.put(Identifier.Key.LEFT,  new KeyDevice( UsedKeys.LEFT, DeviceType.KEYBOARD));
	  identifierToKeyDeviceMap.put(Identifier.Key.RIGHT,  new KeyDevice( UsedKeys.RIGHT, DeviceType.KEYBOARD));
	  identifierToKeyDeviceMap.put(Identifier.Key.A,  new KeyDevice( UsedKeys.ROTATE, DeviceType.KEYBOARD));
	  identifierToKeyDeviceMap.put(Identifier.Key.Z,  new KeyDevice( UsedKeys.ROTATE_A, DeviceType.KEYBOARD));
	  
	  identifierToKeyDeviceMap.put(Identifier.Button._2, new KeyDevice( UsedKeys.ROTATE, DeviceType.GAMEPAD));
	  identifierToKeyDeviceMap.put(Identifier.Button._0,new KeyDevice( UsedKeys.ROTATE_A, DeviceType.GAMEPAD));
  }

  private Optional<KeyDevice> getKeyDeviceByIdentifier(Identifier id) {
	 return Optional.ofNullable(identifierToKeyDeviceMap.get(id));
  }

  private void rotate() {
    if (canRotate(fallingShape))
      rotate(fallingShape);
  }

  private void rotateAnti() {
    if (canRotateAnti(fallingShape))
      rotateAnti(fallingShape);
  }

  private void moveLeft() {
	  boolean cantMove = keyState.getOrDefault(UsedKeys.RIGHT, false) ||  keyState.getOrDefault(UsedKeys.DOWN, false);	  
	  if(cantMove) return;
	  
    if (canMove(fallingShape, Dir.left)) {
      lock.lock();
      try {
        move(Dir.left);
        
        leftRightFX.play();
       
      }
      finally {
        lock.unlock();
      }
    }
  }

  private void moveRight() {
	  boolean cantMove = keyState.getOrDefault(UsedKeys.LEFT, false) ||  keyState.getOrDefault(UsedKeys.DOWN, false);	  
	  if(cantMove) return;
	  
    if (canMove(fallingShape, Dir.right)) {
      lock.lock();
      try {
        move(Dir.right);
        
        leftRightFX.play();
              	
      }
      finally {
        lock.unlock();
      }

    }
  }

  private void moveDown() {
	  boolean cantMove = keyState.getOrDefault(UsedKeys.LEFT, false) ||  keyState.getOrDefault(UsedKeys.RIGHT, false);	  
	  if(cantMove) return;
	  
    if (canMove(fallingShape, Dir.down)) {
      {
        lock.lock();
        try {
          move(Dir.down);
        }
        finally {
          lock.unlock();
        }
      }
      scoreboard.addScore(1);

    }
  }

  void selectShape() {
    ShapeEnum[] shapes = ShapeEnum.values();

    if (nextShape == null) {
      nextShape = new Shape(shapes[rand.nextInt(shapes.length)]);
    }

    fallingShapeRow = 0;
    fallingShapeCol = 5;
    fallingShape = new Shape(nextShape.getShapeEnum());
    nextShape = new Shape(shapes[rand.nextInt(shapes.length)]);

    if (fallingShape != null) {
      try {
        lock.lock();
        fallingShape.reset();
      }
      finally {
        lock.unlock();
      }

    }

  }

  void startNewGame() {
	  musicFX.stop();	  
	  musicFastFX.stop();
	  musicFX.play();
    initGrid();
    selectShape();
    scoreboard.reset();

  }

  void initGrid() {
    for (int r = 0; r < nRows; r++) {
      Arrays.fill(grid[r], EMPTY);
      for (int c = 0; c < nCols; c++) {
        if (c == 0 || c == nCols - 1 || r == nRows - 1)
          grid[r][c] = BORDER;
      }
    }
  }

  private long recomputeDelay() {
    currentMilli = System.currentTimeMillis();

    long timeToWait = currentMilli + scoreboard.getSpeed();
    if (!scoreboard.isGameOver() && fallingShape.isNew()) {
      timeToWait += addARE;
    }

    long delay = 0;

    delay = timeToWait - System.currentTimeMillis();
    if (delay < 0) delay = 0;


   if (fallingShape.isNew() && delay < addARE) return addARE; 

    return delay;
  }

  @Override
  public void run() {

    long delay = recomputeDelay();

    
    
    masterTimer = new SuperTimer(l -> {
      addARE = 0;

      if (!scoreboard.isGameOver()) {

        if (removingLine) return;

        fallingShape.setNew(false);

        if (canMove(fallingShape, Dir.down)) {
          move(Dir.down);
        }
        else {
          shapeHasLanded();
        }

        long newdelay = recomputeDelay();
        masterTimer.setDelay((int) newdelay);
      }
    }, delay, delay);

    masterTimer.setRepeat(true);
    masterTimer.start();

  }

  void drawStartScreen(Graphics2D g) {
    g.setFont(mainFont);

    g.setColor(titlebgColor);
    g.fill(titleRect);
    g.fill(clickRect);

    g.setColor(textColor);
    g.drawString("Tetris", titleX, titleY);

    g.setFont(smallFont);
    g.drawString("click to start", clickX, clickY);
  }

  void drawSquare(Graphics2D g, int colorIndex, int r, int c) {
    g.setStroke(new BasicStroke(1));

    int topX = leftMargin - 4 + c * blockSize;
    int topY = topMargin - 3 + r * blockSize;

    if (!isClassicColor()) {
      g.setColor(colors[colorIndex]);

      g.fillRect(leftMargin - 4 + c * blockSize, topMargin - 3 + r * blockSize,
          blockSize, blockSize);

      g.setColor(gridColor);
      g.drawRect(leftMargin - 4 + c * blockSize, topMargin - 3 + r * blockSize,
          blockSize, blockSize);
    }
    else {
      ShapeEnum shapeType = getShapeByIndex(colorIndex);

      if (shapeType == null) return;

      Color color1 = levelToColor1.get(scoreboard.getCurrentLevel() % 10);
      Color color2 = levelToColor2.get(scoreboard.getCurrentLevel() % 10);

      int strokeSize = 3;
      int strokeDecal = 1;

      if (ShapeType.EMPTY.equals(shapeType.getType())) {
        g.setStroke((new BasicStroke(1)));
        g.setColor(WhiteNes);

        g.fillRect(topX, topY, blockSize - 1, blockSize);

        g.setStroke((new BasicStroke(2)));
        g.setColor(Color.black);
        g.drawLine(topX + blockSize - strokeDecal - 1, topY + strokeDecal, topX + blockSize - strokeDecal - 1, topY + blockSize - (strokeDecal * 2));
        g.drawLine(topX + strokeDecal, topY + blockSize - strokeDecal, topX + blockSize - (strokeDecal * 2), topY + blockSize - strokeDecal);

        g.setStroke((new BasicStroke(3)));
        g.setColor(color2);
        g.drawRect(topX + strokeDecal, topY + strokeDecal, blockSize - (strokeSize + (strokeDecal * 2)), blockSize - (strokeSize + (strokeDecal * 2)));

        g.setColor(WhiteNes);
        g.setStroke((new BasicStroke(1)));
        g.fillRect(topX, topY, strokeSize, strokeSize);
      }
      else {
        Color fillColor = ShapeType.FULL_COLOR_1.equals(shapeType.getType()) ? color1 : color2;

        g.setStroke((new BasicStroke(1)));
        g.setColor(fillColor);

        g.fillRect(topX, topY, blockSize, blockSize);

        g.setStroke((new BasicStroke(2)));
        g.setColor(Color.black);

        g.drawLine(topX + blockSize - strokeDecal, topY + strokeDecal, topX + blockSize - strokeDecal, topY + blockSize - strokeDecal);
        g.drawLine(topX + strokeDecal, topY + blockSize - strokeDecal, topX + blockSize - strokeDecal, topY + blockSize - strokeDecal);

        g.setColor(WhiteNes);
        g.setStroke((new BasicStroke(1)));
        g.fillRect(topX, topY, strokeSize, strokeSize);
        g.fillRect(topX + strokeSize, topY + strokeSize, strokeSize * 2, strokeSize);
        g.fillRect(topX + strokeSize, topY + strokeSize * 2, strokeSize, strokeSize);
      }
    }

  }

  void drawUI(Graphics2D g) {
    // grid background
    g.setColor(gridColor);

    Rectangle gridRectFill = new Rectangle((int) gridRect.getX() - 1, (int) gridRect.getY() - 1, (int) gridRect.getWidth() + 3, (int) gridRect.getHeight() + 3);

    g.fill(gridRectFill);

    g.setColor(gridColor.darker());

    // the borders of grid and preview panel
    g.setColor(gridBorderColor);

    Rectangle gridRectBorder = new Rectangle((int) gridRect.getX() - 4, (int) gridRect.getY() - 4, (int) gridRect.getWidth() + 8, (int) gridRect.getHeight() + 8);
    g.setStroke(largeStroke);
    g.draw(gridRectBorder);
    g.draw(previewRect);

    lockGrid.lock();
    try {

      // the blocks dropped in the grid
      for (int r = 0; r < nRows; r++) {
        for (int c = 0; c < nCols; c++) {
          int idx = grid[r][c];
          if (idx > EMPTY)
            drawSquare(g, idx, r, c);
        }
      }
    }
    finally {
      lockGrid.unlock();
    }

    // scoreboard
    int x = scoreX;
    int y = scoreY;
    g.setColor(textColor);
    g.setFont(smallFont);
    g.drawString(format("hiscore  %6d", scoreboard.getTopscore()), x, y);
    g.drawString(format("level    %6d", scoreboard.getCurrentLevel()), x, y + 30);
    g.drawString(format("lines    %6d", scoreboard.getLines()), x, y + 60);
    g.drawString(format("score    %6d", scoreboard.getScore()), x, y + 90);

    // preview
    int minX = 5, minY = 5, maxX = 0, maxY = 0;
    for (int[] p : nextShape.pos) {
      minX = min(minX, p[0]);
      minY = min(minY, p[1]);
      maxX = max(maxX, p[0]);
      maxY = max(maxY, p[1]);
    }
    double cx = previewCenterX - ((minX + maxX + 1) / 2.0 * blockSize);
    double cy = previewCenterY - ((minY + maxY + 1) / 2.0 * blockSize);

    g.translate(cx, cy);
    for (int[] p : nextShape.pos)
      drawSquare(g, nextShape.getShapeEnum().ordinal(), p[1], p[0]);
    g.translate(-cx, -cy);
  }

  void drawFallingShape(Graphics2D g) {
    int idx = fallingShape.getShapeEnum().ordinal();

    if (fallingShape.isNew() && testGameOver()) {
      gameOver();
    }

    for (int[] p : fallingShape.pos) {
      if (!fallingShape.isNew()) drawSquare(g, idx, fallingShapeRow + p[1], fallingShapeCol + p[0]);
    }
  }

  public void gameOver() {
	  musicFX.stop();
	  musicFastFX.stop();
	  gameOverFX.play();	  
    scoreboard.setGameOver();
    scoreboard.setTopscore();
  }

  private boolean testGameOver() {

    for (int[] p : fallingShape.pos) {

      if (grid[fallingShapeRow + p[1] + 1][fallingShapeCol + p[0]] >= 0) return true;
    }

    return false;
  }

  @Override
  public void paintComponent(Graphics gg) {
    super.paintComponent(gg);
    Graphics2D g = (Graphics2D) gg;

    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);

    BufferedImage bufferImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    // I used Graphics2D instead of Graphics here, because its more flexible and lets you do more things.
    Graphics2D g2 = (Graphics2D) bufferImage.getGraphics();

    drawUI(g2);

    if (scoreboard.isGameOver()) {
      drawStartScreen(g2);
    }
    else {
      if (drawFallingShapeControl) drawFallingShape(g2);
    }

    g.drawImage(bufferImage, 0, 0, null);
  }

  boolean canRotate(Shape s) {
    if (s.isNew() || s.getShapeEnum() == ShapeEnum.Square || removingLine)
      return false;

    int[][] pos = new int[4][2];
    for (int i = 0; i < pos.length; i++) {
      pos[i] = s.pos[i].clone();
    }

    for (int[] row : pos) {
      int tmp = row[0];
      row[0] = row[1];
      row[1] = -tmp;
    }

    for (int[] p : pos) {
      int newCol = fallingShapeCol + p[0];
      int newRow = fallingShapeRow + p[1];

      if (newRow < 0 || newRow >= nRows) return false;
      if (newCol < 0 || newCol >= nCols) return false;
      if (grid[newRow][newCol] != EMPTY) return false;
    }
    return true;
  }

  boolean canRotateAnti(Shape s) {
    if (s.getShapeEnum() == ShapeEnum.Square || removingLine)
      return false;

    int[][] pos = new int[4][2];
    for (int i = 0; i < pos.length; i++) {
      pos[i] = s.pos[i].clone();
    }

    for (int[] row : pos) {
      int tmp = row[1];
      row[1] = row[0];
      row[0] = -tmp;
    }

    for (int[] p : pos) {
      int newCol = fallingShapeCol + p[0];
      int newRow = fallingShapeRow + p[1];
      if (newRow < 0 || newRow >= nRows) return false;
      if (newCol < 0 || newCol >= nCols) return false;
      if (grid[newRow][newCol] != EMPTY) { return false; }
    }
    return true;
  }

  void rotate(Shape s) {
    if (s.getShapeEnum() == ShapeEnum.Square)
      return;

    for (int[] row : s.pos) {
      int tmp = row[0];
      row[0] = row[1];
      row[1] = -tmp;
    }
  }

  void rotateAnti(Shape s) {
    if (s.getShapeEnum() == ShapeEnum.Square)
      return;

    for (int[] row : s.pos) {
      int tmp = row[1];
      row[1] = row[0];
      row[0] = -tmp;
    }
  }

  void move(Dir dir) {
    fallingShapeRow += dir.y;
    fallingShapeCol += dir.x;
  }

  boolean canMove(Shape s, Dir dir) {
    if (s.isNew() || removingLine) return false;

    for (int[] p : s.pos) {
      int newCol = fallingShapeCol + dir.x + p[0];
      int newRow = fallingShapeRow + dir.y + p[1];
      if (grid[newRow][newCol] != EMPTY)
        return false;
    }
    return true;
  }

  int nbMove(Shape s, Dir dir) {
    if (s.isNew()) return 0;

    int nb = 0;

    int curCol = fallingShapeCol;
    int curRow = fallingShapeRow;

    for (int row = curRow; row < nRows; row++) {

      for (int[] p : s.pos) {
        int newCol = curCol + dir.x + p[0];
        int newRow = row + dir.y + p[1];

        if (grid[newRow][newCol] != EMPTY)
          return nb;
      }

      nb++;
    }

    return nb;
  }

  void shapeHasLanded() {
    addShape(fallingShape);

    scoreboard.addLines(removeLines(this));
    testHigh();
    addARE += computeARE(fallingShapeRow);
    selectShape();
    instantDrop = false;
  }

  private void testHigh() {
	  
	  boolean newDropHighStack = false;
	  
	  for (int r = 5; r >= 0; r--) {
	        for (int c = 1; c < nCols - 1; c++) {
	        	if (grid[r][c] >= 0) {
	        		newDropHighStack = true;
	        		break;
	        	}
	        }
	  }
	  
	  
	  if(!highStack && newDropHighStack) {
		  highStack = true;
		  musicFX.stop();
		  musicFastFX.play();
	  }else if(highStack && !newDropHighStack) {
		  musicFastFX.stop();
		  musicFX.play();
	  }
  }
  
  private int computeARE(int fallingShapeRow) {
    if (fallingShapeRow >= 16) return 167;
    else if (fallingShapeRow >= 12) return 200;
    else if (fallingShapeRow >= 8) return 233;
    else if (fallingShapeRow >= 4) return 267;
    else return 300;
  }

  int removeLines(Tetris tetris) {

    int count = 0;
    if (!removingLine) {
      for (int r = 0; r < nRows - 1; r++) {
        for (int c = 1; c < nCols - 1; c++) {

          lockGrid.lock();
          try {
            if (grid[r][c] == EMPTY)
              break;
          }
          finally {
            lockGrid.unlock();
          }

          if (c == nCols - 2) {
            count++;
            linesToRemove.add(r);
          }
        }
      }

      if (count > 0) {   	      	  
        removingLine = true;
        drawFallingShapeControl = false;

        if (testRemove == null) {
          testRemove = new SuperTimer(al -> {
            if (linesToRemove.size() > 0) {

              boolean allEmpty = true;
              lockGrid.lock();
              try {
                for (int c = 5, c2 = 6; c > 0; c--, c2++) {
                  for (int r = 0; r < nRows; r++) {

                    if ((linesToRemove.contains(r) && grid[r][c] >= 0) || (linesToRemove.contains(r) && grid[r][c2] >= 0)) {
                      allEmpty = false;
                    }

                  }
                }
              }
              finally {
                lockGrid.unlock();
              }

              if (allEmpty) {

                linesToRemove.forEach(line -> moveLineDownward(line));
                linesToRemove.clear();
                drawFallingShapeControl = true;
                removingLine = false;
                testRemove.destroyTimer();
              }
              else removeLine(linesToRemove);

            }
          }, 25, 65);
        }

        testRemove.setRepeat(true);
        testRemove.restart();
      }
      
      if(count == 0) {
          landFX.play();
      }
      else if (count == 4) {
        addARE += 300;
        tetrisFX.play();
      }
      else {
    	  lineRemoveFX.play();
      }
    }
    
    return count;
  }

  void removeLine(List<Integer> linesToRemove) {

    lockGrid.lock();
    try {
      for (int r = 0; r < nRows; r++) {
        for (int c = 5, c2 = 6; c > 0; c--, c2++) {
          if (linesToRemove.contains(r) && grid[r][c] >= 0 && grid[r][c2] >= 0) {
            grid[r][c] = EMPTY;
            grid[r][c2] = EMPTY;
            break;
          }

        }
      }
    }
    finally {
      lockGrid.unlock();
    }

  }

  void moveLineDownward(int line) {

    lockGrid.lock();
    try {
      for (int c = 0; c < nCols; c++) {
        for (int r = line; r > 0; r--)
          grid[r][c] = grid[r - 1][c];
      }

    }
    finally {
      lockGrid.unlock();
    }

  }

  void addShape(Shape s) {
    if (removingLine) return;

    for (int[] p : s.pos) {
      int row = fallingShapeRow + p[1];
      int col = fallingShapeCol + p[0];

      lockGrid.lock();
      try {
        if (row >= 0 && col >= 0) grid[row][col] = s.getShapeEnum().ordinal();
      }
      finally {
        lockGrid.unlock();
      }

    }

  }

  public static void main(String[] args) {	  
    SwingUtilities.invokeLater(() -> {
      Tetris tetris = new Tetris();

      TetrisFrame f = new TetrisFrame(tetris);
      f.setVisible(true);
      
      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            while (true) {

                
            			/* Get the available controllers */
            			if(ControllerEnvironment.getDefaultEnvironment() != null) {

                			Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
                			if (controllers.length == 0) {
                				System.out.println("Found no controllers.");
                				System.exit(0);
                			}

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
                					
                					tetris.manageDeviceEvent(comp.getIdentifier(), event);            					
                				}
                			}


                			try {
                				Thread.sleep(17);
                			} catch (InterruptedException e) {
                				// TODO Auto-generated catch block
                				e.printStackTrace();
                			}
            			}
            	
            		
              tetris.repaint();
              Thread.sleep(17);
            }
          }
          catch (Exception e) {
            //
          }
        }
      }).start();
    });
  }
  
  private void manageDeviceEvent(Identifier id, Event event) {
		float eventval = event.getValue();
	  if(id == Identifier.Axis.POV) {
		//au bas g d en fcontion de valeur 0.25 0.5 0.75 1	
			if(eventval == 0.75f) {
				//bas
				managePressedKeys(UsedKeys.DOWN);
			}
			if(eventval == 1f) {
				//g
				managePressedKeys(UsedKeys.LEFT);
			}
			if(eventval == 0.25f) {
				//haut
				managePressedKeys(UsedKeys.UP);
			}
			if(eventval == 0.5f) {
				//d
				managePressedKeys(UsedKeys.RIGHT);
			}
			if(eventval == 0f) {
				checkReleaseUsedKeys(UsedKeys.RIGHT);
				checkReleaseUsedKeys(UsedKeys.DOWN);
				checkReleaseUsedKeys(UsedKeys.LEFT);
				checkReleaseUsedKeys(UsedKeys.UP);
			}
	  }
	  else {
		  getKeyDeviceByIdentifier(id).ifPresent(kd -> {
			  if(eventval > 0) managePressedKeys(kd.getKey());				
				else checkReleaseUsedKeys(kd.getKey());				
		  }); 		  
	  }
  }
  
  private enum ShapeType {
    EMPTY,
    FULL_COLOR_1,
    FULL_COLOR_2
  }

  protected enum ShapeEnum {
    ZShape(new int[][] { { 0, -1 }, { 0, 0 }, { -1, 0 }, { -1, 1 } }, ShapeType.FULL_COLOR_1),
    SShape(new int[][] { { 0, -1 }, { 0, 0 }, { 1, 0 }, { 1, 1 } }, ShapeType.FULL_COLOR_2),
    IShape(new int[][] { { 0, -1 }, { 0, 0 }, { 0, 1 }, { 0, 2 } }, ShapeType.EMPTY),
    TShape(new int[][] { { -1, 0 }, { 0, 0 }, { 1, 0 }, { 0, -1 } }, ShapeType.EMPTY),
    Square(new int[][] { { 0, 0 }, { 1, 0 }, { 0, -1 }, { 1, -1 } }, ShapeType.EMPTY),
    LShape(new int[][] { { -1, -1 }, { 0, -1 }, { 0, 0 }, { 0, 1 } }, ShapeType.FULL_COLOR_1),
    JShape(new int[][] { { 0, 0 }, { 0, 1 }, { 1, -1 }, { 0, -1 } }, ShapeType.FULL_COLOR_2);

    private ShapeType type;

    public ShapeType getType() {
      return type;
    }

    public void setType(ShapeType type) {
      this.type = type;
    }

    private ShapeEnum(int[][] shape, ShapeType type) {
      this.shape = shape;
      this.type = type;
    }

    final int[][] shape;
  }

  public boolean isClassicColor() {
    return classicColor;
  }

  public void setClassicColor(boolean classicColor) {
    this.classicColor = classicColor;
  }

  public boolean isUpActive() {
    return upActive;
  }

  public void setUpActive(boolean upActive) {
    this.upActive = upActive;
  }

  private ShapeEnum getShapeByIndex(int index) {
    switch (index) {
      case Z_SHAPE_IDX:
        return ShapeEnum.ZShape;
      case S_SHAPE_IDX:
        return ShapeEnum.SShape;
      case I_SHAPE_IDX:
        return ShapeEnum.IShape;
      case T_SHAPE_IDX:
        return ShapeEnum.TShape;
      case SQUARE_SHAPE_IDX:
        return ShapeEnum.Square;
      case L_SHAPE_IDX:
        return ShapeEnum.LShape;
      case J_SHAPE_IDX:
        return ShapeEnum.JShape;
      default:
        return null;
    }
  }
  
  public Float getMasterVolume() {
		return masterVolume;
	}

	public void setMasterVolume(Float masterVolume) {
		this.masterVolume = masterVolume;
	}
  
  public void setMasterVolume() {
      Info source = Port.Info.SPEAKER;
      //        source = Port.Info.LINE_OUT;
      //        source = Port.Info.HEADPHONE;

          if (AudioSystem.isLineSupported(source)) 
          {
              try 
              {
                  Port outline = (Port) AudioSystem.getLine(source);
                  outline.open();                
                  FloatControl volumeControl = (FloatControl) outline.getControl(FloatControl.Type.VOLUME);                
                  volumeControl.setValue(masterVolume);            
              } 
              catch (LineUnavailableException ex) 
              {
                  System.err.println("source not supported");
                  ex.printStackTrace();
              }            
          }
  }
  
  public Optional<Identifier> getIdentifierToKeyDeviceMap(UsedKeys uk, DeviceType dt) {
  	return identifierToKeyDeviceMap.entrySet().stream().filter(es -> es.getValue().getKey() == uk && es.getValue().getDeviceType() == dt).map(es -> es.getKey()).findFirst();
  }

  public class SuperTimer {
    private final Timer t;

    private boolean repeat = false;

    public SuperTimer(ActionListener al, long initialDelay, long delay) {
      t = new Timer((int) delay, al);
      t.setInitialDelay((int) initialDelay);
      t.setRepeats(repeat);
    }

    public boolean isRepeat() {
      return repeat;
    }

    public void setRepeat(boolean repeat) {
      this.repeat = repeat;
      t.setRepeats(repeat);
    }


    public boolean isRunning() {
     return t.isRunning();
    }
    
    public void start() {
      t.start();
    }

    public void restart() {
      t.restart();
    }

    public void setDelay(int delay) {
      t.setDelay(delay);
      t.setInitialDelay(delay);
      t.restart();
    }

    public void destroyTimer() {
      if (t != null) {
        t.stop();
      }
    }
    

  }
}

