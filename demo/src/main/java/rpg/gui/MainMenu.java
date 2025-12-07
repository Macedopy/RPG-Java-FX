package rpg.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainMenu {
    private Stage stage;

    public MainMenu(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Create main menu layout
        VBox root = new VBox(20);
        root.setPadding(new Insets(50));
        root.setAlignment(Pos.CENTER);

        // User name label
        Label userNameLabel = new Label("André Maligno");
        userNameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        // Start game button
        Button startButton = new Button("Iniciar Jogo");
        startButton.setStyle("-fx-font-size: 16px; -fx-pref-width: 200px;");
        startButton.setOnAction(e -> showCharacterSelection());

        // Exit button
        Button exitButton = new Button("Sair");
        exitButton.setStyle("-fx-font-size: 16px; -fx-pref-width: 200px;");
        exitButton.setOnAction(e -> stage.close());

        root.getChildren().addAll(userNameLabel, startButton, exitButton);

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("RPG Game - Menu Principal");
        stage.setScene(scene);
        stage.show();
    }

    private void showCharacterSelection() {
        CharacterSelection characterSelection = new CharacterSelection(stage);
        characterSelection.show();
    }
}
