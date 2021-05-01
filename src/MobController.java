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

    private Random rand = new Random();
    private int targX = 0;

    public MobController(Boolean hostile, int maxHealth, Sprite sprite, Sprite adventurer) {
        this.hostile = hostile;
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
        this.target = sprite;
        this.adventurer = adventurer;
    }

    //FSM state and transition implementation
    public enum BehaviorState {
        PATROL {
            @Override
            public BehaviorState nextState(Vector charPos, Vector mobPos) { 
                double dist = Math.abs(charPos.getX() - mobPos.getX());
                if (dist < detectionRange)
                    return BehaviorState.CHASE;
                else 
                    return BehaviorState.PATROL;
            }
        },
        CHASE {
            @Override
            public BehaviorState nextState(Vector charPos, Vector mobPos) { 
                double dist = Math.abs(charPos.getX() - mobPos.getX());
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
            public BehaviorState nextState(Vector charPos, Vector mobPos) { 
                double dist = Math.abs(charPos.getX() - mobPos.getX());
                if (dist < attackRange)
                    return BehaviorState.ATTACK;
                else
                    return BehaviorState.CHASE;
            }
        };

        public abstract BehaviorState nextState(Vector charPos, Vector mobPos);
    }

    private BehaviorState currBehaviorState = BehaviorState.PATROL;

    public void update() {
        vel.add(0, 0.15); // gravity
        vel.scaleX(1 - friction); // friction with ground
        target.getPos().add(vel);

        //keep entity above ground
        if (target.getPos().getY() > 115) {
            target.getPos().setY(115);
            vel.setY(0);
        }

        if (vel.getX() > .01) {
            target.setFlipped(false);
            target.setImageSet("walk");
        } else if (vel.getX() < -.01) {
            target.setFlipped(true);
            target.setImageSet("walk");
        } else {
            target.setImageSet("idle");
        }

        //freq used vars for fsm functions
        double newPlayerX = adventurer.getPos().getX();
        double newTargetX = target.getPos().getX();
        int dir = 1;
        switch(currBehaviorState) {
            case PATROL: //randomly move left or right
                if (Math.abs(targX - newTargetX) < 1)
                    targX = targX + (rand.nextDouble() < .5 ? 100 : -100);
                dir = newTargetX > targX ? -1 : 1;
                vel.setX(dir * moveSpeed);
                break;
            case CHASE: //move towards player
                dir = newTargetX > newPlayerX ? -1 : 1;
                vel.setX(dir * moveSpeed);
                break;
            case ATTACK: //stop movement
                vel.setX(0);
                break;
            default:
                break;
        }

        //advance FSM to next state
        currBehaviorState = currBehaviorState.nextState(adventurer.getPos(), target.getPos());
    }

    public void setSprite(Sprite s) { this.target = s; }

    public Sprite getSprite() { return target; }
}
