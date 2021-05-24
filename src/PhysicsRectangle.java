import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PhysicsRectangle {
    protected Vector pos;
    protected Vector vel;
    private Vector nextPos;
    private Vector nextVel;
    protected double w;
    protected double h;
    private boolean isFixed = false;
    private boolean isEntity = true;

    public PhysicsRectangle(double x, double y, double w, double h) {
        pos = new Vector(x, y);
        nextPos = new Vector(x, y);
        vel = new Vector(0, 0);
        nextVel = new Vector(0, 0);
        this.w = w;
        this.h = h;
    }

    public Vector getPos() {
        return pos;
    }

    public void setPos(Vector pos) {
        this.pos = pos;
    }

    public Vector getVel() {
        return vel;
    }

    public void setVel(Vector vel) {
        this.vel = vel;
    }

    public Vector getNextPos() {
        return nextPos;
    }

    public void setNextPos(Vector nextPos) {
        this.nextPos = nextPos;
    }

    public Vector getNextVel() {
        return nextVel;
    }

    public void setNextVel(Vector nextVel) {
        this.nextVel = nextVel;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public PhysicsRectangle setFixed() {
        isFixed = true;
        return this;
    }

    public boolean getIsEntity() {
        return isEntity;
    }

    public PhysicsRectangle setIsEntity(boolean isEntity) {
        this.isEntity = isEntity;
        return this;
    } 

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

    public void draw(GraphicsContext gc) {
        gc.setLineWidth(0.2);
        if (isFixed)
            gc.setStroke(Color.WHITE);
        else
            gc.setStroke(Color.YELLOW);
        gc.strokeRect(
            this.pos.getX() - this.w / 2, 
            this.pos.getY() - this.h / 2, 
            this.w, 
            this.h
        );
    }

    public void nextFrame() {
        this.pos = this.nextPos;
        this.vel = this.nextVel;
    }
}