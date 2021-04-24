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
    Sprite skeletonSprite;

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

        //bg sound
        SoundBackground bgSound = new SoundBackground();
        bgSound.run();
        
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
        for (int i = 0; i < 11; i++)
            skeletonIdle.add(new Image("images/mobs/skeleton/idle/idle" + i + ".png"));
        ArrayList<Image> skeletonWalk = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            String index = "" + i;
            if (i < 10) index = "0" + index;
            skeletonWalk.add(new Image("images/mobs/skeleton/walk/tile0" + index + ".png"));
        }
        Hashtable<String, ArrayList<Image>> skeletonAnims = new Hashtable<>();
        Hashtable<String, Double> skeletonDeltas = new Hashtable<>();
        skeletonAnims.put("idle", skeletonIdle);
        skeletonAnims.put("walk", skeletonWalk);
        skeletonDeltas.put("idle", .2);
        skeletonDeltas.put("walk", .05);
        skeletonSprite = new Sprite(skeletonAnims, skeletonDeltas, "walk");
        MobController skeleton = new MobController(true, 50, skeletonSprite, adventurer);
        skeletonSprite.setPos(0, 115);
        updatables.add(skeleton);
        sprites.add(skeletonSprite);
        //end skeleton
        

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


        //Sprite platformTest = new Sprite(new Image("images/platform.png"));
        //platformTest.setPos(200, 50);
        //sprites.add(platformTest);

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
