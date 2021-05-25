import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;

public class PhysicsWorld implements Updatable {
  private ArrayList<PhysicsRectangle> rects = new ArrayList<>();

  public PhysicsWorld add(PhysicsRectangle rect) {
    this.rects.add(rect);
    return this;
  }

  public PhysicsWorld remove(PhysicsRectangle rect) {
    this.rects.remove(rect);
    return this;
  }

  public PhysicsWorld clear() {
    this.rects.clear();
    return this;
  }

  public void draw(GraphicsContext gc) {
    this.rects.forEach(r -> r.draw(gc));
  }

  public void update() {
    for (var i = 0; i < rects.size(); i++) {
      PhysicsRectangle r = rects.get(i);
      if (!r.isFixed() && i != 0) {
        r.getNextVel().setY(r.getVel().getY() + 0.2); // gravity
        r.getNextVel().scale(0.98); // friction
        r.getNextPos().add(r.getVel());
      }
    }

    for (var i = 0; i < 10; i++) {
      rects.forEach(r1 -> {
        rects.forEach(r2 -> {
          if (r1 != r2)
            seperate(r1, r2);
        });
      });
    }

    rects.forEach(r -> r.nextFrame());
  }

  private void seperate(PhysicsRectangle r1, PhysicsRectangle r2) {
    double left = r2.getPos().getX() - r2.getWidth() / 2 - r1.getPos().getX() - r1.getWidth() / 2;
    double right = r1.getPos().getX() - r1.getWidth() / 2 - r2.getPos().getX() - r2.getWidth() / 2;
    double top = r2.getPos().getY() - r2.getHeight() / 2 - r1.getPos().getY() - r1.getHeight() / 2;
    double bottom = r1.getPos().getY() - r1.getHeight() / 2 - r2.getPos().getY() - r2.getHeight() / 2;

    boolean overlapping = (top < 0) && (right < 0) && (bottom < 0) && (left < 0);

    if (overlapping && !r1.isFixed()) {
      double cond = r1.isFixed() ? 1 : 0;
      var relativeSpeed = r1.getVel().copy().sub(r2.getVel()).scale(0.9);
      if (left > right && left > top && left > bottom) {
        r1.getNextPos().setX(r1.getPos().getX() + left / 2 + cond * left / 2);
        if (r1.getIsEntity()) {
          r2.getNextVel().setX(r1.getNextVel().getX()*1.001);
          r1.getNextVel().setX(0);
        }
      } else if (right > top && right > bottom) {
        r1.getNextPos().setX(r1.getPos().getX() - right / 2 - cond * right / 2);
        if (r1.getIsEntity()) {
          r2.getNextVel().setX(r1.getNextVel().getX()*1.001);
          r1.getNextVel().setX(0);
        }
      } else if (top > bottom) {
        r1.getNextPos().setY(r1.getPos().getY() + top / 2 + cond * top / 2 - 0.01);
        r1.getNextVel().setY(r1.getVel().getY() * 0.4);
      } else {
        r1.getNextPos().setY(r1.getPos().getY() - bottom / 2 - cond * bottom / 2 + 0.01);
        r1.getNextVel().setY(r1.getVel().getY() * 0.4);
      }
    }
  }
}