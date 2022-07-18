import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Entity class detailing common features among entities in ShadowPirate game
 */
abstract class Entity {
    public double x;
    public double y;
    private Image entity;
    private final static int NULL = 0;

    public Entity(double xCoord, double yCoord){
        this.x=xCoord;
        this.y=yCoord;
    }

    /**
     * Get x coordinate of entity
     */
    public double getX() {return NULL;}

    /**
     * Get y coordinate of entity
     */
    public double getY() {return NULL;}

    public void update() {}

    /**
     * Check item collision between sailor and item
     */
    public boolean itemCollision(Sailor sailor){return false;}

    /**
     * Create hit box for entity
     */
    public Rectangle getBoundingBox(){
        return entity.getBoundingBoxAt(new Point(x, y));
    }

    /**
     * Initiate explosion of Bomb object
     */
    public int explode(int health){return NULL;}

    /**
     * Draw icon of picked up item
     */
    public void updateIcon(int itemNumY) {}

    /**
     * Check if bomb explosion is finished
     */
    public boolean finishExploding() {return false;}

    /**
     * damage update between entities and other objects
     */
    public boolean dmgDealt(){return false;}
}
