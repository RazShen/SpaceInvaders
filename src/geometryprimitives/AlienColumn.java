package geometryprimitives;
import biuoop.DrawSurface;
import drawable.Block;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.util.ArrayList;

/**
 * This class manages a column of aliens, creates the aliens, and moving them by inputted speed and orders.
 *
 * @author Raz Shenkman
 * @version 1.0
 * @since 2017-06-23
 */
public class AlienColumn {
    private ArrayList<Block> alienColumnList;
    private Point originalStartPoint;
    private Point drawFrom;
    private int left;
    private int depth;
    private int right;

    /**
     * This constructor creates an alien column from starting point.
     * @param startDrawFrom starting point.
     */
    public AlienColumn(Point startDrawFrom) {
        Image image = null;
        try {
            image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("block_images/enemy.jpg"));
        } catch (Exception e) {
            System.out.println("Can't find enemy image");
        }
        this.originalStartPoint = startDrawFrom;
        this.drawFrom = startDrawFrom;
        this.left = (int) drawFrom.getX();
        this.right = this.left + 40;
        this.alienColumnList = new ArrayList<>();
        this.alienColumnList.add(new Block((int) this.drawFrom.getX(), (int) this.drawFrom.getY(), image));
        this.alienColumnList.add(new Block((int) this.drawFrom.getX(), (int) this.drawFrom.getY() + 40, image));
        this.alienColumnList.add(new Block((int) this.drawFrom.getX(), (int) this.drawFrom.getY() + 80, image));
        this.alienColumnList.add(new Block((int) this.drawFrom.getX(), (int) this.drawFrom.getY() + 120, image));
        this.alienColumnList.add(new Block((int) this.drawFrom.getX(), (int) this.drawFrom.getY() + 160, image));
        this.depth = ((int) drawFrom.getY() + (this.alienColumnList.size() * 30));
        this.initializeColumn();
    }

    /**
     * This method initializes column values.
     */
    public void initializeColumn() {
        this.drawFrom = this.originalStartPoint;
        this.left = (int) drawFrom.getX();
        this.right = this.left + 40;
        this.depth = this.findDepth();
    }

    /**
     * This method checks if the aliens reached bottom.
     * @return true/false.
     */
    public boolean checkTouchDown() {
        if (checkLeftAliens()) {
            if (findDepth() >= 490) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if the column touched the right border.
     * @return true/false.
     */
    public boolean checkTouchRight() {
        if (checkLeftAliens()) {
            if (this.right >= 790) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if the column touched the left border.
     * @return true/false.
     */
    public boolean checkTouchLeft() {
        if (checkLeftAliens()) {
            if (this.left <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method checks if there are not hit aliens in the column.
     * @return true/false.
     */
    public Boolean checkLeftAliens() {
       for (Block block : this.alienColumnList) {
           if (block.getInGame()) {
               return true;
           }
       }
       return false;
    }

    /**
     * This method changes the alien position according to a given point.
     * @param p new place for column.
     */
    public void returnBlockToPlace(Point p) {
        for (int i = 0; i < 5; i++) {
            this.alienColumnList.get(i).setUpperLeft(new Point(p.getX(), p.getY() + (i * 40)));
        }
        this.drawFrom = p;
        this.left = (int) drawFrom.getX();
        this.right = this.left + 40;

    }

    /**
     * This method changes the aliens position according to a given speed (used for drawing the aliens moving).
     * @param speed given speed to move the aliens by.
     */
    public void moveColumnToSide(int speed) {
        this.right = this.right + speed;
        this.left = this.left + speed;
        for (Block block: this.alienColumnList) {
            block.setUpperLeft(new Point(block.getCollisionRectangle().getUpperLeft().getX() + speed,
                    block.getCollisionRectangle().getUpperLeft().getY()));
        }
    }

    /**
     * This method moves the column down.
     */
    public void moveColumnDown() {
        this.depth = this.depth + 25;
        for (Block block: this.alienColumnList) {
            block.setUpperLeft(new Point(block.getCollisionRectangle().getUpperLeft().getX(),
                    block.getCollisionRectangle().getUpperLeft().getY() + 25));
        }
    }

    /**
     * This method draws the column on a given draw surface.
     * @param d draw surface.
     */
    public void drawColumn(DrawSurface d) {
        for (Block block : this.alienColumnList) {
            block.drawOn(d);
        }
    }

    /**
     * This method return the list of the aliens this column holds.
     * @return list of aliens.
     */
    public ArrayList<Block> getAlienColumnBlockList() {
        return this.alienColumnList;
    }

    /**
     * This method finds the depth of the column (the not hit alien with the biggest y coordinate value).
     * @return the depth of the column.
     */
    public int findDepth() {
        int returnedDepth = 0;
        for (Block block : this.alienColumnList) {
            if (block.getInGame()) {
                returnedDepth = (int) (block.getCollisionRectangle().getUpperLeft().getY() + 30);
            }
        }
        return returnedDepth;
    }

    /**
     * The x value of the column.
     * @return x value of the column.
     */
    public int findDist() {
        int retunedDist = 0;
        for (Block block : this.alienColumnList) {
            if (block.getInGame()) {
                retunedDist = (int) (block.getCollisionRectangle().getUpperLeft().getX());
            }
        }
        return retunedDist;
    }

    /**
     * This method returns the number of not hit aliens in this column.
     * @return  number of not hit aliens in this column.
     */
    public int getNumOfBlockInGame() {
        int blocksInGame = 0;
        for (Block block : this.alienColumnList) {
            if (block.getInGame()) {
                blocksInGame++;
            }
        }
        return blocksInGame;
    }
}
