import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import javafx.stage.*;

/*
    This is a controller class for menu, which is the first page or scene of the project.
    It contains register button, login button and continue as guest button.
*/
public class MenuController{ 

    private Stage primaryStage;

     /**
    * Author: Adeline Fong Li Ling
    *
    * Set the stage, which is primaryStage.
    */
    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

     /**
     * Author: Adeline Fong Li Ling
     *
     * When register button is clicked, user will go to the page of registration form.
    */
    @FXML
    private void registerButtonClicked(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/register.fxml"));
            RegisterController controller = new RegisterController();
            controller.setStage(primaryStage);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch(IOException e){
            System.out.println("Register page is not found");
        }
    }
    /**
    * Author: Adeline Fong Li Ling
    *
    * When login button is clicked, user will go to the page of login form.
    */

    @FXML
    private void loginButtonClicked(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/login.fxml"));
            LoginController controller = new LoginController();
            controller.setStage(primaryStage);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch(IOException e){
            System.out.println("Login page is not found");
        }
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * When guest button is clicked, user will go to the page of properties for guest (without renting feature)
     */
    @FXML
    private void guestButtonClicked(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/guestPage.fxml"));
            GuestPageController controller = new GuestPageController();
            controller.setStage(primaryStage);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch(IOException e){
            System.out.println("Guest page is not found");
        }
    }
    
}
