/**
 * A class for storing and manipulating 2D vectors.
 */
public class Vector {
    private double x;
    private double y;

    public Vector() {
        this.x = 0;
        this.y = 0;
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public String toString() {
        return "{ x: " + this.x + ", y: " + this.y + " }";
    }

    /**
     * Returns a new instance of Vector with identical coordinates.
     * @return The copied Vector.
     */
    public Vector copy() {
        return new Vector(this.x, this.y);
    }

    ////// Reading //////

    public double getX() {
        return this.x;
    }
 
    public double getY() {
        return this.y;
    }

    /**
     * Calculates the magnitude of the Vector instance.
     * @return The magnitude.
     */
    public double mag() {
        return Math.sqrt(Math.pow(this.x, 2) + Math.pow(this.y, 2));
    }

    /** 
     * Calculates the direction or heading in radians of the Vector instance. 
     * @return The angle.
    */
    public double heading() {
        return Math.atan2(this.y, this.x);
    }

    ////// Writing //////
    /*
     * All following methods return 'this' to allow chaining. 
     */

    public Vector setX(double value) {
        x = value;
        return this;
    }

    public Vector setY(double value) {
        y = value;
        return this;
    }
 
    /** 
     * Adds a Vector.
     * @param other The other vector.
     */
    public Vector add(Vector other) {
        x += other.x;
        y += other.y;
        return this;
    }

    public Vector add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    /** 
     * Subtracts a Vector.
     * @param other The other vector.
     */
    public Vector sub(Vector other) {
        x -= other.x;
        y -= other.y;
        return this;
    }

    public Vector sub(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }


    /** 
     * Scales a Vector.
     * @param factor Factor by which to scale.
     */
    public Vector scale(double factor) {
        x *= factor;
        y *= factor;
        return this;
    }

    /** 
     * Scales a Vector along the X axis.
     * @param factor Factor by which to scale.
     */
    public Vector scaleX(double factor) {
        x *= factor;
        return this;
    }

    /** 
     * Scales a Vector along the Y axis.
     * @param factor Factor by which to scale.
     */
    public Vector scaleY(double factor) {
        y *= factor;
        return this;
    }

    /** 
     * Rotates a Vector.
     * @param angle Angle of rotation in radians.
     */
    public Vector rotate(double angle) {
        double oldX = x;
        x =    x * Math.cos(angle) - y * Math.sin(angle);
        y = oldX * Math.sin(angle) - y * Math.cos(angle);
        return this;
    }

    /** 
     * Sets the magnitude of the vector. If zero, the result will have a heading of 0 radians.
     * @param mag The desired magnitude.
     */
    public Vector setMag(double mag) {
        double currentMag = this.mag();
        if (currentMag == 0.0) {
            x = 1;
            y = 0;
            currentMag = 1;
        } 
        this.scale(mag/currentMag);
        return this;
    } 

    ////// Tests //////

    public static void main(String [] args) {
        Vector v = new Vector(1, 0);
        v.rotate(Math.PI/2);
        Vector u = v.copy();
        System.out.println(u.mag());
    }
}