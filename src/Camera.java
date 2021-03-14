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
 * Alternatively, the camera can follow a specified target Sprite. This is can be set
 * with the `void setTarget(Sprite target)` method. The update method still needs to 
 * be called at each frame.
 */
class Camera implements Updatable {
    /** The true position of the camera. */
    private Vector pos = new Vector();
    /** Instantly sets position. */
    public void setPos(Vector pos) { 
        this.pos = pos;
        this.aimPos = pos;
    }
    public void setPos(double x, double y) { this.pos = new Vector(x, y); }
    public Vector getPos() { return pos; }

    /** The true zoom level of the camera. */
    private double scale = 1.0;
    /** Instantly sets zoom level. */
    public void setScale(double scale) { 
        this.scale = scale;
        this.aimScale = scale;
    }
    public double getScale() { return scale; }

    /** The desired position of the camera. */
    private Vector aimPos = new Vector();
    public void setAimPos(Vector pos) { this.aimPos = pos; }
    public void setAimPos(double x, double y) { this.aimPos = new Vector(x, y); }
    public Vector getAimPos() { return aimPos; }

    /** The desired zoom level of the camera. */
    private double aimScale = 1.0;
    public void setAimScale(double scale) { this.aimScale = scale; }
    public double getAimScale() { return aimScale; }
    
    /** 
     * The speed at which the camera glides to the desired values. It can vary from
     * 0.0 to 1.0 where 0.0 represents a fixed camera and 1.0 represents a camera that
     * reaches instantly its desired aim.
     */
    private double speed = 0.05;
    public void setSpeed(double speed) { this.speed = speed; }
    
    /**
     * If set to null, the camera can be controlled directly via `void setAimPos(Vector pos)`.
     * Otherwise, the camera will aim for the target on update.
     */
    private Sprite target = null;
    public Sprite getTarget() { return target; }
    public void setTarget(Sprite target) { this.target = target; }

    /** 
     * To be called at each animation frame. Updates the real values to approach
     * the aim values and follows target if defined.
     */
    public void update() {
        if (target != null) {
            aimPos = target.getPos().copy();
            System.out.println(aimPos);
            aimPos.setY(Math.min(155-338/scale, aimPos.getY()));
            aimPos.setY(Math.max(-580+338/scale, aimPos.getY()));
        }
        double coef = 1 - Math.pow((1-speed), scale);
        pos = pos.copy().scale(1-coef).add(aimPos.copy().scale(coef));
        scale = scale*(1-coef) + aimScale*coef;
    }

    /**
     * Applies the transform to the supplied graphics context to achieve the camera effect.  
     * @param gc The GraphicsContext object.
     */
    public void applyTransform(GraphicsContext gc) {
        double height = gc.getCanvas().getHeight();
        double width = gc.getCanvas().getWidth();
        gc.transform(new Affine(Transform.translate(width/2, height/2)));
        // gc.transform(new Affine(Transform.rotate(angle, 0, 0)));
        gc.transform(new Affine(Transform.translate(-pos.getX()*scale, -pos.getY()*scale)));
        gc.transform(new Affine(Transform.scale(scale, scale)));
    }
}