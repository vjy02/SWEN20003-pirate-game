import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Bomb class detailing placement, boundaries and explosion interactions
 */
public class Bomb extends Entity{
    private Image BOMB = new Image("res/bomb.png");
    private final static int DAMAGE_POINTS = 10;
    private final static double HZ = 60.0;
    private final static double HUNDRED = 100.0;
    private final static double EXPLOSION_DURATION = 500.0;

    private boolean exploded = false;
    public boolean done = false;
    private int explodeCount = 0;

    public Bomb(int x, int y){
        super(x,y);
    }

    /**
     * Method that performs state update
     */
    public void update() {
        if (!done) {
            if (exploded) {
                // Check if explosion is finished
                explodeCount++;
                if (explodeCount / (HZ / HUNDRED) == EXPLOSION_DURATION) {
                    done = true;
                }
            }
            BOMB.drawFromTopLeft(x, y);
        }
    }

    /**
     * Method that performs state update
     */
    public boolean finishExploding(){
        return done;
    }

    public boolean dmgDealt(){
        return exploded;
    }

    public int getDmg(){
        return DAMAGE_POINTS;
    }

    /**
     * Updates bomb as explosion occurs
     */
    public int explode(int health){
        BOMB = new Image("res/explosion.png");
        exploded = true;
        return health - DAMAGE_POINTS;
    }

    public Rectangle getBoundingBox(){
        return BOMB.getBoundingBoxAt(new Point(x, y));
    }
}