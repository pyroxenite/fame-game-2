import java.util.ArrayList;
import javafx.scene.Scene;

public class PlayerController implements Updatable {
    private Game game;
    private Sprite target;
    private Vector vel = new Vector();
    private double activeSpeed = 1;
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
        moveAccordingToKeysPressed();

        vel.add(0, 0.15); // gravity
        vel.scaleX(1 - friction); // friction with ground

        target.getPos().add(vel);

        if (Math.abs(vel.getX()) > .1) {
            target.setImageSet("run");
        } else {
            target.setImageSet("idle");
        }

        if (Math.abs(vel.getY()) > 0.2) {
            if (vel.getY() < 0) {
                target.setImageSet("jump");
            } else {
                target.setImageSet("fall");
            }
        }

        if (target.getPos().getY() > 115) {
            target.getPos().setY(115);
            if (vel.getY() > 1)
                game.camera.hitGround(vel.getY()*5);
            vel.setY(0);
        }
    }

    private void moveAccordingToKeysPressed() {
        if (keyHandler.isPressed("RIGHT") || keyHandler.isPressed("D")) {
            vel.add(activeSpeed, 0);
            target.setFlipped(false);
        }

        if (keyHandler.isPressed("LEFT") || keyHandler.isPressed("A")) {
            vel.add(-activeSpeed, 0);
            target.setFlipped(true);
        }
        
        if (keyHandler.isPressed("UP") || keyHandler.isPressed("W")) {
            if (Math.abs(vel.getY()) < 0.1) {
                vel.add(0, -4); 
            }
            keyHandler.preventRepeat("UP");
        }
        
        if (keyHandler.isPressed("DOWN") || keyHandler.isPressed("S")) {
            // could be used to duck
        }
    }
}
