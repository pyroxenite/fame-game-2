import java.util.ArrayList;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

class ParallaxSprite {
    protected ArrayList<Image> layers;
    protected int depth = 0;
    protected Vector pos = new Vector();
    
    public ParallaxSprite(ArrayList<Image> layers) {
        this.layers = layers;
    }
    
    public Vector getPos() { return pos; }
    
    public void setPos(double x, double y) {
        pos.setX(x);
        pos.setY(y);
    }
    
    public int getDepth() { return depth; }
    public void setDepth(int depth) { this.depth = depth; }

    public void draw(GraphicsContext gc, Camera camera) {
        if (layers.size() > 0) {
            double width = layers.get(0).getWidth();
            double viewWidth = 1200 / camera.getScale();
            for (int i=layers.size()-1; i>=0; i--) {
                double x = pos.getX() + camera.getX()*(i+depth)/10;
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
}