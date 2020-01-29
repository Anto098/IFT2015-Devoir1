package lindenmayer;
import java.awt.geom.Point2D;
public interface Turtle {
    public void draw();
    public void move();
    public void turnR();
    public void turnL();
    public void push();
    public void pop();
    public void stay();
    /**
     * initializes the turtle state (and clears the state stack)
     * @param position turtle position
     * @param angle_deg angle in degrees (90=up, 0=right)
     */
    public void init(Point2D position, double angle_deg);
    /**
     * position of the turtle
     * @return position as a 2D point
     */
    public Point2D getPosition();
    /**
     * angle of the turtle's nose
     * @return angle in degrees
     */
    public double getAngle();
    /**
     * sets the unit step and turn
     * @param step length of an advance (move or draw)
     * @param delta unit angle change in degrees (for turnR and turnL)
     */
    public void setUnits(double step, double delta);
}