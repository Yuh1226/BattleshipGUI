package battleship.fx;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class AudioManager {
    private static AudioManager instance;
    private MediaPlayer backgroundPlayer;
    private final Map<String, Media> soundEffects = new HashMap<>();
    
    private double musicVolume = 0.4; // Default music volume
    private double sfxVolume = 0.7;   // Default sfx volume
    private boolean soundEnabled = true;

    private PauseTransition duckingTimer;
    private Timeline fadeInTimeline;

    private AudioManager() {
        loadSound("fire", "/audio/cannon_fire.mp3");
        loadSound("hit", "/audio/cannon_hit_ship_short.mp3");
        loadSound("miss", "/audio/water_splash.mp3");
        loadSound("sunk", "/audio/ship_destroyed.mp3");
        
        loadBackgroundMusic("/audio/Nhac_nen.mp3");
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadSound(String name, String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                soundEffects.put(name, new Media(resource.toExternalForm()));
            }
        } catch (Exception e) {
            System.err.println("Could not load sound: " + path);
        }
    }

    private void loadBackgroundMusic(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                backgroundPlayer = new MediaPlayer(media);
                backgroundPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                backgroundPlayer.setVolume(musicVolume * 0.3); // Music is naturally quieter
            }
        } catch (Exception e) {
            System.err.println("Could not load background music: " + path);
        }
    }

    public void playBackgroundMusic() {
        if (backgroundPlayer != null && soundEnabled) {
            backgroundPlayer.play();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundPlayer != null) {
            backgroundPlayer.stop();
        }
    }

    public void playSound(String name) {
        if (!soundEnabled) return;
        Media media = soundEffects.get(name);
        if (media != null) {
            MediaPlayer sfxPlayer = new MediaPlayer(media);
            sfxPlayer.setVolume(sfxVolume);
            
            // Ducking Effect: lower BG music when SFX plays
            applyDucking();
            
            sfxPlayer.play();
            // Cleanup player after finished
            sfxPlayer.setOnEndOfMedia(sfxPlayer::dispose);
        }
    }

    private void applyDucking() {
        if (backgroundPlayer == null || !soundEnabled) return;

        // Cancel existing recovery timers
        if (duckingTimer != null) duckingTimer.stop();
        if (fadeInTimeline != null) fadeInTimeline.stop();

        double duckedVol = (musicVolume * 0.3) * 0.2; // Drop to 20% of current music volume
        backgroundPlayer.setVolume(duckedVol);

        // Wait longer before restoring volume to avoid jitter with multiple SFX
        duckingTimer = new PauseTransition(Duration.seconds(2.0));
        duckingTimer.setOnFinished(e -> {
            fadeInTimeline = new Timeline(
                new KeyFrame(Duration.millis(800), 
                new KeyValue(backgroundPlayer.volumeProperty(), musicVolume * 0.3))
            );
            fadeInTimeline.play();
        });
        duckingTimer.play();
    }

    public void setMusicVolume(double volume) {
        this.musicVolume = volume / 100.0;
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(this.musicVolume * 0.3);
        }
    }

    public void setSfxVolume(double volume) {
        this.sfxVolume = volume / 100.0;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (enabled) {
            if (backgroundPlayer != null) backgroundPlayer.play();
        } else {
            if (backgroundPlayer != null) backgroundPlayer.pause();
        }
    }

    public double getMusicVolume() { return musicVolume * 100; }
    public double getSfxVolume() { return sfxVolume * 100; }
    public boolean isSoundEnabled() { return soundEnabled; }
}
