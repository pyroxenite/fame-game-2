import java.util.ArrayList;
import java.util.Hashtable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

/**
 * Implements a sprite: an object that holds one or more images and a position.
 * If multiple images are supplied, they are displayed in succession with a 
 * delay determined by the delta attribute.
 */
public class Sprite {
    private Hashtable<String, ArrayList<Image>> imageSets;
    private String currentImageSet;
    Hashtable<String, Double> deltas;
    private Vector pos = new Vector();
    private boolean animated = false;
    private boolean verticalFlip = false;
    private int currentFrameNumber = 0, yOffset = 0;
    private double lastFrameUpdateTime = 0;

    /**
     * Used to initialize a single image Sprite.
     * @param image The image of the Sprite
     */
    public Sprite(Image image) {
        this.imageSets = new Hashtable<String, ArrayList<Image>>();
        this.imageSets.put("idle", new ArrayList<>());
        this.imageSets.get("idle").add(image);
    }

    /**
     * Used to initialize an animated Sprite with a single image set.
     * @param images The image set
     * @param delta The time bewteen each image in the set
     */
    public Sprite(ArrayList<Image> images, double delta) {
        this.imageSets = new Hashtable<>();
        this.imageSets.put("idle", images);
        this.deltas = new Hashtable<>();
        this.deltas.put("idle", delta);
        animated = true;
    }

    /**
     * Used to initialize an animated Sprite with multiple image sets.
     * @param imageSets The image sets hashtable
     * @param animDeltas The time bewteen each image in each set
     * @param initSet The initial set
     */
    public Sprite(Hashtable<String, ArrayList<Image>> imageSets, Hashtable<String, Double> animDeltas, String initSet) {
        this.imageSets = imageSets;
        this.deltas = animDeltas;
        this.currentImageSet = initSet;
        animated = true;
    }

    public Vector getPos() { return pos; }

    public void setPos(Vector pos) {
        this.pos = pos;
    }
    public void setPos(double x, double y) {
        pos.setX(x);
        pos.setY(y);
    }

    public void setOffset(int o) { this.yOffset = o; }

    public int getCurrentFrameNumber() {
        return currentFrameNumber;
    }

    public void setFlipped(boolean verticalFlip) { this.verticalFlip = verticalFlip; } 

    public int getDirection() {
        return verticalFlip?-1:1;
    }

    public Image getCurrentImage(double time) {
        ArrayList<Image> currentImages = imageSets.get(currentImageSet);
        if (time - lastFrameUpdateTime > deltas.get(currentImageSet)) { 
            currentFrameNumber = (currentFrameNumber + 1) % currentImages.size();
            lastFrameUpdateTime = time;
        }

        return currentImages.get(currentFrameNumber);
    }

    public int getCurrentImageSetCount() { 
        return imageSets.get(currentImageSet).size(); 
    }

    public String getCurrentImageSet() {
        return currentImageSet;
    }

    public void setImageSet(String setName) { 
        if (currentImageSet != setName) {
            currentImageSet = setName; 
            currentFrameNumber = 0;
        }
    }

    public Rectangle2D getBoundary() { 
        ArrayList<Image> currentImages = imageSets.get(currentImageSet);
        Image currentImage = currentImages.get(currentFrameNumber);
        return new Rectangle2D(pos.getX(), pos.getY(), currentImage.getWidth(), currentImage.getHeight());
    }

    public boolean intersects(Sprite spr) {
        return spr.getBoundary().intersects(this.getBoundary());
    }

    public void draw(GraphicsContext gc, double t) {
        Image image;
        if (animated)
            image = getCurrentImage(t);
        else
            image = imageSets.get("idle").get(0);

        if (verticalFlip) {
            gc.drawImage(
                image, 
                pos.getX() - image.getWidth()/2 + image.getWidth(), 
                pos.getY() - image.getHeight()/2 - yOffset,
                -image.getWidth(),
                image.getHeight()
            );
        } else {
            gc.drawImage(
                image, 
                pos.getX() - image.getWidth()/2, 
                pos.getY() - image.getHeight()/2  - yOffset,
                image.getWidth(),
                image.getHeight()
            );
        }
    }
}
