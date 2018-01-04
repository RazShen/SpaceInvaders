package drawable;
import java.awt.Color;


/**
 * This class Holds a shield object, which is a simple block.
 *
 * @author Raz Shenkman
 * @version 1.0
 * @since 2017-06-23
 */
public class Shield extends Block {
    /**
     * Constructor for shield that fully inherits from block constructor.
     * @param topLeftX top left x coordinate.
     * @param topLeftY top left y coordinate.
     * @param color color of the shield.
     */
    public Shield(int topLeftX, int topLeftY, Color color) {
        super(topLeftX, topLeftY, color);
    }
}
