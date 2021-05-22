import java.util.ArrayList;

public class PhysicsWorld {
  private ArrayList<Rectangle> rects = new ArrayList<>();

  public PhysicsWorld() {

  }


  
  
  public PhysicsWorld add(Rectangle rect) {
    this.rects.add(rect);
    return this;
  }
  
  public void draw() {
    this.rects.forEach(r -> r.draw());
  }
  
  public void update() {
    for (var i=0; i<rects.size(); i++) {
      Rectangle r = rects.get(i);
      if (!r.isFixed() && i != 0) {
        r.getNextVel().setY(r.getVel().getY() + 0.4); // gravity
        r.getNextVel().scale(0.98); // friction
        r.getNextPos().add(r.getVel());
      }
    }
    
    for (var i=0; i<10; i++) {
      rects.forEach(r1 -> {
        rects.forEach(r2 -> {
          if (r1 != r2)
            seperate(r1, r2);
        });
      });
    }
    
    rects.forEach(r -> r.nextFrame());
  }
  
  private void seperate(Rectangle r1, Rectangle r2) {
    double left =  r2.getPos().getX() - r2.getWidth()/2 - r1.getPos().getX() - r1.getWidth()/2;
    double right = r1.getPos().getX() - r1.getWidth()/2 - r2.getPos().getX() - r2.getWidth()/2;
    double top =  r2.getPos().getY() - r2.getHeight()/2 - r1.getPos().getY() - r1.getHeight()/2;
    double bottom = r1.getPos().getY() - r1.getHeight()/2 - r2.getPos().getY() - r2.getHeight()/2;
    
    boolean overlapping = (top < 0) && (right < 0) && (bottom < 0) && (left < 0);
    
    if (overlapping && !r1.isFixed()) {
      double condition = r1.isFixed()?1:0;
      if (left > right && left > top && left > bottom) {
        r1.getNextPos().setX(r1.getPos().getX() + left/2 + condition*left/2);
        r1.getNextVel().setX(-(r1.getVel().getX()-r2.getVel().getX())*0.2 + r2.getVel().getX());
      } else if (right > top && right > bottom) {
        r1.getNextPos().setX(r1.getPos().getX() - right/2 - condition*right/2);
        r1.getNextVel().setX(-(r1.getVel().getX()-r2.getVel().getX())*0.2 - r2.getVel().getX()*0.6);
      } else if (top > bottom) {
        r1.getNextPos().setY(r1.getPos().getY() + top/2 + condition*top/2 - 0.01);
        r1.getNextVel().setY(-(r2.getVel().getY()-r1.getVel().getY())*0.4);
      } else {
        r1.getNextPos().setY(r1.getPos().getY() - bottom/2 - condition*bottom/2 + 0.01);
        r1.getNextVel().setY(-(r2.getVel().getY()-r1.getVel().getY())*0.4);
      }
    }
  }
}