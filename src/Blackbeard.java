import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.Random;
import java.util.ArrayList;

/**
 * Blackbeard class detailing movement, attacking and interactions with other objects
 */
public class Blackbeard extends Pirate{
    private final static Image BB_LEFT = new Image("res/blackbeard/blackbeardLeft.png");
    private final static Image BB_RIGHT = new Image("res/blackbeard/blackbeardRight.png");
    private final static Image BB_LEFT_INV = new Image("res/blackbeard/blackbeardHitLeft.png");
    private final static Image BB_RIGHT_INV = new Image("res/blackbeard/blackbeardHitRight.png");
    private final static int MAX_HEALTH_POINTS = 90;
    private final static int OFF_SET = 5;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 15;
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
    private final static int ATK_BOX_RANGE = 150;
    private final static int HP_OFFSET_Y = 6;


    private DrawOptions COLOUR = new DrawOptions();
    private final Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);

    private static Random rand = new Random();
    private ArrayList<ProjectileBB> bullets = new ArrayList<ProjectileBB>();

    private boolean shotFired = false;
    private int shootCD = 0;
    private int healthPoints;
    private double speed;
    private int direction = 0;
    private boolean invincibleState = false;
    private int invincibleCD = 0;
    private Image currentImage;

    public Blackbeard(double x, double y) {
        super(x, y);
        this.speed = MIN_SPEED + (MAX_SPEED - MIN_SPEED) * rand.nextDouble();
        this.healthPoints = MAX_HEALTH_POINTS;
        this.currentImage = BB_RIGHT;
        this.COLOUR = new DrawOptions();
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Method that performs state update
     */
    public void update(ArrayList<Entity> entities, Sailor sailor){

        // changes object image according to state and direction
        if (invincibleState){
            if (currentImage == BB_LEFT){
                currentImage = BB_LEFT_INV;
            }
            else if (currentImage == BB_RIGHT){
                currentImage = BB_RIGHT_INV;
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
                if (currentImage == BB_RIGHT_INV)
                    currentImage = BB_RIGHT;
                else {
                    currentImage = BB_LEFT;
                }
            }
            y = y-speed;
        }
        else if (direction == DOWN){
            if (!invincibleState) {
                if (currentImage == BB_RIGHT_INV)
                    currentImage = BB_RIGHT;
                else {
                    currentImage = BB_LEFT;
                }
            }
            y = y+speed;
        }
        else if (direction == RIGHT){
            if (invincibleState){
                currentImage = BB_RIGHT_INV;
            }
            else {
                currentImage = BB_RIGHT;
            }
            x = x+speed;
        }
        else if (direction == LEFT){
            if (invincibleState){
                currentImage = BB_LEFT_INV;
            }
            else {
                currentImage = BB_LEFT;
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
     * Method that moves Blackbeard towards opposite direction as it collides with the border or block
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
            currentImage = BB_RIGHT;
        }
        else if (direction == LEFT){
            direction = RIGHT;
            currentImage = BB_LEFT;
        }
    }

    /**
     * Method that checks for collisions between Blackbeard and other objects, also checks projectile collisions
     */
    private void checkCollisions(ArrayList<Entity> entities, Sailor sailor){
        Rectangle pirateBox = currentImage.getBoundingBoxAt(new Point(x, y+OFF_SET));
        Rectangle atkBox = new Rectangle(x-X_OFFSET,y-Y_OFFSET,ATK_BOX_RANGE,ATK_BOX_RANGE);
        Rectangle sailorBox = sailor.getCurrentImage().getBoundingBoxAt(new Point(sailor.x, sailor.y));

        // check collisions of projectiles on Sailor
        if (atkBox.intersects(sailorBox)){
            if (!shotFired) {
                bullets.add(new ProjectileBB(getX(),getY()));
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
     * Method that renders the current health as a percentage on screen
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
     * Method that checks if Blackbeard's health is <= 0
     */
    public boolean isDead(){
        return healthPoints <= NULL;
    }


    /**
     * Updates Blackbeard health according to damage taken, prints out log statement
     */
    public void takeDmg(int dmg){
        if (!invincibleState) {
            this.healthPoints = healthPoints - dmg;
            System.out.println("Sailor inflicts "+dmg+" damage points on Blackbeard. " +
                    "Blackbeard's current health: "+healthPoints+"/"+MAX_HEALTH_POINTS);
            invincibleState = true;
        }
    }

    /**
     * Bounding box to calculate hit box
     */
    public Rectangle getBoundingBox(){
        return currentImage.getBoundingBoxAt(new Point(x, y));
    }

}
