import java.util.ArrayList;
import java.util.Hashtable;

import javafx.application.Application;
import javafx.animation.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

public class Game extends Application {
    StackPane root = new StackPane();
    Scene scene = new Scene(root);
    Canvas canvas;
    Camera camera = new Camera();
    KeyHandler keyHandler = new KeyHandler(scene);

    SoundBackground bgSound;

    ArrayList<Sprite> sprites = new ArrayList<>();
    ArrayList<Updatable> updatables = new ArrayList<>();

    Sprite adventurer;
    Sprite skeletonSprite;

    public PlayerController playerController;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Game");
        stage.setScene(scene);

        stage.setHeight(900);
        stage.setWidth(1600);

        canvas = new Canvas();
        root.getChildren().add(canvas);
 
        // Bind canvas size to stack pane size.
        canvas.widthProperty().bind(root.widthProperty());
        canvas.heightProperty().bind(root.heightProperty());

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false); // stops pixel-art from bluring

        // game controler
        GameController gameController = new GameController(this);
        updatables.add(gameController);

        // background sound
        bgSound = new SoundBackground();
        bgSound.run();
        
        // // background
        // ArrayList<Image> bgLayers = new ArrayList<>();
        // for (int i=1; i<=5; i++)
        //     bgLayers.add(new Image("images/bglayers2/layer" + i + ".png"));

        // ParallaxSprite background = new ParallaxSprite(bgLayers);
        // background.setPos(0, -110);    

        // // foreground
        // ArrayList<Image> fgLayers = new ArrayList<>();
        // for (int i=0; i<1; i++)
        //     fgLayers.add(new Image("images/bglayers2/layer" + i + ".png"));

        // ParallaxSprite foreground = new ParallaxSprite(fgLayers);
        // foreground.setPos(0, -110);
        // foreground.setDepth(-1);

        // background
        ArrayList<Image> bgLayers = new ArrayList<>();
        for (int i=1; i<=10; i++)
            bgLayers.add(new Image("images/bglayers/layer" + i + ".png"));

        ParallaxSprite background = new ParallaxSprite(bgLayers);
        background.setPos(0, -600);    

        // foreground
        ArrayList<Image> fgLayers = new ArrayList<>();
        for (int i=0; i<1; i++)
            fgLayers.add(new Image("images/bglayers/layer" + i + ".png"));

        ParallaxSprite foreground = new ParallaxSprite(fgLayers);
        foreground.setPos(0, -600);
        foreground.setDepth(-1);

        //player
        adventurer = new AnimationLoader().loadAnimation("adventurer");
        adventurer.setPos(0, 115);
        sprites.add(adventurer);
        //end player

        //skeleton
        skeletonSprite = new AnimationLoader().loadAnimation("skeleton");
        skeletonSprite.setPos(0, 115 - 100);
        sprites.add(skeletonSprite);
        MobController skeleton = new MobController(this);
        skeleton.setTarget(skeletonSprite);
        skeleton.setMaxHealth(50);
        skeleton.setHostile(true);
        updatables.add(skeleton);
        //end skeleton
        
        {
        Sprite mushroom1 = new Sprite(new Image("images/mushrooms/type1-single-1.png"));
        mushroom1.setPos(100, 130);
        sprites.add(mushroom1);

        Sprite mushroom2 = new Sprite(new Image("images/mushrooms/type1-double-1.png"));
        mushroom2.setPos(300, 130);
        sprites.add(mushroom2);

        Sprite mushroom3 = new Sprite(new Image("images/mushrooms/type1-single-2.png"));
        mushroom3.setPos(400, 130);
        sprites.add(mushroom3);

        Sprite mushroom4 = new Sprite(new Image("images/mushrooms/type1-triple-1.png"));
        mushroom4.setPos(700, 130);
        sprites.add(mushroom4);

        Sprite mushroom5 = new Sprite(new Image("images/mushrooms/type1-single-3.png"));
        mushroom5.setPos(900, 130);
        sprites.add(mushroom5);
        }


        Sprite platformTest = new Sprite(new Image("images/platform.png"));
        platformTest.setPos(200, 50);
        sprites.add(platformTest);

        // controls
        playerController = new PlayerController(this, keyHandler);
        playerController.setTarget(adventurer);
        playerController.setActiveSpeed(0.8);
        playerController.setFriction(0.3);
        // playerController.setFriction(0.05);
        updatables.add(playerController);
        
        // camera
        camera.setScale(3);
        camera.setSpeed(0.05);
        camera.setTarget(adventurer);
        camera.setPos(adventurer.getPos());
        updatables.add(camera);

        final long startNanoTime = System.nanoTime();

        // GAMELOOP
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                double t = (currentNanoTime - startNanoTime) / 1e9;
                clear();

                adjustCanvasToWindowSize(gc);
                camera.applyTransform(gc);

                for (Updatable obj: updatables) {
                    obj.update();
                }

                background.draw(gc, t, camera);

                for (Sprite s: sprites) {
                    s.draw(gc, t, camera);
                }

                foreground.draw(gc, t, camera);

                drawBlackRects(gc);
                drawHealth(gc);
            }
        }.start();

        stage.show();
    }

    private void clear() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void adjustCanvasToWindowSize(GraphicsContext gc) {
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        gc.transform(new Affine(Transform.translate(width/2, height/2))); // center origin
        double scale = Math.min(width/1200, height/675);
        gc.transform(new Affine(Transform.scale(scale, scale))); // scale according to window size
    }

    private void drawBlackRects(GraphicsContext gc) {
        gc.setTransform(1, 0, 0, 1, 0, 0);
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        gc.setFill(Color.BLACK);
        if (width/1200 < height/675) {
            gc.fillRect(0, 0, width, (height-width/16*9)/2);
            gc.fillRect(0, height-(height-width/16*9)/2, width, (height-width/16*9)/2);
        } else {
            gc.fillRect(0, 0, (width-height/9*16)/2, height);
            gc.fillRect(width-(width-height/9*16)/2, 0, (width-height/9*16)/2, height);
        }
    }

    public void drawHealth(GraphicsContext gc) {
        for (int i = 0; i < playerController.maxHealth(); i++) {
            final int HEART_SIZE = 50;
            Image heart = new Image("images/ui/heart" + (i + 1 <= playerController.currentHealth() ? "full" : "empty") + ".png");
            gc.drawImage(heart, i * HEART_SIZE * 1.25 + 30, 10, HEART_SIZE, HEART_SIZE);
        }
    }

    public Camera getCamera() { return camera; }
    public KeyHandler getKeyHandler() { return keyHandler; }
}
