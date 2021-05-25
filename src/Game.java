import java.util.ArrayList;
import javafx.application.Application;
import javafx.animation.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Transform;

enum GameUIState { PLAYING, MENU, GAMEOVER }

public class Game extends Application {
    StackPane root = new StackPane();
    Scene scene = new Scene(root);
    Canvas canvas;
    Camera camera = new Camera();
    KeyHandler keyHandler = new KeyHandler(scene);

    SoundBackground bgSound;
    ParallaxSprite background;
    ParallaxSprite foreground;

    ArrayList<Sprite> sprites = new ArrayList<>();
    ArrayList<Updatable> updatables = new ArrayList<>();

    Sprite adventurer;

    int currentLevel = 1;

    public PlayerController playerController;
    
    PhysicsWorld physicsWorld;
    PhysicsRectangle groundRect;
    GameController gameController;

    GameUIState gameUIState = GameUIState.PLAYING;
    
    private Sprite advertisedText = null;
    private int advertisementFrame = 0;
    private int advertisementMaxFrame = 100;
    
    public GameUIState getGameUIState() { return gameUIState; }

    public void setGameUIState(GameUIState gameUIState) { this.gameUIState = gameUIState; }

    public int getAdFrame() { return advertisementFrame; }

    public void initializeWorld() {
        gameController.inititalize();

        updatables.clear();
        updatables.add(gameController);

        // physics
        physicsWorld = new PhysicsWorld();
        updatables.add(physicsWorld);

        // ground
        groundRect = new PhysicsRectangle(0, 115 + 50 + 14, 10e10, 100).setFixed();
        physicsWorld.add(groundRect);

        // player
        sprites.clear();
        adventurer = SpriteLoader.loadAnimation("adventurer");
        adventurer.setPos(-100, 115);
        sprites.add(adventurer);

        playerController = new PlayerController(this, keyHandler, adventurer);
        updatables.add(playerController);
        physicsWorld.add(playerController);

        // camera
        camera.setScale(3);
        camera.setSpeed(0.05);
        camera.setTarget(adventurer);
        camera.setPos(adventurer.getPos().copy().add(new Vector(0, -100)));
        updatables.add(camera);
    }

    public void addMob(Sprite sprite, MobController mob) {
        sprites.add(sprite);
        updatables.add(mob);
        physicsWorld.add(mob);
    }

    public void removeMob(Sprite sprite, MobController mob) {
        sprites.remove(sprite);
        updatables.remove(mob);
        physicsWorld.remove(mob);
    }

    public void nextLevel() {
        currentLevel++;
        if (currentLevel > 3) currentLevel = 1;
        gameController.changeLevels("Level " + currentLevel);
    }

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
        gc.setFont(Font.loadFont("./fonts/Bitmgothic.ttf", 18));

        ArrayList<ParallaxSprite> environment = SpriteLoader.loadEnvironment("Level 1");
        background = environment.get(0);
        foreground = environment.get(1); 
        foreground.setDepth(-1);

        // game controler
        gameController = new GameController(this);

        // background sound
        bgSound = new SoundBackground();
        bgSound.run();

        gameController.changeLevels("Level 1");
        
        // GAMELOOP
        final long startNanoTime = System.nanoTime();
        new AnimationTimer() {
            public void handle(long currentNanoTime) {
                double t = (currentNanoTime - startNanoTime) / 1e9;
                clear();

                adjustCanvasToWindowSize(gc);
                camera.applyTransform(gc);

                // update everything
                if (gameUIState == GameUIState.PLAYING)
                    updatables.forEach(Updatable::update);
                else {
                    gameController.update();
                    camera.update();
                }
                    
                groundRect.getPos().setX(playerController.getPos().getX());

                // draw background
                background.draw(gc, camera);

                // draw sprites
                sprites.forEach(s -> s.draw(gc, t));

                // draw foreground
                foreground.draw(gc, camera);

                // draw physics debug graphics
                physicsWorld.draw(gc);

                if (gameUIState == GameUIState.GAMEOVER) {
                    
                }

                drawBlackRects(gc);
                drawHealth(gc);
                gameController.draw(gc);

                drawAdvertisedText(gc);
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
        if (width/16 < height/9) {
            gc.fillRect(0, 0, width, (height-width/16*9)/2);
            gc.fillRect(0, height-(height-width/16*9)/2, width, (height-width/16*9)/2);
        } else {
            gc.fillRect(0, 0, (width-height/9*16)/2, height);
            gc.fillRect(width-(width-height/9*16)/2, 0, (width-height/9*16)/2, height);
        }
    }

    public void drawHealth(GraphicsContext gc) {
        // make hearts responsive 
        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        double scale = Math.min(width, height/9*16)/1600;

        if (width/16 < height/9) gc.setTransform(scale, 0, 0, scale, 0, (height-width/16*9)/2);
        else gc.setTransform(scale, 0, 0, scale, (width-height/9*16)/2, 0);

        // draw hearts
        for (int i = 0; i < playerController.maxHealth()/2; i++) {
            final int HEART_SIZE = 50;
            
            double heartVal = playerController.currentHealth()/2.0 - i - 0.5;
            String name;
            if (heartVal < 0) name = "empty";
            else if (heartVal < 0.5) name = "half";
            else name = "full";

            Image heart = new Image("images/ui/heart/" + name + ".png");
            gc.drawImage(heart, i * HEART_SIZE * 1.25 + 20, 20, HEART_SIZE, HEART_SIZE);
        }
    }

    public void drawAdvertisedText(GraphicsContext gc) {
        if (advertisedText == null)
            return;

        if (advertisementFrame < advertisementMaxFrame) {
            advertisementFrame++;
        } else {
            advertisedText = null;
            advertisementFrame = 0;
            return;
        }

        double width = gc.getCanvas().getWidth();
        double height = gc.getCanvas().getHeight();
        double scale = Math.min(width, height/9*16)/1600;

        if (width/16 < height/9) gc.setTransform(scale, 0, 0, scale, 0, (height-width/16*9)/2);
        else gc.setTransform(scale, 0, 0, scale, (width-height/9*16)/2, 0);

        var t = advertisementFrame/(double) advertisementMaxFrame;

        gc.translate(800 + Math.tan((t-0.5)*Math.PI) * 50, 450);
        gc.scale(3, 3);

        advertisedText.draw(gc, 0);
    }

    public void advertise(Sprite text) {
        advertisedText = text;
    }

    public Camera getCamera() { return camera; }
    public KeyHandler getKeyHandler() { return keyHandler; }

    public void setAdvertisementMaxFrame(int frame) {
        advertisementMaxFrame = frame;
    }

    public PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }
}

