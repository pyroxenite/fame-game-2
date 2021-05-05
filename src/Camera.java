import javafx.scene.canvas.GraphicsContext;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

/**
 * Implements a smooth camera that can be controlled directly by coordinates via the 
 * `void setAimPos(Vector pos)` method. The position of the camera approaches the aim 
 * position at a configurable speed set by `void setSpeed(double speed)` at each call
 * of its update method. The same is true for its zoom level via the method called
 * `void setAimScale(double scale)`.
 * 
 * To instantly configure the camera's position and zoom amount, the same methods with
 * 'Aim' ommited bypass the glidding effect.
 * 
 * Alternatively, the camera can follow a specified Sprite target. This is can be set
 * with the `void setTarget(Sprite target)` method. The update method still needs to 
 * be called at each frame.
 */
class Camera implements Updatable {
    /** The true position of the camera. */
    private Vector pos = new Vector();
    private double scale = 1.0;
    private Vector aimPos = new Vector();
    private double aimScale = 1.0;
    private double speed = 0.05;

    /** Instantly sets position. */
    public void setPos(Vector pos) { 
        this.pos = pos.copy();
        this.aimPos = pos.copy();
    }

    public void setPos(double x, double y) { 
        pos = new Vector(x, y); 
        aimPos = pos.copy();
    }

    public Vector getPos() { return pos; }

    /** The true zoom level of the camera. */
    /** Instantly sets zoom level. */
    public void setScale(double scale) { 
        this.scale = scale;
        this.aimScale = scale;
    }

    public double getScale() { return scale; }

    /** The desired position of the camera. */
    public void setAimPos(Vector pos) { this.aimPos = pos; }
    public void setAimPos(double x, double y) { this.aimPos = new Vector(x, y); }
    public Vector getAimPos() { return aimPos; }

    /** The desired zoom level of the camera. */
    public void setAimScale(double scale) { this.aimScale = scale; }
    public double getAimScale() { return aimScale; }
    
    /**
     * The speed at which the camera glides to the desired values. It can vary from
     * 0.0 to 1.0 where 0.0 represents a fixed camera and 1.0 represents a camera that
     * reaches instantly its desired aim.
     * @param speed The speed.
     */
    public void setSpeed(double speed) { this.speed = speed; }
    
    /**
     * If set to null, the camera can be controlled directly via `void setAimPos(Vector pos)`.
     * Otherwise, the camera will aim for the target on update.
     */
    private Sprite target = null;
    public Sprite getTarget() { return target; }
    public void setTarget(Sprite target) { this.target = target; }

    /** Springs used to shake camera. */
    private Spring springX = new Spring();
    private Spring springY = new Spring();

    public double getX() {
        return pos.getX() + springX.getPos() * 5;
    }

    /** 
     * To be called at each animation frame. Updates the real values to approach
     * the aim values and follows target if defined.
     */
    public void update() {
        if (target != null) {
            aimPos = target.getPos().copy();
            aimPos.setY(Math.min(155-338/scale, aimPos.getY())); // camera upper bound
            aimPos.setY(Math.max(-580+338/scale, aimPos.getY())); // camera lower bound
        }
        double coef = 1 - Math.pow((1-speed), scale);
        pos = pos.copy().scale(1-coef).add(aimPos.copy().scale(coef));
        scale = scale*(1-coef) + aimScale*coef;
        springX.update();
        springY.update();
    }

    /**
     * Applies the transform to the supplied graphics context to achieve the camera effect.  
     * @param gc The GraphicsContext object.
     */
    public void applyTransform(GraphicsContext gc) {
        // gc.transform(new Affine(Transform.rotate(angle, 0, 0)));
        gc.transform(new Affine(Transform.scale(scale, scale)));
        gc.transform(new Affine(Transform.translate(-pos.getX() + springX.getPos(), -pos.getY() + springY.getPos())));
    }

    public void hitLeft(double intensity) {
        springX.setParameters(3, 0.2, intensity);
        springY.setParameters(1.3*3, 0.2, intensity);
        pos.setX(pos.getX()+10);
    }

    public void hitRight(double intensity) {
        springX.setParameters(3, 0.2, intensity);
        springY.setParameters(1.3*3, 0.2, intensity);
        pos.setX(pos.getX()-10);
    }

    public void hitGround(double intensity) {
        // springX.setParameters(3, 0.2, intensity);
        springY.setParameters(2, 0.2, intensity);
        pos.setY(pos.getY()-intensity/10);
    }
}