package battleship.fx.screens;

import battleship.fx.LocalizationManager;
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
        void onLanguageChanged(String lang);
        void onSoundToggled(boolean enabled);
        void onVolumeChanged(double volume);
    }

    private Listener listener;
    private final Label titleLabel = new Label();
    private final Label langLabel = new Label();
    private final Label audioLabel = new Label();
    private final ComboBox<String> languageCombo = new ComboBox<>();
    private final CheckBox soundCheck = new CheckBox();
    private final Slider volumeSlider = new Slider(0, 100, 75);
    private final Button backButton = new Button();

    public SettingsScreen() {
        setAlignment(Pos.CENTER);
        setSpacing(30);
        setPadding(new Insets(50));
        getStyleClass().add("screen-root");

        titleLabel.getStyleClass().add("screen-title");

        GridPane settingsGrid = new GridPane();
        settingsGrid.setAlignment(Pos.CENTER);
        settingsGrid.setHgap(30);
        settingsGrid.setVgap(25);
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

        // Sound
        audioLabel.getStyleClass().add("stat-label");
        
        soundCheck.setSelected(true);
        soundCheck.getStyleClass().add("settings-check");
        soundCheck.setOnAction(e -> {
            volumeSlider.setDisable(!soundCheck.isSelected());
            if (listener != null) listener.onSoundToggled(soundCheck.isSelected());
        });

        VBox audioBox = new VBox(10, soundCheck, volumeSlider);
        audioBox.setAlignment(Pos.CENTER_LEFT);
        
        volumeSlider.setShowTickLabels(false);
        volumeSlider.setShowTickMarks(false);
        volumeSlider.setPrefWidth(200);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (listener != null) listener.onVolumeChanged(newVal.doubleValue());
        });

        settingsGrid.add(audioLabel, 0, 1);
        settingsGrid.add(audioBox, 1, 1);

        backButton.setPrefWidth(200);
        backButton.getStyleClass().add("secondary-button");
        backButton.setOnAction(event -> {
            if (listener != null) {
                listener.onBack();
            }
        });

        getChildren().addAll(titleLabel, settingsGrid, backButton);
        updateLanguage();
    }

    public void updateLanguage() {
        titleLabel.setText(LocalizationManager.get("settings"));
        langLabel.setText(LocalizationManager.get("lang_label"));
        audioLabel.setText(LocalizationManager.get("audio_label"));
        soundCheck.setText(LocalizationManager.get("enable_sound"));
        backButton.setText(LocalizationManager.get("back"));
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }
}
