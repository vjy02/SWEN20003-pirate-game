import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Block class detailing placement and boundaries
 */
public class Block extends Entity{
    private final static Image BLOCK = new Image("res/block.png");

    public Block(int x, int y){
        super(x,y);
    }

    /**
     * Method that performs state update
     */
    public void update() {
        BLOCK.drawFromTopLeft(x, y);
    }

    public Rectangle getBoundingBox(){
        return BLOCK.getBoundingBoxAt(new Point(x, y));
    }
}