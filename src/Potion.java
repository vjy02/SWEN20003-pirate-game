import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Potion class containing Item interactions and effects
 */
public class Potion extends Entity{
    private final static Image POTION = new Image("res/items/potion.png");
    private final static Image POTION_ICON = new Image("res/items/potionIcon.png");
    private final static int ICON_X_OFFSET = 15;
    private int hpGain = 25;
    private boolean itemGone = false;

    public Potion(int x, int y){
        super(x,y);
    }

    public void update() {
        if (!itemGone){
            POTION.drawFromTopLeft(x, y);
        }
    }
    public void updateIcon(int y) {POTION_ICON.drawFromTopLeft(ICON_X_OFFSET, y);}

    /**
     * Updates item effects on sailor
     */
    public void getItem (){
        Sailor.setHP(hpGain);
    }

    /**
     * Checks for Sailor picking up item, updates log accordingly
     */
    public boolean itemCollision(Sailor sailor){
        Rectangle elixirBox = POTION.getBoundingBoxAt(new Point(x, y));
        Rectangle sailorBox = sailor.getBoundingBox();

        if (!itemGone) {
            if (sailorBox.intersects(elixirBox)) {
                getItem();
                itemGone = true;
                System.out.println("Sailor finds Potion. Sailor's current health: "+sailor.healthPoints+"/"+sailor.maxHP);
                return true;
            }
        }
        return false;
    }
}
