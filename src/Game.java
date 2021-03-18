import java.util.ArrayList;
import java.util.Hashtable;

import javafx.application.Application;
import javafx.animation.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;

public class Game extends Application {
    Group root = new Group();
    Scene scene = new Scene(root);
    Canvas canvas;
    Camera camera = new Camera();
    KeyHandler keyHandler = new KeyHandler(scene);

    ArrayList<Sprite> sprites = new ArrayList<>();
    ArrayList<Updatable> updatables = new ArrayList<>();

    Sprite adventurer;

    public void start(Stage stage) {
        stage.setTitle("Game");
        stage.setScene(scene);

        int width = 1200;
        canvas = new Canvas(width, width/16*9);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gc.setImageSmoothing(false); // stops pixel-art from bluring

        // game controler
        GameController gameController = new GameController(this);
        updatables.add(gameController);

        // background
        ArrayList<Image> bgLayers = new ArrayList<>();
        for (int i=1; i<11; i++)
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

        // sprites
        // Sprite bc = new Sprite(new Image("images/briefcase.png"));
        // bc.setPos(300, 120);
        // sprites.add(bc);

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

        Hashtable<String, ArrayList<Image>> adventurerAnims = new Hashtable<String, ArrayList<Image>>();
        adventurerAnims.put("idle", idle);
        adventurerAnims.put("run", run);
        adventurerAnims.put("jump", jump);
        adventurerAnims.put("fall", fall);

        Hashtable<String, Double> adventurerAnimsDeltas = new Hashtable<String, Double>();
        adventurerAnimsDeltas.put("idle", .2);
        adventurerAnimsDeltas.put("run", .08);
        adventurerAnimsDeltas.put("jump", .1);
        adventurerAnimsDeltas.put("fall", .2);


        adventurer = new Sprite(adventurerAnims, adventurerAnimsDeltas, "idle");
        adventurer.setPos(400, 115);
        sprites.add(adventurer);

        // controls
        PlayerController playerController = new PlayerController(this, keyHandler);
        playerController.setTarget(adventurer);
        playerController.setActiveSpeed(0.75);
        playerController.setFriction(0.5);
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

                camera.applyTransform(gc);

                // update all there is to update
                for (Updatable obj: updatables)
                    obj.update();

                // draw stuff
                background.draw(gc, t, camera);

                for (Sprite s: sprites)
                    s.draw(gc, t, camera);

                foreground.draw(gc, t, camera);
            }
        }.start();

        stage.show();
    }

    private void clear() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setTransform(1, 0, 0, 1, 0, 0);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public Camera getCamera() { return camera; }
    public KeyHandler getKeyHandler() { return keyHandler; }
}
