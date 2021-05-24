import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
import javafx.application.Application;
import java.util.ArrayList;

public class GameController implements Updatable {
    Game game;
    KeyHandler keyHandler;

    boolean loweringCamera = false;
    int fade = 0;
    double alpha = 0;
    int delayCounterMax = 100;
    int delayCounter = -1;
    int curentLevel = 1;

    String newLevelName;

    public GameController(Game game) {
        this.game = game;
        keyHandler = game.getKeyHandler();
    }

    public void update() {
        if (keyHandler.isPressed("H")) {
            game.getCamera().hitLeft(40);
            keyHandler.preventRepeat("H");
        }
        if (keyHandler.isPressed("L")) {
            lowerCameraIntoForest();
        }

        if (keyHandler.isPressed("U")) {
            fade = 1;
            curentLevel = curentLevel%3 + 1;
            newLevelName = "Level " + curentLevel;
        }

        if (loweringCamera) {
            Camera camera = game.getCamera();
            camera.getAimPos().add(new Vector(0, 0.8));

            if (camera.getPos().getY() > 65) {
                loweringCamera = false;
                camera.setSpeed(0.05);
                camera.setAimScale(3);
            } else if (camera.getPos().getY() > -10) {
                camera.setTarget(game.adventurer);
            }
        }
    }

    public void draw(GraphicsContext gc) {
        if (fade != 0) {
            gc.setTransform(1, 0, 0, 1, 0, 0);
            double width = gc.getCanvas().getWidth();
            double height = gc.getCanvas().getHeight();
            alpha = alpha > 1 ? 1 : alpha;
            alpha = alpha < 0 ? 0 : alpha;
            gc.setFill(new Color(0, 0, 0, alpha));
            gc.fillRect(0, 0, width, height);
            
            if (delayCounter < 0) {
                if (fade == 1)    
                    alpha += .01;
                else {
                    alpha += -.01;
                }
                alpha = alpha > 1 ? 1 : alpha;
                alpha = alpha < 0 ? 0 : alpha;
                if (alpha == 1) {
                    fade = -1;
                    delayCounter = 0;
                    alpha += .01;
                }
                if (alpha == 0) {
                    fade = 0;
                    delayCounter = -1;
                }
            } else {
                delayCounter++;
                if (delayCounter == delayCounterMax / 2)  {
                    ArrayList<ParallaxSprite> backgrounds = (new SpriteLoader()).loadBackground(newLevelName);
                    game.background = backgrounds.get(0);
                    game.foreground = backgrounds.get(1); 
                    game.foreground.setDepth(-1);
                } else if (delayCounter == delayCounterMax) {
                    delayCounter = -1;
                }
            }
        }
    }

    public void changeLevels(String levelName) {
        fade = 1;
        newLevelName = levelName;
    }

    public void lowerCameraIntoForest() {
        loweringCamera = true;
        Camera camera = game.getCamera();
        camera.setTarget(null);
        camera.setPos(game.adventurer.getPos().getX(), -480);
        // camera.setPos(game.adventurer.getPos().getX(), -100);
        camera.setSpeed(0.002);
        camera.setScale(5);
    }
}
