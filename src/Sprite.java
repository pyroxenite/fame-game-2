import java.util.ArrayList;
import java.util.Hashtable;

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
    private boolean verticalFilp = false;

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
    // public Sprite(ArrayList<Image> images, double delta) {
    //     this.imageSets = new ArrayList<>();
    //     this.imageSets.add(images);
    //     this.delta = delta;
    //     animated = true;
    // }

    public Sprite(Hashtable<String, ArrayList<Image>> imageSet, Hashtable<String, Double> animDeltas, String initSet) {
        this.imageSets = imageSet;
        this.deltas = animDeltas;
        this.currentImageSet = initSet;
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

    public void setFlipped(boolean verticalFilp) { this.verticalFilp = verticalFilp; } 

    public Image getCurrentImage(double time) {
        ArrayList<Image> currentImages = imageSets.get(currentImageSet);
        int index = (int) (time/deltas.get(currentImageSet)) % currentImages.size();
        return currentImages.get(index);
    }

    public void setImageSet(String setName) { currentImageSet = setName; }

    public void draw(GraphicsContext gc, double t, Camera camera) {
        Image image;
        if (animated)
            image = getCurrentImage(t);
        else
            image = imageSets.get("idle").get(0);

        if (verticalFilp) {
            gc.drawImage(
                image, 
                pos.getX() - image.getWidth()/2 + image.getWidth(), 
                pos.getY() - image.getHeight()/2,
                -image.getWidth(),
                image.getHeight()
            );
        } else {
            gc.drawImage(
                image, 
                pos.getX() - image.getWidth()/2, 
                pos.getY() - image.getHeight()/2,
                image.getWidth(),
                image.getHeight()
            );
        }
    }
}
