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
            game.getCamera().hitLeft(50);
            keyHandler.preventRepeat("H");
        }
        if (keyHandler.isPressed("L")) {
            lowerCameraIntoForest();
        }

        if (loweringCamera) {
            Camera camera = game.getCamera();
            camera.getAimPos().add(new Vector(0, 0.8));
            if (camera.getPos().getY() > 55) {
                loweringCamera = false;
                camera.setTarget(game.adventurer);
                camera.setSpeed(0.05);
            }
        }
    }

    public void lowerCameraIntoForest() {
        loweringCamera = true;
        Camera camera = game.getCamera();
        camera.setTarget(null);
        camera.setPos(game.adventurer.getPos().getX(), -480);
        camera.setSpeed(0.001);
    }
}
