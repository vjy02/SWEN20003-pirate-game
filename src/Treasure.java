import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Treasure class containing drawing placement and boundary detection
 */
public class Treasure extends Entity{
    private final static Image TREASURE = new Image("res/treasure.png");

    public Treasure(int x, int y){
        super(x,y);
    }

    /**
     * Method that performs state update
     */
    public void update() {
        TREASURE.drawFromTopLeft(x, y);
    }

    public Rectangle getBoundingBox(){
        return TREASURE.getBoundingBoxAt(new Point(x, y));
    }
}