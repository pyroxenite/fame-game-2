import java.util.ArrayList;
import javafx.scene.Scene;

public class KeyHandler {
    protected ArrayList<String> input = new ArrayList<>();

    public KeyHandler(Scene scene) {
        scene.setOnKeyPressed(e -> {
            String code = e.getCode().toString();
            if (!input.contains(code))
                input.add(code);
        });

        scene.setOnKeyReleased(e -> {
            String code = e.getCode().toString();
            input.remove(code);
        });
    }

    public ArrayList<String> pressedKeys() {
        return input;
    }

    public boolean isPressed(String key) {
        return input.contains(key);
    }

    public void preventRepeat(String key) {
        input.remove(key);
    }
}
