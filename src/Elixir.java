import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

/**
 * Elixir class containing Item interactions and effects
 */
public class Elixir extends Entity{
    private final static Image ELIXIR = new Image("res/items/elixir.png");
    private final static Image ELIXIR_ICON = new Image("res/items/elixirIcon.png");
    private final static int ICON_X_OFFSET = 15;

    private int hpGain = 35;
    private boolean itemGone = false;

    public Elixir(int x, int y){
        super(x,y);
    }

    public void update() {
        if (!itemGone){
        ELIXIR.drawFromTopLeft(x, y);
        }
    }

    public void updateIcon(int y) {ELIXIR_ICON.drawFromTopLeft(ICON_X_OFFSET, y);}

    /**
     * Updates item effects on sailor
     */
    public void getItem (){
        Sailor.setMaxHP(hpGain);
    }

    /**
     * Checks for Sailor picking up item, updates log accordingly
     */
    public boolean itemCollision(Sailor sailor){
        Rectangle elixirBox = ELIXIR.getBoundingBoxAt(new Point(x, y));
        Rectangle sailorBox = sailor.getBoundingBox();

        if (!itemGone) {
            if (sailorBox.intersects(elixirBox)) {
                getItem();
                System.out.println("Sailor finds Elixir. Sailor's current health: "+sailor.healthPoints+"/"+sailor.maxHP);
                itemGone = true;
                return true;
            }
        }
        return false;
    }
}
