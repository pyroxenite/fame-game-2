import java.util.*;
import javafx.scene.Scene;
import javafx.scene.image.Image;

public class Mob implements Updatable {

    private Sprite target;
    private Vector vel = new Vector();
    private Vector acc = new Vector();
    private Boolean hostile;
    private int maxHealth;
    private int currentHealth;
    private double friction = 0;

    // private Hashtable<String, ArrayList<Image>> imageSets;

    public Mob(Boolean hostile, int maxHealth, Sprite sprite) {
        this.hostile = hostile;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.target = sprite;
    }

    public void update() {
        vel.add(0, 0.15); // gravity
        vel.scaleX(1 - friction); // friction with ground
        target.getPos().add(vel);

        if (target.getPos().getY() > 115) {
            target.getPos().setY(115);
            vel.setY(0);
        }
    }

    public void setSprite(Sprite s) { this.target = s; }

    public Sprite getSprite() { return target; }
}

