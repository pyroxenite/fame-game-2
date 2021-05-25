import javafx.scene.paint.Color;
import javafx.scene.canvas.*;
import javafx.scene.image.Image;
import javafx.application.Application;
import java.util.ArrayList;

import java.io.*;
import java.util.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class GameController implements Updatable {
    Game game;
    KeyHandler keyHandler;

    boolean loweringCamera = false;
    int fade = 0;
    double alpha = 1;
    int delayCounterMax = 100;
    int delayCounter = -1;
    int currentLevel = 1;
    boolean deathIsDone = false;

    String newLevelName;

    JSONParser parser = new JSONParser();

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
            lowerCamera();
        }

        // if (keyHandler.isPressed("U")) {
        //     fade = 1;
        //     currentLevel = currentLevel%3 + 1;
        //     newLevelName = "Level " + currentLevel;
        // }

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

        if (game.getGameUIState() == GameUIState.PLAYING && game.playerController.currentHealth() <= 0 && !deathIsDone) {
            handleDeath();
            deathIsDone = true;
        }

        if (deathIsDone && game.getAdFrame() == 100) {
            deathIsDone = false;
            this.changeLevels(newLevelName);
            game.setGameUIState(GameUIState.PLAYING);
        }
    }

    public void draw(GraphicsContext gc) {
        if (fade != 0) {
            gc.setTransform(1, 0, 0, 1, 0, 0);
            double width = gc.getCanvas().getWidth();
            double height = gc.getCanvas().getHeight();

            alpha = Math.max(0, Math.min(1, alpha)); 

            gc.setFill(new Color(0, 0, 0, alpha));
            gc.fillRect(0, 0, width, height);
            
            if (delayCounter < 0) {
                alpha += fade/100.0;
                alpha = Math.max(0, Math.min(1, alpha)); 

                if (alpha == 1) {
                    fade = -1;
                    delayCounter = 0;
                    lowerCamera();
                } else if (alpha == 0) {
                    fade = 0;
                    delayCounter = -1;
                }
            } else {
                delayCounter++;
                if (delayCounter == delayCounterMax / 2)  {
                    ArrayList<ParallaxSprite> backgrounds = SpriteLoader.loadEnvironment(newLevelName);
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
        game.initializeWorld();
        game.gameUIState = GameUIState.PLAYING;

        game.advertise(new Sprite(new Image("images/text/" + levelName + ".png")));

        try {
            Object obj = parser.parse(new FileReader("config/levels.json"));
            JSONObject jsonObject = (JSONObject)obj;
            JSONObject levelData = (JSONObject)jsonObject.get(levelName);

            for (Object k : levelData.keySet()) {
                String name = (String) k;
                if (name.equals("mobs")) {
                    JSONObject mobs = (JSONObject)levelData.get(name);

                    for (Object v : mobs.keySet()) {
                        String mobName = (String)v;
                        JSONObject mobData = (JSONObject)mobs.get(mobName);

                        int numMob = ((Long) mobData.get("num")).intValue();
                        Boolean isBoss = (Boolean) mobData.get("isBoss");

                        for (int i = 0; i < numMob; i++) {

                            Sprite sprite = SpriteLoader.loadAnimation(mobName);
                            sprite.setPos(new Vector(300 + Math.random()*500 + (isBoss?400:0), 115));
                            System.out.println(((Long)mobData.get("yOffset")).intValue());
                            sprite.setOffset(((Long)mobData.get("yOffset")).intValue());
                            
                            MobController mob = new MobController(game, sprite, ((Long) mobData.get("health")).intValue());

                            if (isBoss) mob.setBoss();
                            mob.setDamage(0);

                            mob.setMoveSpeed(((Double)mobData.get("speed")));
                            mob.setDmgFrames(
                                ((Long) mobData.get("frameStart")).intValue(), 
                                ((Long) mobData.get("frameEnd")).intValue()
                            );
                            game.addMob(sprite, mob);
                        }
                    }
                } else if (name.equals("platforms")) {
                    JSONObject platforms = (JSONObject)levelData.get("platforms");
                    for (Object p : platforms.keySet()) {
                        System.out.println(p);
                    }
                }
            }   
        } catch (Exception e) {
            e.printStackTrace();
        }

        fade = 1;
        newLevelName = levelName;
    }

    public void lowerCamera() {
        loweringCamera = true;
        Camera camera = game.getCamera();
        camera.setTarget(null);
        //camera.setPos(game.adventurer.getPos().getX(), -480);
        camera.setPos(game.adventurer.getPos().getX(), -120);
        camera.setSpeed(0.002);
        camera.setScale(5);
    }

    private void handleDeath() {
        game.setGameUIState(GameUIState.GAMEOVER);
        Camera camera = game.getCamera();
        camera.setSpeed(0.002);
        camera.setAimScale(8);
        game.advertise(new Sprite(new Image("images/text/you-have-died.png")));
        game.setAdvertisementMaxFrame(300);
    }

    public void inititalize() {
        deathIsDone = false;
    }
}
