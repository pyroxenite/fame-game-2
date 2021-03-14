import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Implements a sprite: an object that holds one or more images and a position.
 * If multiple images are supplied, they are displayed in succession with a 
 * delay determined by the delta attribute.
 */
public class Sprite {
    protected ArrayList<ArrayList<Image>> imageSets;
    protected double delta;
    protected Vector pos = new Vector();
    protected boolean animated = false;
    protected int currentImageSet = 0;

    /**
     * Used to initialize a single image Sprite.
     * @param image The image of the Sprite
     */
    public Sprite(Image image) {
        this.imageSets = new ArrayList<>();
        this.imageSets.add(new ArrayList<>());
        this.imageSets.get(0).add(image);
        this.delta = 1.0;
    }

    /**
     * Used to initialize an animated Sprite with a single image set.
     * @param images The image set
     * @param delta The time bewteen each image in the set
     */
    public Sprite(ArrayList<Image> images, double delta) {
        this.imageSets = new ArrayList<>();
        this.imageSets.add(images);
        this.delta = delta;
        animated = true;
    }

    // public Sprite(ArrayList<ArrayList<Image>> imageSets, double delta) {
    //     this.imageSets = imageSets;
    //     this.delta = delta;
    //     animated = true;
    // }

    public Vector getPos() { return pos; }

    public void setPos(double x, double y) {
        pos.setX(x);
        pos.setY(y);
    }

    public Image getCurrentImage(double time) {
        ArrayList<Image> currentImages = imageSets.get(currentImageSet);
        int index = (int) (time/delta) % (currentImages.size());
        return currentImages.get(index);
    }

    public void draw(GraphicsContext gc, double t, Camera camera) {
        Image image = imageSets.get(0).get(0);
        if (animated)
            image = getCurrentImage(t);

        gc.drawImage(
            image, 
            pos.getX() - image.getWidth()/2, 
            pos.getY() - image.getHeight()/2
        );
    }
}
