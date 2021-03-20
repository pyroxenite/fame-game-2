public class GameController implements Updatable {
    Game game;
    KeyHandler keyHandler;

    boolean loweringCamera = false;

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
