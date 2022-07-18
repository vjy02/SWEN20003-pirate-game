import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.Random;
import java.util.ArrayList;

/**
 * Pirate class detailing movement, attacking and interactions with other objects
 */
public class Pirate extends Sailor{
    private final static Image PIRATE_LEFT = new Image("res/pirate/pirateLeft.png");
    private final static Image PIRATE_RIGHT = new Image("res/pirate/pirateRight.png");
    private final static Image PIRATE_LEFT_INV = new Image("res/pirate/pirateHitLeft.png");
    private final static Image PIRATE_RIGHT_INV = new Image("res/pirate/pirateHitRight.png");
    private final static int MAX_HEALTH_POINTS = 45;
    private final static int OFF_SET = 5;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 15;
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);
    private final static int NULL = 0;
    private final static double MAX_SPEED = 0.7;
    private final static double MIN_SPEED = 0.2;
    private final static double HZ = 60.0;
    private final static double HUNDRED = 100.0;
    private final static double INVINCIBLE_CD = 150.0;
    private final static double ATTACK_CD = 300.0;
    private final static int UP = 1;
    private final static int DOWN = 2;
    private final static int RIGHT = 3;
    private final static int LEFT = 4;
    private final static int X_OFFSET = 80;
    private final static int Y_OFFSET = 45;
    private final static int ATK_BOX_RANGE= 150;
    private final static int HP_OFFSET_Y = 6;
    private final Colour GREEN = new Colour(0, 0.8, 0.2);

    private static Random rand = new Random();
    private DrawOptions COLOUR = new DrawOptions();
    private Projectile bullet = new Projectile(getX(),getY());
    private ArrayList<Projectile> bullets = new ArrayList<Projectile>();

    private int healthPoints;
    private double speed;
    private int attackCD = 0;
    private int attackCD2 = 0;
    private int direction = 0;
    private boolean attackOn = false;
    private boolean attackOn2 = false;
    private boolean attackIdle = false;
    private boolean shotFired = false;
    private boolean invincibleState = false;
    private int invincibleCD = 0;
    private int shootCD = 0;
    private Image currentImage;

    public Pirate(double x, double y) {
        super(x, y);
        this.speed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * rand.nextDouble();
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = PIRATE_RIGHT;
        this.COLOUR = new DrawOptions();
        COLOUR.setBlendColour(GREEN);
    }

    public double getX() {return x;}

    public double getY() {return y;}


    /**
     * Method that performs state update
     */
    public void update(ArrayList<Entity> entities, Sailor sailor){
        // changes object image according to state and direction
        if (invincibleState){
            if (currentImage == PIRATE_LEFT){
                currentImage = PIRATE_LEFT_INV;
            }
            else if (currentImage == PIRATE_RIGHT){
                currentImage = PIRATE_RIGHT_INV;
            }
            invincibleCD++;
            if (invincibleCD / (HZ / HUNDRED) == INVINCIBLE_CD) {
                invincibleState = false;
                invincibleCD = NULL;
            }
        }

        if (direction == NULL) {
            direction = moveDirection();
        }
        if (direction == UP){
            if (!invincibleState) {
                if (currentImage == PIRATE_RIGHT_INV)
                    currentImage = PIRATE_RIGHT;
                else {
                    currentImage = PIRATE_LEFT;
                }
            }
            y = y-speed;
        }
        else if (direction == DOWN){
            if (!invincibleState) {
                if (currentImage == PIRATE_RIGHT_INV)
                    currentImage = PIRATE_RIGHT;
                else {
                    currentImage = PIRATE_LEFT;
                }
            }
            y = y+speed;
        }
        else if (direction == RIGHT){
            if (invincibleState){
                currentImage = PIRATE_RIGHT_INV;
            }
             else {
                 currentImage = PIRATE_RIGHT;
            }
            x = x+speed;
        }
        else if (direction == LEFT){
            if (invincibleState){
                currentImage = PIRATE_LEFT_INV;
            }
            else {
                currentImage = PIRATE_LEFT;
            }
            x = x - speed;
        }

        // controls shooting cool down
        if (shotFired){
            shootCD++;
            if (shootCD / (HZ / HUNDRED) == ATTACK_CD){
                shootCD = NULL;
                shotFired = false;
            }
        }

        // aiming and drawing projectiles shot towards Sailor
        for (Projectile current : bullets){
            if (!current.shotCheck()){
                current.shootBool(x,y,sailor);
            }
            current.update();
            current.checkCollisions(sailor, bottom_edge, top_edge, left_edge, right_edge);
        }

        renderHealthPoints();
        checkCollisions(entities, sailor);
        currentImage.drawFromTopLeft(x, y);
    }


    /**
     * Generates random direction at spawn of Pirate
     */
    protected int moveDirection() {
        int[] moveDirection = {UP, DOWN, RIGHT, LEFT};
        int direction = moveDirection[rand.nextInt(moveDirection.length)];
        return direction;
    }

    /**
     * Method that checks for collisions between Pirate and entities/boundaries also controls projectile collisions
     */
    private void checkCollisions(ArrayList<Entity> entities, Sailor sailor){
        Rectangle pirateBox = currentImage.getBoundingBoxAt(new Point(x, y+OFF_SET));
        Rectangle atkBox = new Rectangle(x-X_OFFSET,y-Y_OFFSET,ATK_BOX_RANGE,ATK_BOX_RANGE);
        Rectangle sailorBox = sailor.getCurrentImage().getBoundingBoxAt(new Point(sailor.x, sailor.y));

        // check collisions of projectiles on Sailor
        if (atkBox.intersects(sailorBox)){
            if (!shotFired) {
                bullets.add(new Projectile(getX(),getY()));
                shotFired = true;
            }
        }

        // check collisions of Pirate and other entities
        for (Entity current : entities) {
            if (current instanceof Block){
                Rectangle blockBox = current.getBoundingBox();
                if (pirateBox.intersects(blockBox)) {
                    moveBack();
                    break;
                }
            }
            if (current instanceof Bomb){
                Rectangle bombBox = current.getBoundingBox();
                if (pirateBox.intersects(bombBox)) {
                    moveBack();
                    break;
                }
            }
        }

        // Out of bounds detection
        if (isOutOfBound(entities)){
            moveBack();
        }
    }


    /**
     * Method that moves Pirate towards opposite direction
     */
    private void moveBack(){
        if (direction == UP){
            direction = DOWN;
        }
        else if (direction == DOWN){
            direction = UP;
        }
        else if (direction == RIGHT){
            direction = LEFT;
            currentImage = PIRATE_RIGHT;
        }
        else if (direction == LEFT){
            direction = RIGHT;
            currentImage = PIRATE_LEFT;
        }
    }

    /**
     * Method that renders the current health as a percentage on screen above Pirate
     */
    public void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/MAX_HEALTH_POINTS) * (int)HUNDRED;
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", x, y-HP_OFFSET_Y, COLOUR);
    }

    /**
     * Method that checks if Pirate's health is <= 0
     */
    public boolean isDead(){
        return healthPoints <= NULL;
    }

    /**
     * Prints log and updates damage taken
     */
    public void takeDmg(int dmg){
        if (!invincibleState) {
            this.healthPoints = healthPoints - dmg;
            System.out.println("Sailor inflicts "+dmg+" damage points on Pirate. " +
                    "Pirate's current health: "+healthPoints+"/"+MAX_HEALTH_POINTS);
            invincibleState = true;
        }
    }

    public Rectangle getBoundingBox(){
        return currentImage.getBoundingBoxAt(new Point(x, y));
    }


}
