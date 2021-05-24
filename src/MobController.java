import java.util.*;
import java.util.Random;

public class MobController extends PhysicsRectangle implements Updatable {

    private Sprite target;
    private Sprite adventurer;
    private Boolean hostile = true;
    private int maxHealth;
    private int currentHealth;
    private double moveSpeed = 0.4;
    private double friction = .05;
    private static double detectionRange = 150;
    private static double attackRange = 25;
    private Game game;
    private boolean hasHitPlayer, staggered;

    private int dmgFrameEnd, dmgFrameStart;

    private Random rand = new Random();
    private int targX = 0;

    public MobController(Game game, Sprite target) {
        super(0, 0, 19, 32);
        this.game = game;
        this.hostile = false;
        this.maxHealth = 50;
        this.currentHealth = maxHealth;
        this.adventurer = game.adventurer;
        this.target = target;
    }

    public void setHostile(boolean isHostile) {
        this.hostile = isHostile;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setMoveSpeed(double speed) {
        this.moveSpeed = speed;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setTarget(Sprite target) {
        this.target = target;
    }

    public void setDmgFrames(int start, int end) {
        this.dmgFrameStart = start;
        this.dmgFrameEnd = end;
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
                if (dist < attackRange)
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
        if (staggered && target.getCurrentFrameNumber() == 5) staggered = false;

        //handle hit on mob from player
        if (
            game.playerController.attacking() && 
            target.intersects(adventurer) && 
            adventurer.getCurrentFrameNumber() >= 3 &&
            !game.playerController.onHitList(target)
        ) {
            int dir = (int) Math.signum(target.getPos().getX() - adventurer.getPos().getX());
            int magnitude = 1;
            currentHealth -= game.playerController.damage();
            vel.add(dir * magnitude * 2, -magnitude);
            staggered = true;
            target.setImageSet("stagger");
        }

        this.target.setPos(this.pos);

        if (staggered)
            return;


        if (vel.getX() > .5) {
            target.setFlipped(false);
        } else if (vel.getX() < -.5) {
            target.setFlipped(true);
        }

        currBehaviorState = currBehaviorState.nextState(target, adventurer);

        //freq used vars for fsm functions
        double newPlayerX = adventurer.getPos().getX();
        double newTargetX = target.getPos().getX();
        int dir = 1;
        switch (currBehaviorState) {
            case PATROL: //randomly move left or right
                if (Math.abs(vel.getX()) > .1) {
                    target.setImageSet("walk");
                } else {
                    target.setImageSet("idle");
                }
                if (Math.abs(targX - newTargetX) < 1)
                    targX = targX + (rand.nextDouble() < .5 ? 100 : -100);
                dir = newTargetX > targX ? -1 : 1;
                vel.setX(dir * moveSpeed);
                break;
            case CHASE: //move towards player
                if (Math.abs(vel.getX()) > .1) {
                    target.setImageSet("run");
                } else {
                    target.setImageSet("idle");
                }
                dir = newTargetX > newPlayerX ? -2 : 2;
                vel.setX(dir * moveSpeed);
                break;
            case ATTACK: //stop movement
                if (prevBehaviorState != BehaviorState.ATTACK) hasHitPlayer = false; //reset has hit player flag on state change
                vel.setX(0);
                target.setImageSet("attack");
                if (target.getCurrentFrameNumber() == 1) hasHitPlayer = false; //reset hit player flag on new anim cycle
                if (target.getCurrentFrameNumber() >= dmgFrameStart && target.getCurrentFrameNumber() < dmgFrameEnd) { //hit frames
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

