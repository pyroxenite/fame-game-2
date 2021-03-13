import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Implements a sprite: an object that holds one or more images and a position.
 * If multiple images are supplied, they are displayed in succession with a 
 * delay determined by the duration attribute.
 */
public class Sprite {
    protected Image[] images;
    protected double duration;
    protected Vector pos = new Vector();
    protected boolean animated = false;

    public Sprite(Image image) {
        this.images = new Image[1];
        this.images[0] = image;
        this.duration = 1.0;
    }

    public Sprite(Image[] images, double duration) {
        this.images = images;
        this.duration = duration;
        animated = true;
    }

    public Vector getPos() { return pos; }

    public void setPos(double x, double y) {
        pos.setX(x);
        pos.setY(y);
    }

    public Image getCurrentImage(double time) {
        int index = (int) ((time % (images.length * duration)) / duration);
        return images[index];
    }

    public void draw(GraphicsContext gc, double t, Camera camera) {
        Image image = images[0];
        if (animated)
            image = getCurrentImage(t);

        gc.drawImage(
            image, 
            pos.getX() - image.getWidth()/2, 
            pos.getY() - image.getHeight()/2
        );
    }
}
