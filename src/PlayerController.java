import java.util.ArrayList;
import javafx.scene.Scene;

public class PlayerController implements Updatable {
    private ArrayList<String> input = new ArrayList<>();
    private Sprite target;
    private Vector vel = new Vector();
    private double activeSpeed = 2;
    private double friction = 0;

    public PlayerController(Scene scene, Sprite target) {
        this.target = target;

        scene.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (!input.contains(code))
                input.add(code);
        });

        scene.setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            input.remove(code);
        });
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
        if (input.contains("RIGHT"))
            vel.add(new Vector(activeSpeed, 0));

        if (input.contains("LEFT"))
            vel.add(new Vector(-activeSpeed, 0));

        if (input.contains("DOWN"))
            vel.add(new Vector(0, activeSpeed));

        if (input.contains("UP"))
            vel.add(new Vector(0, -activeSpeed));

        target.getPos().add(vel);
        vel.scale(1 - friction);
    }
}
