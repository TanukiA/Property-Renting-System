import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/*
    This is the Main class that initiates the JavaFX application.
    It loads the main menu, which contains Login, Register and Continue as Guest button.
*/
public class Main extends Application{

    public static void main(String[] args){
        launch(args);
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * This is the first method which uses primaryStage to set scene. FXML file will start loading from here. 
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
    
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/menu.fxml"));
            MenuController controller = new MenuController();
            controller.setStage(primaryStage);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setTitle("Cyberjaya Online Rental Management System");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        }catch(IOException e){
            e.printStackTrace();
        } 
        
    }  

}
