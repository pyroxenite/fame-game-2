import java.util.ArrayList;
import javafx.scene.Scene;

public class PlayerController extends PhysicsRectangle implements Updatable {
    private Game game;
    private Sprite target;
    private KeyHandler keyHandler;
    private int maxHealth = 10, currentHealth = 10;

    private boolean attacking, staggered = false;
    private int currentAttackAnim = 1;
    private int damage = 1;
    private double activeSpeed = 0.7;
    private ArrayList<Sprite> hitList = new ArrayList<Sprite>();

    public PlayerController(Game game, KeyHandler keyHandler, Sprite target) {
        super(0, 0, 18, 24);
        this.game = game;
        this.keyHandler = keyHandler;
        this.target = target; 
        this.target.setPos(this.pos);
    }

    public Sprite getTarget() { return target; }
    public void setTarget(Sprite target) { 
        this.target = target; 
    }

    public void incHealth(int inc) { 
        currentHealth = currentHealth + inc; 
        if (currentHealth > maxHealth) currentHealth = maxHealth;
        if (currentHealth < 0) currentHealth = 0;
    }

    public void resetHealth() {
        currentHealth = maxHealth;
    }

    public void knockBack(int magnitude, int dir) { 
        target.setImageSet("stagger");
        staggered = true;
        vel.add(dir * magnitude * 2, -magnitude / 5);
    }

    public int currentHealth() { return currentHealth; }
    public int maxHealth() { return maxHealth; }

    public boolean attacking() { return attacking; }

    public int damage() { return damage; }

    public boolean onHitList(Sprite mob) {
        if (hitList.contains(mob)) {
            return true;
        } else {
            hitList.add(mob);
            return false;
        }
    }

    public void update() {
        if (!attacking && !staggered) {
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
        }

        moveAccordingToKeysPressed();

        vel.scaleX(0.8);

        //reset attacking timer
        if (attacking && target.getCurrentFrameNumber() == target.getCurrentImageSetCount() - 1) {
            attacking = false; 
            hitList.clear();
        }

        if (staggered && target.getCurrentFrameNumber() == 2) {
            staggered = false;
        }

        if (currentHealth <= 0) {
            target.setImageSet("death");
            if (target.getCurrentImageSet().equals("death")) {
                System.out.println("This works");
                System.out.println(target.getCurrentFrameNumber());
                target.setImageSet("death-final");
            }
        }

        this.target.setPos(this.pos.copy().add(0, -5));
    }

    private void moveAccordingToKeysPressed() {
        if (!staggered) {
            if (!attacking) {
                if (keyHandler.isPressed("RIGHT") || keyHandler.isPressed("D")) {
                    vel.add(activeSpeed, 0);
                    target.setFlipped(false);
                }
        
                if (keyHandler.isPressed("LEFT") || keyHandler.isPressed("A")) {
                    vel.add(-activeSpeed, 0);
                    target.setFlipped(true);
                }
                
                if (keyHandler.isPressed("UP") || keyHandler.isPressed("W")) {
                    if (Math.abs(vel.getY()) < 0.01) {
                        vel.add(0, -5); 
                    }
                    keyHandler.preventRepeat("UP");
                }
                
                if (keyHandler.isPressed("DOWN") || keyHandler.isPressed("S")) {
                    // could be used to duck
                }
            }

            if (keyHandler.isPressed("K") || keyHandler.isPressed("Q")) {
                if (!attacking) {
                    target.setImageSet("attack" + currentAttackAnim);
                    attacking = true;
                    currentAttackAnim++;
                    if (currentAttackAnim > 3) currentAttackAnim = 1;
                }
            }
        }
    }
}
