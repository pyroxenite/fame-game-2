import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.image.*;

import java.util.ArrayList;
import javafx.animation.*;

public class Game extends Application {
    Group root = new Group();
    Scene scene = new Scene(root);
    Canvas canvas;

    ArrayList<Sprite> sprites = new ArrayList<>();
    ArrayList<Updatable> updatables = new ArrayList<>();

    public void start(Stage stage) {
        stage.setTitle("Game");
        stage.setScene(scene);

        int width = 1200;
        canvas = new Canvas(width, width/16*9);
        root.getChildren().add(canvas);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        
        gc.setImageSmoothing(false); // stops pixel-art from bluring

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
        Sprite bc = new Sprite(new Image("images/briefcase.png"));
        bc.setPos(300, 120);
        sprites.add(bc);

        ArrayList<Image> ufoI = new ArrayList<>();
        for (int i = 0; i < 4; i++)
            ufoI.add(new Image("images/adventurer/idle-0" + i + ".png"));

        Sprite ufo = new Sprite(ufoI, 0.2);
        ufo.setPos(0, 0);
        sprites.add(ufo);

        // controls
        PlayerController ufoController = new PlayerController(scene, ufo);
        ufoController.setActiveSpeed(0.5);
        ufoController.setFriction(0.1);
        updatables.add(ufoController);
        
        // camera
        Camera camera = new Camera();
        camera.setScale(2);
        camera.setSpeed(0.1);
        camera.setTarget(ufo);
        camera.setPos(ufo.getPos());
        updatables.add(camera);

        final long startNanoTime = System.nanoTime();

        // gameloop
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
}
