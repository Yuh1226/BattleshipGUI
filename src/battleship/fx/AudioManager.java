package battleship.fx;

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
    private final Map<String, Double> soundDurations = new HashMap<>();
    private double volume = 0.5;
    private double musicVolume = 0.5;
    private double sfxVolume = 0.5;
    private boolean soundEnabled = true;

    public static final double TRAVEL_DELAY = 0.5;

    private AudioManager() {
        // Cố định thời gian ngắn lại ngay tại đây (đơn vị: giây)
        loadSound("fire", "/audio/cannon_fire.mp3", 1.0);
        loadSound("hit", "/audio/cannon_hit_ship_short.mp3", 1.2);
        loadSound("miss", "/audio/water_splash.mp3", 1.0);
        loadSound("sunk", "/audio/ship_destroyed.mp3", 3.0);
        
        loadBackgroundMusic("/audio/Nhac_nen.mp3");
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    private void loadSound(String name, String path, double duration) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                soundEffects.put(name, new Media(resource.toExternalForm()));
                soundDurations.put(name, duration);
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
                backgroundPlayer.setVolume(musicVolume * 0.2); // Background music is now subtler
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
        playSound(name, null);
    }

    public void playSound(String name, Runnable onFinished) {
        if (!soundEnabled) {
            if (onFinished != null) onFinished.run();
            return;
        }
        Media media = soundEffects.get(name);
        if (media != null) {
            MediaPlayer sfxPlayer = new MediaPlayer(media);
            sfxPlayer.setVolume(sfxVolume);
            
            // Ducking Effect: lower BG music when SFX plays
            applyDucking(name);
            
            sfxPlayer.play();
            
            // Apply duration limit if exists
            Double duration = soundDurations.get(name);
            if (duration != null && duration > 0) {
                javafx.animation.PauseTransition stopTask = new javafx.animation.PauseTransition(Duration.seconds(duration));
                stopTask.setOnFinished(e -> {
                    sfxPlayer.stop();
                    sfxPlayer.dispose();
                    if (onFinished != null) onFinished.run();
                });
                stopTask.play();
            } else {
                // Cleanup player after finished
                sfxPlayer.setOnEndOfMedia(() -> {
                    sfxPlayer.dispose();
                    if (onFinished != null) onFinished.run();
                });
            }
        } else {
            if (onFinished != null) onFinished.run();
        }
    }

    private void applyDucking(String sfxName) {
        if (backgroundPlayer == null || !soundEnabled) return;

        double originalBgVol = musicVolume * 0.2;
        double duckedVol = originalBgVol * 0.4; // Reduce to 40% of its current level

        // Lower volume immediately
        backgroundPlayer.setVolume(duckedVol);

        // Transition back to original volume after a delay
        javafx.animation.PauseTransition recoveryDelay = new javafx.animation.PauseTransition(Duration.seconds(1.5));
        recoveryDelay.setOnFinished(e -> {
            // Simple linear-ish fade back
            javafx.animation.Timeline fadeIn = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(Duration.millis(50), 
                new javafx.animation.KeyValue(backgroundPlayer.volumeProperty(), originalBgVol))
            );
            fadeIn.play();
        });
        recoveryDelay.play();
    }

    public void setVolume(double volume) {
        this.volume = volume / 100.0;
        this.musicVolume = this.volume;
        this.sfxVolume = this.volume;
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(this.musicVolume * 0.1);
        }
    }

    public double getMusicVolume() {
        return musicVolume * 100.0;
    }

    public void setMusicVolume(double volume) {
        this.musicVolume = volume / 100.0;
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(this.musicVolume * 0.2);
        }
    }

    public double getSfxVolume() {
        return sfxVolume * 100.0;
    }

    public void setSfxVolume(double volume) {
        this.sfxVolume = volume / 100.0;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (enabled) {
            playBackgroundMusic();
        } else {
            if (backgroundPlayer != null) backgroundPlayer.pause();
        }
    }
}
