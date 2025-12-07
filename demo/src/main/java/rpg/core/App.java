package rpg.core;

import javafx.application.Application;
import javafx.stage.Stage;
import rpg.gui.MainMenu;

public class App extends Application
{
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        MainMenu mainMenu = new MainMenu(primaryStage);
        mainMenu.show();
    }
}
