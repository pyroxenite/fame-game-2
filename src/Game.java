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

    ArrayList<Sprite> sprites = new ArrayList<>();
    ArrayList<Updatable> updatables = new ArrayList<>();

    Sprite adventurer;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Game");
        stage.setScene(scene);

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

        // // background
        // ArrayList<Image> bgLayers = new ArrayList<>();
        // for (int i=1; i<=5; i++)
        //     bgLayers.add(new Image("images/bglayers2/layer" + i + ".png"));

        // LayeredSprite background = new LayeredSprite(bgLayers);
        // background.setPos(0, -110);    

        // // foreground
        // ArrayList<Image> fgLayers = new ArrayList<>();
        // for (int i=0; i<1; i++)
        //     fgLayers.add(new Image("images/bglayers2/layer" + i + ".png"));

        // LayeredSprite foreground = new LayeredSprite(fgLayers);
        // foreground.setPos(0, -110);
        // foreground.setDepth(-1);

        // background
        ArrayList<Image> bgLayers = new ArrayList<>();
        for (int i=1; i<=10; i++)
            bgLayers.add(new Image("images/bglayers/layer" + i + ".png"));

        LayeredSprite background = new LayeredSprite(bgLayers);
        background.setPos(0, -600);    

        // foreground
        ArrayList<Image> fgLayers = new ArrayList<>();
        for (int i=0; i<1; i++)
            fgLayers.add(new Image("images/bglayers/layer" + i + ".png"));

        LayeredSprite foreground = new LayeredSprite(fgLayers);
        foreground.setPos(0, -600);
        foreground.setDepth(-1);

        ArrayList<Image> idle = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            idle.add(new Image("images/adventurer/idle-0" + i + ".png"));

        ArrayList<Image> run = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            run.add(new Image("images/adventurer/run-0" + i + ".png"));

        ArrayList<Image> jump = new ArrayList<>();
        for (int i = 2; i < 3; i++) // using only one image to prevent ugly looping for now
            jump.add(new Image("images/adventurer/jump-0" + i + ".png"));

        ArrayList<Image> fall = new ArrayList<>();
        for (int i = 0; i < 2; i++)
            fall.add(new Image("images/adventurer/fall-0" + i + ".png"));

        Hashtable<String, ArrayList<Image>> adventurerAnims = new Hashtable<>();
        adventurerAnims.put("idle", idle);
        adventurerAnims.put("run", run);
        adventurerAnims.put("jump", jump);
        adventurerAnims.put("fall", fall);

        Hashtable<String, Double> adventurerAnimsDeltas = new Hashtable<>();
        adventurerAnimsDeltas.put("idle", .2);
        adventurerAnimsDeltas.put("run", .08);
        adventurerAnimsDeltas.put("jump", .1);
        adventurerAnimsDeltas.put("fall", .2);

        adventurer = new Sprite(adventurerAnims, adventurerAnimsDeltas, "idle");
        adventurer.setPos(0, 115);
        sprites.add(adventurer);


        //skeleton test
        ArrayList<Image> skeletonIdle = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            skeletonIdle.add(new Image("images/mobs/skeleton/idle/tile00" + i + ".png"));
        Hashtable<String, ArrayList<Image>> skeletonAnims = new Hashtable<>();
        Hashtable<String, Double> skeletonDeltas = new Hashtable<>();
        skeletonAnims.put("idle", idle);
        skeletonDeltas.put("idle", .2);
        Sprite skeletonSprite = new Sprite(skeletonAnims, skeletonDeltas, "idle");
        Mob skeleton = new Mob(true, 50, skeletonSprite);
        skeletonSprite.setPos(0, 115);
        updatables.add(skeleton);
        //end skeleton

        // controls
        PlayerController playerController = new PlayerController(this, keyHandler);
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

                // update all there is to update
                for (Updatable obj: updatables)
                    obj.update();

                // draw stuff
                background.draw(gc, t, camera);

                for (Sprite s: sprites)
                    s.draw(gc, t, camera);

                foreground.draw(gc, t, camera);

                drawBlackRects(gc);
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

    public Camera getCamera() { return camera; }
    public KeyHandler getKeyHandler() { return keyHandler; }
}
