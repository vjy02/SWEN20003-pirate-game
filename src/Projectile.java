import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Projectile class controlling bullet movement and angle of fire done by enemies
 */
public class Projectile extends Entity{
    public final static Image BULLET = new Image("res/pirate/pirateProjectile.png");
    private final static DrawOptions ANGLE = new DrawOptions();

    private double angleRadians;
    private double velocityX;
    private double velocityY;
    private boolean bulletGone = false;
    private boolean shot = false;
    private double speed;
    private int dmg;

    public Projectile(double x, double y){
        super(x, y);
        this.angleRadians = 0;
        this.velocityX = 0;
        this.velocityY = 0;
        this.speed = 0.4;
        this.dmg = 10;
    }

    /**
     * Method that performs state update
     */
    public void update(){
        // Updates projectile trajectory towards angle calculated
        if (!bulletGone) {
            x += velocityX;
            y += velocityY;
            BULLET.drawFromTopLeft(x, y, ANGLE.setRotation(angleRadians));
        }
    }

    /**
     * Checks if projectile angle has been determined and ready to move
     */
    public boolean shotCheck(){
        if (shot){
            return true;
        }
        return false;
    }

    /**
     * Calculates angle of fire of projectile alongside velocities
     */
    public void shootBool(double pirateX, double pirateY, Sailor sailor){
        this.angleRadians = Math.atan2(sailor.y - pirateY, sailor.x - pirateX);
        this.velocityX = speed * Math.cos(angleRadians);
        this.velocityY = speed * Math.sin(angleRadians);
        shot = true;
    }

    /**
     * Calculates collision between projectile and sailor alongside boundaries
     */
    public void checkCollisions(Sailor sailor, double bottom_edge, double top_edge,
                                   double left_edge, double right_edge){
        Point bulletBox = new Point(x, y);
        Rectangle sailorBox = sailor.getCurrentImage().getBoundingBoxAt(new Point(sailor.x, sailor.y));

        // check collisions and print log
        if (sailorBox.intersects(bulletBox)) {
            if (!bulletGone) {
                sailor.healthPoints = sailor.healthPoints - dmg;
                System.out.println("Pirate inflicts "+dmg+" damage points on Sailor. " +
                        "Sailor's current health: "+sailor.healthPoints+"/"+sailor.maxHP);
            }
            bulletGone = true;
        }
        // boundary collision check
        if ( (y > bottom_edge) || (y < top_edge) || (x < left_edge) ||
                (x > right_edge)){
            bulletGone = true;
        }
    }
}
