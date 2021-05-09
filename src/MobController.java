import java.util.*;
import java.util.Random;

public class MobController implements Updatable {

    private Sprite target;
    private Sprite adventurer;
    private Vector vel = new Vector();
    private Vector acc = new Vector();
    private Boolean hostile;
    private int maxHealth;
    private int currentHealth;
    private double moveSpeed = 0.6;
    private double friction = 0;
    private static double detectionRange = 150;
    private static double attackRange = 25;
    private static Game game;
    private boolean hasHitPlayer, hasPlayerHit;

    private Random rand = new Random();
    private int targX = 0;

    public MobController(Game game) {
        this.game = game;
        this.hostile = false;
        this.maxHealth = 50;
        this.currentHealth = maxHealth;
        this.target = null;
        this.adventurer = game.adventurer;
    }

    public void setHostile(boolean isHostile) {
        this.hostile = isHostile;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setTarget(Sprite target) {
        this.target = target;
    }

    //FSM state and transition implementation
    public enum BehaviorState {
        PATROL {
            @Override
            public BehaviorState nextState(Sprite mob, Sprite player) { 
                double dist = Math.abs(player.getPos().getX() - mob.getPos().getX());
                if (dist < detectionRange)
                    return BehaviorState.CHASE;
                else 
                    return BehaviorState.PATROL;
            }
        },
        CHASE {
            @Override
            public BehaviorState nextState(Sprite mob, Sprite player) { 
                double dist = Math.abs(player.getPos().getX() - mob.getPos().getX());
                if (dist < attackRange)
                    return BehaviorState.ATTACK;
                else if (dist > detectionRange)
                    return BehaviorState.PATROL;
                else
                    return BehaviorState.CHASE;
            }
        },
        ATTACK {
            @Override
            public BehaviorState nextState(Sprite mob, Sprite player) { 
                double dist = Math.abs(player.getPos().getX() - mob.getPos().getX());
                if (dist < attackRange || mob.getCurrentFrameNumber() != 17)
                    return BehaviorState.ATTACK;
                else
                    return BehaviorState.CHASE;
            }
        };

        public abstract BehaviorState nextState(Sprite mob, Sprite player);
    }

    private BehaviorState currBehaviorState = BehaviorState.PATROL;
    private BehaviorState prevBehaviorState = currBehaviorState;

    public void update() {
        vel.add(0, 0.15); // gravity
        vel.scaleX(1 - friction); // friction with ground
        target.getPos().add(vel);

        //keep entity above ground
        if (target.getPos().getY() > 115) {
            target.getPos().setY(115);
            if (vel.getY() > 1)
                game.camera.hitGround(vel.getY()*5);
            vel.setY(0);
        }

        if (vel.getX() > .01) {
            target.setFlipped(false);
            
        } else if (vel.getX() < -.01) {
            target.setFlipped(true);
        } else {
            
        }

        currBehaviorState = currBehaviorState.nextState(target, adventurer);

        //freq used vars for fsm functions
        double newPlayerX = adventurer.getPos().getX();
        double newTargetX = target.getPos().getX();
        int dir = 1;
        switch (currBehaviorState) {
            case PATROL: //randomly move left or right
                target.setImageSet("walk");
                if (Math.abs(targX - newTargetX) < 1)
                    targX = targX + (rand.nextDouble() < .5 ? 100 : -100);
                dir = newTargetX > targX ? -1 : 1;
                vel.setX(dir * moveSpeed);
                break;
            case CHASE: //move towards player
                target.setImageSet("run");
                dir = newTargetX > newPlayerX ? -2 : 2;
                vel.setX(dir * moveSpeed);
                break;
            case ATTACK: //stop movement
                if (prevBehaviorState != BehaviorState.ATTACK) hasHitPlayer = false; //reset has hit player flag on state change
                vel.setX(0);
                target.setImageSet("attack");
                if (target.getCurrentFrameNumber() == 1) hasHitPlayer = false; //reset hit player flag on new anim cycle
                if (target.getCurrentFrameNumber() >= 8) { //hit frames
                    if (!hasHitPlayer && target.intersects(adventurer)) { //has hit
                        game.camera.hitSide(10 * target.getDirection());
                        game.playerController.incHealth(-1);
                        game.playerController.knockBack(10, (int) -Math.signum(target.getPos().getX() - adventurer.getPos().getX()));
                        hasHitPlayer = true; //set hit player flag
                    }
                }
                break;
            default:
                target.setImageSet("idle");
                break;
        }

        //advance FSM to next state
        prevBehaviorState = currBehaviorState;
    }

    public void setSprite(Sprite s) { this.target = s; }

    public Sprite getSprite() { return target; }
}

