package lindenmayer;
import java.awt.geom.Point2D;
import java.util.Stack;

public class MyTurtle implements Turtle {
    private class State {
        private Point2D position;
        private double angle_deg;
        public State(Point2D position, double angle_deg) {
            this.position=position;
            this.angle_deg=angle_deg;
        }

        public Point2D getPosition() {
            return position;
        }

        public double getAngle_deg() {
            return angle_deg;
        }

        public void setPosition(Point2D position) {
            this.position = position;
        }

        public void setAngle_deg(double angle_deg) {
            this.angle_deg = angle_deg;
        }
    }
    private State state;
    private Stack stack = new Stack();
    private double delta,step;

    public void draw(){
    };
    public void move(){
        double x = state.getPosition().getX();
        double y = state.getPosition().getY();
        double new_x = x+Math.cos(state.getAngle_deg())*step;
        double new_y = y+Math.sin(state.getAngle_deg())*step;
        state.setPosition(new Point2D.Double(new_x,new_y));
    };
    public void turnR(){
        state.setAngle_deg(state.getAngle_deg()-delta);
    };
    public void turnL(){
        state.setAngle_deg(state.getAngle_deg()+delta);
    };
    public void push(){
        stack.push(state);
    };
    public void pop(){
        stack.pop();
    };
    public void stay(){

    };
    /**
     * initializes the turtle state (and clears the state stack)
     * @param position turtle position
     * @param angle_deg angle in degrees (90=up, 0=right)
     */
    public void init(Point2D position, double angle_deg) {
        state = new State(position, angle_deg);
        stack = new Stack();
    };
    /**
     * position of the turtle
     * @return position as a 2D point
     */
    public Point2D getPosition() {
        return state.getPosition();
    };
    /**
     * angle of the turtle's nose
     * @return angle in degrees
     */
    public double getAngle(){
        return state.getAngle_deg();
    };
    /**
     * sets the unit step and turn
     * @param step length of an advance (move or draw)
     * @param delta unit angle change in degrees (for turnR and turnL)
     */
    public void setUnits(double step, double delta){
        step = step;
        delta = delta;
    };

}
