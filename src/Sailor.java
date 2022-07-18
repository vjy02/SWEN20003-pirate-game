import bagel.*;
import bagel.util.Colour;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.ArrayList;

/**
 * Sailor class detailing all interactions with other objects alongside player inputted movement and attack
 */
public class Sailor{
    private final static Image SAILOR_LEFT = new Image("res/sailor/sailorLeft.png");
    private final static Image SAILOR_RIGHT = new Image("res/sailor/sailorRight.png");
    private final static Image SAILOR_LEFT_ATK = new Image("res/sailor/sailorHitLeft.png");
    private final static Image SAILOR_RIGHT_ATK = new Image("res/sailor/sailorHitRight.png");
    private final static int MOVE_SIZE = 2;
    private final static int NULL = 0;
    private final static int OFF_SET = 7;
    private final static int WIN_X = 990;
    private final static int WIN_Y = 630;
    private final static int HEALTH_X = 10;
    private final static int HEALTH_Y = 25;
    private final static int ORANGE_BOUNDARY = 65;
    private final static int RED_BOUNDARY = 35;
    private final static int FONT_SIZE = 30;
    private final static int BOUNDARY_X_OFFSET = 15;
    private final static double HZ = 60.0;
    private final static double HUNDRED = 100.0;
    private final static double ATTACK_STATE_CD = 100.0;
    private final static double ATTACK_CD = 200.0;
    private final static Font FONT = new Font("res/wheaton.otf", FONT_SIZE);
    private final static DrawOptions COLOUR = new DrawOptions();
    private final static Colour GREEN = new Colour(0, 0.8, 0.2);
    private final static Colour ORANGE = new Colour(0.9, 0.6, 0);
    private final static Colour RED = new Colour(1, 0, 0);

    private static int atkDmg = 15;
    public static int maxHP = 100;
    public static int healthPoints;

    public double bottom_edge;
    public double  top_edge;
    public double  right_edge;
    public double  left_edge;
    public double x;
    public double y;
    private double oldX;
    private double oldY;
    private int attackCD = 0;
    private int attackCD2 = 0;
    private boolean attackOn = false;
    private boolean attackOn2 = false;
    private boolean attackIdle = false;
    private Image currentImage;

    public Sailor(double startX, double startY){
        this.x=startX;
        this.y=startY;
        this.healthPoints = maxHP;
        this.currentImage = SAILOR_RIGHT;
        COLOUR.setBlendColour(GREEN);
    }

    /**
     * Return current Image state of Sailor
     */
    public Image getCurrentImage() {
        return currentImage;
    }

    /**
     * Method that performs state update
     */
    public void update(Input input, ArrayList<Entity> entities, ArrayList<Pirate> pirates){

        // update player movement while storing previous coordinates
        if (input.isDown(Keys.UP)){
            setOldPoints();
            move(NULL, -MOVE_SIZE);
        }else if (input.isDown(Keys.DOWN)){
            setOldPoints();
            move(NULL, MOVE_SIZE);
        }else if (input.isDown(Keys.LEFT)){
            setOldPoints();
            move(-MOVE_SIZE,NULL);
            if (attackOn) {
                currentImage = SAILOR_LEFT_ATK;
            }
            else {
                currentImage = SAILOR_LEFT;
            }
        }else if (input.isDown(Keys.RIGHT)){
            setOldPoints();
            move(MOVE_SIZE,NULL);
            if (attackOn) {
                currentImage = SAILOR_RIGHT_ATK;
            }
            else {
                currentImage = SAILOR_RIGHT;
            }
        }

        // Update sailor attack image if attacking
        if (!attackOn){
            if (currentImage == SAILOR_RIGHT_ATK){
                currentImage = SAILOR_RIGHT;
            }
            if (currentImage == SAILOR_LEFT_ATK){
                currentImage = SAILOR_LEFT;
            }
        }

        // Calculate time that Sailor is in attack state, return to normal state once time is over
        if (attackOn) {
            attackCD++;
            if (attackCD / (HZ / HUNDRED) == ATTACK_STATE_CD) {
                attackIdle = true;
                attackCD = NULL;
                attackOn2 = true;
                attackOn = false;
            }
        }

        // Calculate time that Sailor is in idle state, once time is over Sailor can attack again
        if (attackOn2) {
            attackCD2++;
            if (attackCD2 / (HZ / HUNDRED) == ATTACK_CD) {
                attackIdle = false;
                attackCD2 = NULL;
                attackOn2 = false;
            }
        }

        // initiate attack state for Sailor
        else if (input.wasPressed(Keys.S) && !attackIdle){
            attackOn = true;
            if (currentImage == SAILOR_LEFT) {
                currentImage = SAILOR_LEFT_ATK;
            } else if (currentImage == SAILOR_RIGHT) {
                currentImage = SAILOR_RIGHT_ATK;
            }
        }
        currentImage.drawFromTopLeft(x, y);
        checkCollisions(entities, pirates);
        renderHealthPoints();
    }

    /**
     * Method that checks for collisions between sailor and blocks, bombs and projectiles
     */
    private void checkCollisions(ArrayList<Entity> entities, ArrayList<Pirate> pirates){
        Rectangle sailorBox = currentImage.getBoundingBoxAt(new Point(x+OFF_SET, y+OFF_SET));

        // check collisions and print log
        for (Entity current : entities) {
            if (current instanceof Block){
                Rectangle blockBox = current.getBoundingBox();
                if (sailorBox.intersects(blockBox)) {
                    moveBack();
                }
            }

            // change bomb state and sailor take damage with first collision with bomb object
            if (current instanceof Bomb){
                Rectangle bombBox = current.getBoundingBox();
                if (sailorBox.intersects(bombBox)) {
                    if (!current.finishExploding()){
                        moveBack();
                    }
                    if (current.dmgDealt() == false){
                        System.out.println("Bomb inflicts "+((Bomb) current).getDmg()+" damage points on Sailor. " +
                                "Sailor's current health: "+healthPoints+"/"+maxHP);
                        healthPoints = current.explode(healthPoints);
                    }
                }
            }
        }

        // collision with pirate deals damage if sailor in attack state
        for (Pirate current : pirates){
            if (current instanceof Pirate){
                Rectangle pirateBox = current.getBoundingBox();
                if (sailorBox.intersects(pirateBox) && attackOn) {
                    current.takeDmg(atkDmg);
                    break;
                }
            }
        }

        // boundary collision
        if (isOutOfBound(entities)){
            moveBack();
        }
    }

    /**
     * Method that moves the sailor given the direction
     */
    private void move(int xMove, int yMove){
        x += xMove;
        y += yMove;
    }

    /**
     * Method that stores the old coordinates of the sailor
     */
    private void setOldPoints(){
        oldX = x;
        oldY = y;
    }

    /**
     * Method that moves the sailor back to its previous position
     */
    private void moveBack(){
        x = oldX;
        y = oldY;
    }

    /**
     * Updates sailor max HP
     */
    public static void setMaxHP(int HP) {
        maxHP = HP + maxHP;
        healthPoints = maxHP;
    }


    /**
     * Sets new HP for Sailor
     */
    public static void setHP(int HP) {
        if (healthPoints + HP > maxHP){
            healthPoints = maxHP;
        }
        else {
            healthPoints = healthPoints + HP;
        }
    }

    /**
     * Sets new attack damage for sailor
     */
    public static void setAtk(int atk) {
        atkDmg = atkDmg + atk;
    }

    public static int getAtk() {
        return atkDmg;
    }

    /**
     * Method that renders the current health as a percentage on screen
     */
    private void renderHealthPoints(){
        double percentageHP = ((double) healthPoints/maxHP) * (int)HUNDRED;
        if (percentageHP > ORANGE_BOUNDARY){
            COLOUR.setBlendColour(GREEN);
        }
        if (percentageHP <= RED_BOUNDARY){
            COLOUR.setBlendColour(RED);
        } else if (percentageHP <= ORANGE_BOUNDARY){
            COLOUR.setBlendColour(ORANGE);
        }
        FONT.drawString(Math.round(percentageHP) + "%", HEALTH_X, HEALTH_Y, COLOUR);
    }

    /**
     * Method that checks if sailor's health is <= 0
     */
    public boolean isDead(){
        return healthPoints <= 0;
    }

    /**
     * Method that checks if sailor has reached the ladder or treasure
     */
    public boolean hasWon(ArrayList<Entity> entities, boolean gameOn0){
        Rectangle sailorBox = currentImage.getBoundingBoxAt(new Point(x, y+OFF_SET));
        for (Entity current : entities) {
            if (current instanceof Treasure){
                Rectangle treasureBox = current.getBoundingBox();
                if (sailorBox.intersects(treasureBox)) {
                    return true;
                }
            }
        }
        if (gameOn0) {
            return (x >= WIN_X-BOUNDARY_X_OFFSET) && (y > WIN_Y);
        }
        return false;
    }

    /**
     * Method that checks if sailor has gone out-of-bound
     */
    public boolean isOutOfBound(ArrayList<Entity> entities){
        boolean passed = false;

        // read for boundary coordinates
        for (Entity current : entities) {
            if (current instanceof Boundary) {
                if (!passed) {
                    left_edge = current.getX();
                    top_edge = current.getY();
                    passed = true;
                }
                if (passed) {
                    right_edge = current.getX();
                    bottom_edge = current.getY();
                }
            }
        }
        return (y > bottom_edge) || (y < top_edge) || (x < left_edge) ||
                (x > right_edge-BOUNDARY_X_OFFSET);
    }

    public Rectangle getBoundingBox() {return currentImage.getBoundingBoxAt(new Point(x, y));
    }
}