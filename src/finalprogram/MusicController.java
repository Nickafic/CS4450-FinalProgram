package finalprogram;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class MusicController {
    private Clip clip;

    public void playMusic(String filePath) {
        try {
            File musicPath = new File(filePath);

            if (musicPath.exists()) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(musicPath);

                clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                clip.start();
            } else {
                System.out.println("Can't find file: " + filePath);
            }
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file format: " + filePath);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Line is unavailable to play audio: " + filePath);
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Error reading audio file: " + filePath);
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Unexpected error while playing audio: " + filePath);
            e.printStackTrace();
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
