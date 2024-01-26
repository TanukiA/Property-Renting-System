import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/*
    This is a controller class for admin registration. It is seperated from other user's registration because the 
    features is different from them.  
*/
public class AdminRegisterController implements Initializable{

    private Stage popupStage;

    @FXML private TextField tf4;

    @FXML private TextField tf1;

    @FXML private TextField tf3;

    @FXML private PasswordField pwd;

    @FXML private Label errorLabel1;

    @FXML private Label errorLabel2;

    @FXML private TextField tf2;

     /* 
    Author: Adeline Fong Li Ling

    When submit button is pressed, this method will check for any empty field. If field is not empty, it checks
    the password length and ensure digit only for phone number. After that, check whether the username exists or not.
    If everything is valid, create new account. A stage is popout to notify success or fail.
    */
    @FXML
    private void submit(ActionEvent event) {
        errorLabel1.setText("");
        errorLabel2.setText("");
        
        if(tf1.getText().isBlank() || tf2.getText().isBlank() || pwd.getText().isBlank() || 
        tf3.getText().isBlank() || tf4.getText().isBlank()){
            errorLabel1.setText("All fields must be filled. Please try again.");
        
        }else{
            boolean allow1 = RegisterModel.checkPwdLength(pwd.getText());
            boolean allow2 = RegisterModel.checkPhoneNo(tf4.getText());
            if(!allow1)
                errorLabel1.setText("Minimum password length is 6 characters.");
            if(!allow2)
                errorLabel2.setText("Phone number should only contains digits.");

            if(allow1 && allow2){
                boolean valid = RegisterModel.checkExistence(tf2.getText());
                if(valid){
                     // register admin account 
                    RegisterModel.addAccount(tf2.getText(), tf1.getText(), pwd.getText(), tf3.getText(), tf4.getText(), "Admin", null);
                    
                    showAlert("success");
                    tf1.clear();
                    tf2.clear();
                    pwd.clear();
                    tf3.clear();
                    tf4.clear();

                }else{
                    showAlert("fail");
                }
            }
        }
    }

     /* 
    Author: Adeline Fong Li Ling

    Popup stage to notify success or fail for registration. It has a close button.
    */
    public void showAlert(String labelID){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/popOutDialog.fxml"));
        PopOutDialogController popupController = new PopOutDialogController();
        popupController.setLabelID(labelID);
        loader.setController(popupController);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            popupStage = new Stage();
            popupController.setStage(popupStage);
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setScene(scene);
            popupStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {

    }

}
