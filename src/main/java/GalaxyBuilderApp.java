import WorldBuilder.GalaxyDataBase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GalaxyBuilderApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // Check to make sure DB is there if not creates an empty one.
        new GalaxyDataBase();
        // Loads scene
        FXMLLoader fxmlLoader = new FXMLLoader(GalaxyBuilderApp.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1240, 780);
        stage.setTitle("Galaxy Builder v0.1");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
