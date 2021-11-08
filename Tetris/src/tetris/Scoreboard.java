package tetris;

public class Scoreboard {
  static final int MAXLEVEL = 29;

  private int firstLevel = 0;
  private int currentLevel;
  private int lines;
  private int score;
  private int topscore;
  private boolean gameOver = true;
  private boolean firstLevelCapPass = false;
  private static final double speedMultiplier = 1.15;

  void reset() {
    setTopscore();
    lines = score = 0;
    currentLevel = firstLevel;
    gameOver = false;
    firstLevelCapPass = false;
  }

  void setGameOver() {
    gameOver = true;
  }

  public boolean isGameOver() {
    return gameOver;
  }

  void setTopscore() {
    if (score > topscore)
      topscore = score;
  }

  int getTopscore() {
    return topscore;
  }

  int getSpeed() {

    switch (currentLevel) {
      case 0:
        return (int) (800 * speedMultiplier);
      case 1:
        return (int) (715 * speedMultiplier);
      case 2:
        return (int) (632 * speedMultiplier);
      case 3:
        return (int) (549 * speedMultiplier);
      case 4:
        return (int) (466 * speedMultiplier);
      case 5:
        return (int) (383 * speedMultiplier);
      case 6:
        return (int) (300 * speedMultiplier);
      case 7:
        return (int) (216 * speedMultiplier);
      case 8:
        return (int) (133 * speedMultiplier);
      case 9:
        return (int) (100 * speedMultiplier);
      case 10:
      case 11:
      case 12:
        return (int) (83 * speedMultiplier);
      case 13:
      case 14:
      case 15:
        return (int) (67 * speedMultiplier);
      case 16:
      case 17:
      case 18:
        return (int) (50 * speedMultiplier);
      case 29:
        return (int) (17 * speedMultiplier);
      default:
        return (int) (33 * speedMultiplier);
    }
  }

  public void addScore(int sc) {
    score += sc;
  }

  void addLines(int line) {

    switch (line) {
      case 1:
        addScore(40 * (getCurrentLevel() + 1));
        break;
      case 2:
        addScore(100 * (getCurrentLevel() + 1));
        break;
      case 3:
        addScore(300 * (getCurrentLevel() + 1));
        break;
      case 4:
        addScore(1200 * (getCurrentLevel() + 1));
        break;
      default:
        return;
    }

    int nbLinesBefore = lines;

    lines += line;

    if (!firstLevelCapPass) testFirstLevelCap();

    else if (lines % 10 < nbLinesBefore % 10) addLevel();

  }

  private void testFirstLevelCap() {
    if ((firstLevel <= 9 && lines >= (firstLevel * 10 + 10)) || (lines >= Math.max(100, firstLevel * 10 - 50))) {
      firstLevelCapPass = true;
      addLevel();
    }
  }

  void addLevel() {
    if (currentLevel < MAXLEVEL)
      currentLevel++;
  }

  int getCurrentLevel() {
    return currentLevel;
  }

  int getLines() {
    return lines;
  }

  int getScore() {
    return score;
  }

  public int getFirstLevel() {
    return firstLevel;
  }

  public void setFirstLevel(int firstLevel) {
    this.firstLevel = firstLevel;
  }

}
