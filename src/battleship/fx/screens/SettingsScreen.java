package battleship.fx.screens;

import battleship.fx.LocalizationManager;
import battleship.fx.AudioManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class SettingsScreen extends VBox {
    public interface Listener {
        void onBack();
        void onBackToMenu();
        void onLanguageChanged(String lang);
        void onSoundToggled(boolean enabled);
        void onMusicVolumeChanged(double volume);
        void onSfxVolumeChanged(double volume);
    }

    private Listener listener;
    private final Label titleLabel = new Label();
    private final Label langLabel = new Label();
    private final Label audioLabel = new Label();
    private final Label musicLabel = new Label();
    private final Label sfxLabel = new Label();
    
    private final ComboBox<String> languageCombo = new ComboBox<>();
    private final CheckBox soundCheck = new CheckBox();
    private final Slider musicSlider = new Slider(0, 100, 75);
    private final Slider sfxSlider = new Slider(0, 100, 75);
    
    private final Button backButton = new Button();
    private final Button menuButton = new Button();

    public SettingsScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(20);
        setPadding(new Insets(40));
        getStyleClass().add("screen-root");

        titleLabel.getStyleClass().add("screen-title");

        GridPane settingsGrid = new GridPane();
        settingsGrid.setAlignment(Pos.CENTER);
        settingsGrid.setHgap(30);
        settingsGrid.setVgap(20);
        settingsGrid.getStyleClass().add("settings-container");
        settingsGrid.setPadding(new Insets(30));

        // Language
        langLabel.getStyleClass().add("stat-label");
        languageCombo.getItems().addAll("English", "Tiếng Việt");
        languageCombo.setValue(LocalizationManager.getCurrentLanguage().getDisplayName());
        languageCombo.getStyleClass().add("settings-combo");
        languageCombo.setOnAction(e -> {
            if (listener != null) {
                String val = languageCombo.getValue();
                if (val.equals("Tiếng Việt")) {
                    LocalizationManager.setLanguage(LocalizationManager.Language.VI);
                } else {
                    LocalizationManager.setLanguage(LocalizationManager.Language.EN);
                }
                listener.onLanguageChanged(val);
                updateLanguage();
            }
        });

        settingsGrid.add(langLabel, 0, 0);
        settingsGrid.add(languageCombo, 1, 0);

        // Sound Toggle
        audioLabel.getStyleClass().add("stat-label");
        soundCheck.setSelected(AudioManager.getInstance().isSoundEnabled());
        soundCheck.getStyleClass().add("settings-check");
        soundCheck.setOnAction(e -> {
            boolean enabled = soundCheck.isSelected();
            musicSlider.setDisable(!enabled);
            sfxSlider.setDisable(!enabled);
            if (listener != null) listener.onSoundToggled(enabled);
        });
        settingsGrid.add(audioLabel, 0, 1);
        settingsGrid.add(soundCheck, 1, 1);

        // Music Volume
        musicLabel.getStyleClass().add("stat-label");
        musicSlider.setValue(AudioManager.getInstance().getMusicVolume());
        musicSlider.setPrefWidth(200);
        musicSlider.setDisable(!soundCheck.isSelected());
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (listener != null) listener.onMusicVolumeChanged(newVal.doubleValue());
        });
        
        VBox musicBox = new VBox(5, musicLabel, musicSlider);
        settingsGrid.add(musicBox, 0, 2);

        // SFX Volume
        sfxLabel.getStyleClass().add("stat-label");
        sfxSlider.setValue(AudioManager.getInstance().getSfxVolume());
        sfxSlider.setPrefWidth(200);
        sfxSlider.setDisable(!soundCheck.isSelected());
        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (listener != null) listener.onSfxVolumeChanged(newVal.doubleValue());
        });

        VBox sfxBox = new VBox(5, sfxLabel, sfxSlider);
        settingsGrid.add(sfxBox, 1, 2);

        VBox bottomButtons = new VBox(10);
        bottomButtons.setAlignment(Pos.CENTER);

        backButton.setPrefWidth(250);
        backButton.getStyleClass().add("action-button");
        backButton.setOnAction(event -> {
            if (listener != null) {
                listener.onBack();
            }
        });

        menuButton.setPrefWidth(250);
        menuButton.getStyleClass().add("secondary-button");
        menuButton.setOnAction(event -> {
            if (listener != null) {
                listener.onBackToMenu();
            }
        });

        bottomButtons.getChildren().addAll(backButton, menuButton);

        getChildren().addAll(titleLabel, settingsGrid, bottomButtons);
        updateLanguage();
    }

    public void updateLanguage() {
        titleLabel.setText(LocalizationManager.get("settings"));
        langLabel.setText(LocalizationManager.get("lang_label"));
        audioLabel.setText(LocalizationManager.get("audio_label"));
        soundCheck.setText(LocalizationManager.get("enable_sound"));
        musicLabel.setText(LocalizationManager.get("music_volume"));
        sfxLabel.setText(LocalizationManager.get("sfx_volume"));
        backButton.setText(LocalizationManager.get("back"));
        menuButton.setText(LocalizationManager.get("return_hq"));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
