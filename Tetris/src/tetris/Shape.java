package tetris;

import tetris.Tetris.ShapeEnum;

public class Shape {

  public final int[][] pos = new int[4][2];
  private final ShapeEnum shapeEnum;

  private boolean lastRotateAnti = true; 
  
 

private boolean isNew = false;

  public Shape(ShapeEnum shapeBase) {
    shapeEnum = shapeBase;
    reset();
  }

  public boolean isNew() {
    return isNew;
  }

  public void setNew(boolean isNew) {
    this.isNew = isNew;
  }

  void reset() {
    for (int i = 0; i < pos.length; i++) {
      pos[i] = shapeEnum.shape[i].clone();
      isNew = true;
    }
  }

  public ShapeEnum getShapeEnum() {
    return shapeEnum;
  }
  public boolean isLastRotateAnti() {
		return lastRotateAnti;
	}

	public void setLastRotateAnti(boolean lastRotateAnti) {
		this.lastRotateAnti = lastRotateAnti;
	}
  
}
