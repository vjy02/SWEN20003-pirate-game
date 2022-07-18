import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Sword class containing Item interactions and effects
 */
public class Sword extends Entity{
    private final static Image SWORD = new Image("res/items/sword.png");
    private final static Image SWORD_ICON = new Image("res/items/swordIcon.png");
    private final static int ICON_X_OFFSET = 15;
    private boolean itemGone = false;
    private int atkGain = 15;

    public Sword(int x, int y){
        super(x,y);
    }

    public void update() {
        if (!itemGone){
            SWORD.drawFromTopLeft(x, y);
        }
    }

    public void updateIcon(int y) {SWORD_ICON.drawFromTopLeft(ICON_X_OFFSET, y);}

    /**
     * Updates item effects on sailor
     */
    public void getItem (){
        Sailor.setAtk(atkGain);
    }

    /**
     * Checks for Sailor picking up item, updates log accordingly
     */
    public boolean itemCollision(Sailor sailor){
        Rectangle elixirBox = SWORD.getBoundingBoxAt(new Point(x, y));
        Rectangle sailorBox = sailor.getBoundingBox();

        if (!itemGone) {
            if (sailorBox.intersects(elixirBox)) {
                getItem();
                System.out.println("Sailor finds Sword. Sailor's damage points increased to "+sailor.getAtk());
                itemGone = true;
                return true;
            }
        }
        return false;
    }
}
