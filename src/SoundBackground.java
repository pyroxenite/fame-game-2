import java.io.File;
import javafx.util.Duration;
import javafx.scene.media.*;

public class SoundBackground implements Runnable {
    Media media;
    MediaPlayer mediaPlayer;

    public SoundBackground() {
        File mediaFile = new File("./sounds/1.wav");
        media = new Media(mediaFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
    }

    public void run() {
        mediaPlayer.setOnReady(() -> { 
            mediaPlayer.play(); 
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            mediaPlayer.play();
        });
    }
}
