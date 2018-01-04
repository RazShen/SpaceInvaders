package drawable;
import biuoop.DrawSurface;
import collisiondata.BlockRemover;
import collisiondata.ScoreTrackingListener;
import geometryprimitives.AlienColumn;
import geometryprimitives.Point;
import interfaces.Sprite;
import rungame.GameLevel;
import java.util.ArrayList;

/**
 * This class manages multiple alien columns, it's movement algorithm, returning back to place algorithm and create
 * alien columns.
 *
 * @author Raz Shenkman
 * @version 1.0
 * @since 2017-06-23
 */
public class MultAlienColumn implements Sprite {
    private ArrayList<AlienColumn> alienColumnsList;
    private int originalSpeed;
    private double changingSpeed;
    private Point startDrawing;
    private Point drawingPoint;
    private double movedDown;
    private Boolean goRight;
    private Boolean goDown;
    private Boolean lost;

    /**
     * Default constructor for multiple alien column.
     */
    public MultAlienColumn() {
        this.alienColumnsList = new ArrayList<>();
        this.originalSpeed = 1;
        this.changingSpeed = 1;
        this.startDrawing = new Point(50, 50);
        this.drawingPoint = this.startDrawing;
        this.goRight = true;
        this.goDown = false;
        this.movedDown = 0;
        this.lost = false;
    }

    /**
     * This method creates 10 columns.
     */
    public void initializeAlienColumn() {
        for (int i = 0; i < 10; i++) {
            this.alienColumnsList.add(new AlienColumn(new Point(this.startDrawing.getX() + (i * 55),
                    this.startDrawing.getY() + 15)));
        }
    }

    /**
     * This method return current alien columns to place, by checking which column contains living aliens and
     * re arranging the columns.
     */
    public void returnToPlace() {
        int index = 0;
        for (int j = 0; j < 10; j++) {
            if (!this.alienColumnsList.get(j).checkLeftAliens()) {
                index++;
            } else {
                break;
            }
        }
        for (int j = index; j < 10; j++) {
            this.alienColumnsList.get(j).returnBlockToPlace(new Point(startDrawing.getX() + (j - index) * 50,
                    startDrawing.getY()));
        }
    }

    /**
     * This method checks if a column touched the right border.
     * @return true/false.
     */
    public Boolean findTouchRight() {
        for (AlienColumn alienColumn : this.alienColumnsList) {
            if (alienColumn.checkTouchRight()) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if a column touched the left border.
     * @return true/false.
     */
    public Boolean findTouchLeft() {
        for (AlienColumn alienColumn : this.alienColumnsList) {
            if (alienColumn.checkLeftAliens()) {
                if (alienColumn.checkTouchLeft()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method checks if a column touched the bottom alien boundary (where the shields are)..
     * @return true/false.
     */
    public Boolean findTouchDown() {
        for (AlienColumn alienColumn : this.alienColumnsList) {
            if (alienColumn.checkLeftAliens()) {
                if (alienColumn.checkTouchDown()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * This method moves all the columns by the current speed.
     */
    public void moveColumns() {
        for (AlienColumn alienColumn : this.alienColumnsList) {
            alienColumn.moveColumnToSide((int) this.changingSpeed);
        }
    }

    /**
     * Draw the sprite object on the screen.
     * @param d the draw surface to draw on.
     */
    public void drawOn(DrawSurface d) {
        for (AlienColumn alienColumn : this.alienColumnsList) {
            alienColumn.drawColumn(d);
        }
    }

    /**
     * This method moves all the columns one step down.
     */
    public void dropDown() {
        for (AlienColumn alienColumn : this.alienColumnsList) {
            alienColumn.moveColumnDown();
        }
    }

    /**
     * Notify the sprite object that time has passed.
     * @param dt is the amount of seconds passed since the last call.
     */
    public void timePassed(double dt) {
       this.moveColumns();
       if (findTouchRight()) {
           this.dropDown();
           this.changingSpeed = -(this.changingSpeed + 0.1 * this.changingSpeed);
       } else if (findTouchLeft()) {
           this.dropDown();
           this.changingSpeed = -(this.changingSpeed + 0.1 * this.changingSpeed);
       }
        if (findTouchDown()) {
            this.lost = true;
            return;
        }
    }

    /**
     * This method returns all the aliens this class holds.
     * @return all the aliens this class holds.
     */
    public ArrayList<Block> getMultAlienColumnBlocks() {
        ArrayList<Block> blockList = new ArrayList<Block>();
        for (AlienColumn alienColumn : this.alienColumnsList) {
            blockList.addAll(alienColumn.getAlienColumnBlockList());
        }
        return blockList;
    }

    /**
     * This method returns the list of aliens columns.
     * @return list of aliens columns.
     */
    public ArrayList<AlienColumn> getAlienColumnsList() {
        return this.alienColumnsList;
    }

    /**
     * This method returns if the alien columns hit bottom boundary.
     * @return true/false.
     */
    public Boolean ifHitBottom() {
        return this.lost;
    }

    /**
     * This method adds the aliens to sprite collection and the collidable object of the game environment of the game.
     * @param game the class game (for initializing).
     * @param blockRemover the blocked remover hit listener.
     * @param scoreTrackingListener the score tracking hit listener.
     */
    public void addToGame(GameLevel game, BlockRemover blockRemover, ScoreTrackingListener scoreTrackingListener) {
        ArrayList<Block> blockList = this.getMultAlienColumnBlocks();
        for (Block block : blockList) {
            block.setInGame();
            game.addSprite(block);
            block.addHitListener(blockRemover);
            block.addHitListener(scoreTrackingListener);
            game.addCollidable(block);
        }
    }

    /**
     * This method adds all the not hit aliens to the game again.
     * @param game the class game (for initializing).
     * @param blockRemover the blocked remover hit listener.
     * @param scoreTrackingListener the score tracking hit listener.
     */
    public void addAgainToGame(GameLevel game, BlockRemover blockRemover, ScoreTrackingListener scoreTrackingListener) {
        ArrayList<Block> blockList = this.getMultAlienColumnBlocks();
        for (Block block : blockList) {
            if (block.getInGame()) {
                block.setInGame();
                game.addSprite(block);
                block.addHitListener(blockRemover);
                block.addHitListener(scoreTrackingListener);
                game.addCollidable(block);
            }
        }
    }

    /**
     * This method removes lisetners from current living aliens.
     * @param blockRemover the blocked remover hit listener.
     * @param scoreTrackingListener the score tracking hit listener
     */
    public void removeListenersFromBlocks(BlockRemover blockRemover, ScoreTrackingListener scoreTrackingListener) {
        ArrayList<Block> blockList = this.getMultAlienColumnBlocks();
        for (Block block : blockList) {
            if (block.getInGame()) {
                block.removeHitListener(blockRemover);
                block.removeHitListener(scoreTrackingListener);
            }
        }
    }

    /**
     * This method initializes the speed of the alien columns.
     */
    public void initializeSpeed() {
        this.changingSpeed = this.originalSpeed;
    }

    /**
     * This method initialize hit bottom border boolean.
     */
    public void returnNotTouchDown() {
        this.lost = false;
    }

    /**
     * This method sets the speed of the columns.
     * @param inputSpeed of columns.
     */
    public void setSpeed(int inputSpeed) {
        this.originalSpeed = inputSpeed;
        this.changingSpeed = this.originalSpeed;
    }
}
