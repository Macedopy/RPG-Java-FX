package rpg.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import rpg.models.Berserker;
import rpg.models.Fuzileiro;
import rpg.models.Mago;
import rpg.models.Personagem;

public class CharacterSelection {
    private Stage stage;
    private Personagem selectedCharacter;

    public CharacterSelection(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Selecione seu Personagem");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        HBox characterBox = new HBox(30);
        characterBox.setAlignment(Pos.CENTER);

        // Berserker
        VBox berserkerBox = createCharacterCard("Berserker", "Berserker.jpg", () -> {
            selectedCharacter = new Berserker("Berserker", 150, 20, 15, 1);
            startGame();
        });

        // Fuzileiro
        VBox fuzileiroBox = createCharacterCard("Fuzileiro", "Fuzileiro.png", () -> {
            selectedCharacter = new Fuzileiro("Fuzileiro");
            startGame();
        });

        // Mago
        VBox magoBox = createCharacterCard("Mago", "Mago.jpg", () -> {
            selectedCharacter = new Mago("Mago");
            startGame();
        });

        characterBox.getChildren().addAll(berserkerBox, fuzileiroBox, magoBox);

        Button backButton = new Button("Voltar");
        backButton.setOnAction(e -> {
            MainMenu mainMenu = new MainMenu(stage);
            mainMenu.show();
        });

        root.getChildren().addAll(titleLabel, characterBox, backButton);

        Scene scene = new Scene(root, 800, 500);
        stage.setTitle("RPG Game - Seleção de Personagem");
        stage.setScene(scene);
        stage.show();
    }

    private VBox createCharacterCard(String name, String imagePath, Runnable onSelect) {
        VBox card = new VBox(15);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(30));
        card.setStyle("-fx-border-color: black; -fx-border-width: 2; -fx-background-color: #f0f0f0;");

        // Placeholder for image
        ImageView imageView = new ImageView();
        try {
            // Try to load image, if not found, show placeholder
            Image image = new Image(getClass().getResourceAsStream("/rpg/gui/" + imagePath));
            imageView.setImage(image);
        } catch (Exception e) {
            // Placeholder rectangle if image not found
            imageView.setStyle("-fx-background-color: #cccccc; -fx-min-width: 150; -fx-min-height: 150;");
        }
        imageView.setFitWidth(150);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button selectButton = new Button("Selecionar");
        selectButton.setOnAction(e -> onSelect.run());

        card.getChildren().addAll(imageView, nameLabel, selectButton);
        return card;
    }

    private void startGame() {
        // Start the actual game screen
        GameScreen gameScreen = new GameScreen(stage, selectedCharacter);
        gameScreen.show();
    }
}
