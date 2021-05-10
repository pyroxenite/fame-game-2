import java.util.ArrayList;
import javafx.scene.Scene;

public class PlayerController implements Updatable {
    private Game game;
    private Sprite target;
    private Vector vel = new Vector();
    private double activeSpeed = 1;
    private double friction = 0;
    private KeyHandler keyHandler;
    private int maxHealth = 5, currentHealth = 5;
    private boolean attacking, staggered = false;
    private int currentAttackAnim = 1;
    private int damage = 1;
    private ArrayList<Sprite> hitList = new ArrayList<Sprite>();


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

    public void incHealth(int inc) { 
        currentHealth = currentHealth + inc; 
        if (currentHealth > maxHealth) currentHealth = maxHealth;
        if (currentHealth < 0) currentHealth = 0;

        if (inc < 0) {

        }
    }

    public void knockBack(int magnitude, int dir) { 
        target.setImageSet("stagger");
        staggered = true;
        vel.add(dir * magnitude * 2, -magnitude / 5);
    }

    public int currentHealth() {return currentHealth;}
    public int maxHealth() {return maxHealth;}

    public boolean attacking() {return attacking;}

    public int damage() {return damage;}

    public boolean onHitList(Sprite mob) {
        if (hitList.contains(mob)) {
            return true;
        } else {
            hitList.add(mob);
            return false;
        }
    }

    public void update() {
        moveAccordingToKeysPressed();

        vel.add(0, 0.15); // gravity
        vel.scaleX(1 - friction); // friction with ground

        target.getPos().add(vel);

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

        if (target.getPos().getY() > 115) {
            target.getPos().setY(115);
            if (vel.getY() > 1)
                game.camera.hitGround(vel.getY()*5);
            vel.setY(0);
        }

         //reset attacking timer
        if (attacking && target.getCurrentFrameNumber() == target.getCurrentImageSetCount() - 1) {
            attacking = false; 
            hitList.clear();
        }

        if (staggered && target.getCurrentFrameNumber() == 2) {
            staggered = false;
        }
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
                    if (Math.abs(vel.getY()) < 0.1) {
                        vel.add(0, -4); 
                    }
                    keyHandler.preventRepeat("UP");
                }
                
                if (keyHandler.isPressed("DOWN") || keyHandler.isPressed("S")) {
                    // could be used to duck
                }
            }

            if (keyHandler.isPressed("K")) {
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
