import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.Parent;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import java.nio.file.Paths;
import java.nio.file.Path;

/*
    This is a controlller class used during account registration for Tenant, Owner and Agent.
    It manages how the registration form perform and being submitted.

*/
public class RegisterController implements Initializable{ 

    Stage primaryStage, popupStage;
    private File file;

    @FXML private TextField tf1, tf2, tf3, tf4, tf5;

    @FXML private ComboBox<String> selectAcc;

    @FXML private Label errorLabel1;

    @FXML private Label errorLabel2;

    @FXML private Label errorLabel3;

    @FXML private PasswordField pwd;

    @FXML private Button fileChooser;

    @FXML private Label fileAttach;

     /* 
    Author: Adeline Fong Li Ling

    Initialize the comboBox to select account type, setItems to it.
    */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        ObservableList<String>accountTypeList = FXCollections.observableArrayList("Tenant", "Property owner", "Property agent");
        selectAcc.setItems(accountTypeList);
    }

     /* 
    Author: Adeline Fong Li Ling

    setStage, which is primaryStage that is passed from previous scene 
    */
    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

     /* 
    Author: Adeline Fong Li Ling

    Action when comboBox is selected. If Owner is selected, the fileChooser is enabled and textfield of hiring owner
    is disabled. If Agent is selected, the fileChooser is disabled and textfield of hiring owner is enabled.
    If Tenant is selected, both filChooser and textfield of hiring owner is not applicable, so both disabled.
    */
    @FXML
    private void accountTypeSelected(ActionEvent event) {
        if(selectAcc.getValue() != null){
            String accountType = selectAcc.getValue();
            if(accountType.equals("Property owner")){
                fileChooser.setDisable(false);
                tf5.setDisable(true);
            }else if(accountType.equals("Property agent")){
                fileChooser.setDisable(true);
                tf5.setDisable(false);
            }else if(accountType.equals("Tenant")){
                fileChooser.setDisable(true);
                tf5.setDisable(true);
            }
        }
    }

     /* 
    Author: Adeline Fong Li Ling

    Mehod for choosing file from directory, in PDF or DOC.
    It is the supporting document for Owner only, to be attached upon registration
    */
    @FXML
    private void chooseFile(ActionEvent event) {
        FileChooser fc= new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.PDF", "*.pdf"),
        new ExtensionFilter("Word Files", "*.doc", "*.docx"));
        file = fc.showOpenDialog(null);
        fileAttach.setText(file.getAbsolutePath());
    }

     /* 
    Author: Adeline Fong Li Ling

    When the submit button is pressed, it checks for empty field.
    It also checks for password length and phone number. For Agent, it will validate whether the hiring owner's
    username is in the record. Only when all condition are valid, addRegisterRequest() method is called.
    As for Owner, it will also save the attached file. Lastly, clear the form after submitted.
    */
    @FXML
    private void submitRequest(ActionEvent event) {

        errorLabel1.setText("");
        errorLabel2.setText("");
        
        if(tf1.getText().isBlank() || tf2.getText().isBlank() || pwd.getText().isBlank() || 
        tf3.getText().isBlank() || tf4.getText().isBlank() || selectAcc.getSelectionModel().isEmpty()){
            errorLabel1.setText("All fields must be filled. Please try again.");

            if(selectAcc.getValue().equals("Property agent")){
                if(tf5.getText().isBlank()){
                    errorLabel1.setText("All fields must be filled. Please try again.");
                }
            }else if(selectAcc.getValue().equals("Property owner")){
                if(file == null){
                    errorLabel2.setText("Document must be attached for property owner.");
                } 
            }
        
        }else{
            boolean allow1 = RegisterModel.checkPwdLength(pwd.getText());
            boolean allow2 = RegisterModel.checkPhoneNo(tf4.getText());
            boolean allow3 = true;
            if(selectAcc.getValue().equals("Property agent")){
                allow3 = RegisterModel.validOwner(tf5.getText());
            }
            if(!allow1)
                errorLabel1.setText("Minimum password length is 6 characters.");
            if(!allow2)
                errorLabel2.setText("Phone number should only contains digits.");
            if(!allow3)
                errorLabel3.setText("Please ensure owner's username is correct.");
            if(allow1 && allow2 && allow3){
                boolean valid = RegisterModel.checkExistence(tf2.getText());
                if(valid){
                    // send registration request to admin
                    RegisterModel.addRegisterRequest(tf2.getText(), tf1.getText(), pwd.getText(), tf3.getText(), tf4.getText(), 
                    selectAcc.getSelectionModel().getSelectedItem(), tf5.getText());

                    // save owner's document
                    if(selectAcc.getValue().equals("Property owner")){
                        try{
                            String name = file.getName();
                            String extension = name.substring(1+name.lastIndexOf(".")).toLowerCase();
                            Path target = Paths.get("ownerDocuments", tf2.getText() + "_doc." + extension);
                            Files.copy(file.toPath(), target);
                        }catch(IOException e){
                            e.printStackTrace();
                        } 
                    }
            
                    showAlert("registerRequested");
                    tf1.clear();
                    tf2.clear();
                    pwd.clear();
                    tf3.clear();
                    tf4.clear();
                    tf5.clear();
                    file = null;
                    fileAttach.setText("No attachment");
                    selectAcc.getSelectionModel().clearSelection();

                }else{
                    showAlert("fail");
                }
            }
        }
    }

     /* 
    Author: Adeline Fong Li Ling

    In case user want to login instead of register, login button is clicked to go to the login page.
    */
    @FXML
    private void loginButtonClicked(ActionEvent event) {
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

     /* 
    Author: Adeline Fong Li Ling

    Popout the dialog to notify user after submit button is clicked, either success or failed.
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
}
