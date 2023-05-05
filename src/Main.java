import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;


public class Main extends Application {



    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Password Strength Calculator");
        Scene scene = new Scene(root);
        primaryStage.setResizable(false);
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);

        primaryStage.setScene(scene);
        String css = this.getClass().getResource("css/application.css").toExternalForm();
        scene.getStylesheets().add(css);
        primaryStage.show();



    }

    public static void main(String[] args) {
        launch(args);
    }


}