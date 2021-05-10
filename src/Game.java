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

        ArrayList<Image> attack1 = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            attack1.add(new Image("images/adventurer/attack1-0" + i + ".png"));

        ArrayList<Image> attack2 = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            attack2.add(new Image("images/adventurer/attack2-0" + i + ".png"));

        ArrayList<Image> attack3 = new ArrayList<>();
        for (int i = 0; i < 6; i++)
            attack3.add(new Image("images/adventurer/attack3-0" + i + ".png"));

        ArrayList<Image> stagger = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            stagger.add(new Image("images/adventurer/hurt-0" + i + ".png"));

        Hashtable<String, ArrayList<Image>> adventurerAnims = new Hashtable<>();
        adventurerAnims.put("idle", idle);
        adventurerAnims.put("run", run);
        adventurerAnims.put("jump", jump);
        adventurerAnims.put("fall", fall);
        adventurerAnims.put("attack1", attack1);
        adventurerAnims.put("attack2", attack2);
        adventurerAnims.put("attack3", attack3);
        adventurerAnims.put("stagger", stagger);

        Hashtable<String, Double> adventurerAnimsDeltas = new Hashtable<>();
        adventurerAnimsDeltas.put("idle", .2);
        adventurerAnimsDeltas.put("run", .08);
        adventurerAnimsDeltas.put("jump", .1);
        adventurerAnimsDeltas.put("fall", .2);
        adventurerAnimsDeltas.put("attack1", .1);
        adventurerAnimsDeltas.put("attack2", .1);
        adventurerAnimsDeltas.put("attack3", .1);
        adventurerAnimsDeltas.put("stagger", .1);

        adventurer = new Sprite(adventurerAnims, adventurerAnimsDeltas, "idle");
        adventurer.setPos(0, 115);
        sprites.add(adventurer);


        //skeleton test
        ArrayList<Image> skeletonIdle = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            skeletonIdle.add(new Image("images/mobs/skeleton/idle/idle" + i + ".png"));
        }
        ArrayList<Image> skeletonWalk = new ArrayList<>();
        for (int i = 0; i < 13; i++) {
            skeletonWalk.add(new Image("images/mobs/skeleton/walk/walk" + i + ".png"));
        }
        ArrayList<Image> skeletonAttack = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            skeletonAttack.add(new Image("images/mobs/skeleton/attack/attack" + i + ".png"));
        }
        ArrayList<Image> skeletonStagger = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            skeletonStagger.add(new Image("images/mobs/skeleton/damage/damage" + i + ".png"));
        }
        
        Hashtable<String, ArrayList<Image>> skeletonAnims = new Hashtable<>();
        skeletonAnims.put("idle", skeletonIdle);
        skeletonAnims.put("walk", skeletonWalk);
        skeletonAnims.put("run", skeletonWalk);
        skeletonAnims.put("attack", skeletonAttack);
        skeletonAnims.put("stagger", skeletonStagger);

        Hashtable<String, Double> skeletonDeltas = new Hashtable<>();
        skeletonDeltas.put("idle", .2);
        skeletonDeltas.put("walk", .05);
        skeletonDeltas.put("run", .03);
        skeletonDeltas.put("attack", .05);
        skeletonDeltas.put("stagger", .05);

        skeletonSprite = new Sprite(skeletonAnims, skeletonDeltas, "walk");
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
