import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class LayeredSprite {
    protected ArrayList<Image> layers;
    protected int depth = 0;
    protected Vector pos = new Vector();
    
    public LayeredSprite(ArrayList<Image> layers) {
        this.layers = layers;
    }
    
    public Vector getPos() { return pos; }
    
    public void setPos(double x, double y) {
        pos.setX(x);
        pos.setY(y);
    }
    
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public void draw(GraphicsContext gc, double t, Camera camera) {
        double width = layers.get(0).getWidth();
        double viewWidth = 1200;
        for (int i=layers.size()-1; i>=0; i--) {
            double x = pos.getX() + camera.getX()*(i+depth)/12;
            int shift = (int) ((camera.getX() + viewWidth/2 - x) / width);

            if (camera.getX() < 0) 
                gc.drawImage(
                    layers.get(i), 
                    x + width*(shift - 2), 
                    pos.getY()
                );

            gc.drawImage(
                layers.get(i), 
                x + width*(shift - 1), 
                pos.getY()
            );

            gc.drawImage(
                layers.get(i), 
                x + width*shift, 
                pos.getY()
            );
        }
    }
}