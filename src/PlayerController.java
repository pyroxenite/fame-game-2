import java.util.ArrayList;
import javafx.scene.Scene;

public class PlayerController implements Updatable {
    private Game game;
    private Sprite target;
    private Vector vel = new Vector();
    private double activeSpeed = 2;
    private double friction = 0;
    private KeyHandler keyHandler;

    public PlayerController(Game game, KeyHandler keyHandler) {
        this.game = game;
        this.keyHandler = keyHandler;
    }

    public Sprite getTarget() { return target; }
    public void setTarget(Sprite target) { this.target = target; }

    public double getActiveSpeed() { return activeSpeed; }
    public void setActiveSpeed(double speed) { this.activeSpeed = speed; }

    public Vector getVel() { return vel; }
    public void setVel(Vector vel) { this.vel = vel; }

    public double getFriction() { return friction; }

    public void setFriction(double friction) { this.friction = friction; }

    public void update() {
        if (keyHandler.isPressed("RIGHT") || keyHandler.isPressed("D")) {
            vel.add(new Vector(activeSpeed, 0));
            target.setFlipped(false);
        }

        if (keyHandler.isPressed("LEFT") || keyHandler.isPressed("A")) {
            vel.add(new Vector(-activeSpeed, 0));
            target.setFlipped(true);
        }
        
        if (keyHandler.isPressed("UP") || keyHandler.isPressed("W")) {
            if (Math.abs(vel.getY()) < 0.1) {
                vel.add(new Vector(0, -3)); 
            }
            keyHandler.preventRepeat("UP");
        }
        
        // if (keyHandler.isPressed("DOWN") || keyHandler.isPressed("S")) {
        //     vel.add(new Vector(0, activeSpeed));
        //     keyHandler.preventRepeat("DOWN");
        // }

        target.getPos().add(vel);
        vel.scaleX(1 - friction);

        vel.add(new Vector(0, 0.2));

        if (target.getPos().getY() > 115) {
            target.getPos().setY(115);
            vel.setY(0);
        }
    }
}
