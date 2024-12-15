package Man;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;

public class SoundManager {
    private Clip backgroundMusicClip;
    private boolean isMusicPlaying = true;

    public SoundManager(String filePath) {
        initializeBackgroundMusic(filePath);
    }

    private void initializeBackgroundMusic(String filePath) {
        try {
            URL resource = getClass().getResource(filePath);
            if (resource == null) {
                throw new IllegalArgumentException("Resource not found: " + filePath);
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioInputStream);
        } catch (Exception e) {
            System.err.println("Error initializing background music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void toggleSound() {
        if (isMusicPlaying) {
            stopBackgroundMusic();
        } else {
            playBackgroundMusic();
        }
        isMusicPlaying = !isMusicPlaying;
    }

    public void playBackgroundMusic() {
        if (backgroundMusicClip != null) {
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
        }
    }

    public boolean isMusicPlaying() {
        return isMusicPlaying;
    }

    public void playSoundOnce(String filePath) {
        try {
            URL resource = getClass().getResource(filePath);
            if (resource == null) {
                throw new IllegalArgumentException("Resource not found: " + filePath);
            }
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        } catch (Exception e) {
            System.err.println("Error playing sound: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
