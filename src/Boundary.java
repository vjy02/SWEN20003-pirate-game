/**
 * Boundary class returning necessary coordinates
 */
public class Boundary extends Entity{
    public Boundary(double x, double y){
        super(x,y);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
