import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

/*
    This is the controller class for Login which is used by all users (Admin, Tenant, Owner, Agent)
    It contains 2 textfields to fill in username and password. Will switch to homepage according to 
    account type, if input is valid.
*/
public class LoginController {

    Stage primaryStage;
    Database db;

    @FXML
    private TextField userTextField;

    @FXML
    private Label errorLabel;

    @FXML
    private PasswordField pwdField;

    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    /**
     * Author: Nurshara Batrisyia, Adeline Fong Li Ling
     *
     * When login button is clicked, it will first check the empty textfield, set error text if empty.
     * After that, check the existence of username and password pairs in the account files.
     * If matched, user will enter homepage according to account type, else user cannot proceed.
     * Before going to homepage, all database(csv files) to be used are read into static ArrayList.
     */
    @FXML
    void loginButtonClicked(ActionEvent event) throws IOException{

        if(userTextField.getText().isBlank() || pwdField.getText().isBlank()){
            errorLabel.setText("Please fill in both username and password.");
        }else{
            int role = Person.checkLogin(userTextField.getText(), pwdField.getText());
            switch(role){
                case -1:

                    errorLabel.setText("Account is not found. Please try again.");
                    break;
                
                case 0:
                    db = new Database();
                    db.readTenant();
                    db.readOwner();
                    db.readAgent();
                    db.readProperty();
                    db.readFacilities();
                    db.readRental();
                    db.readRentalRequests(); //Read Rental request object.
                    db.readAllTenantRentalRequest(); //Tenant rental request string message
                    Tenant.setBookmarkHistoryMap();
                    
                    Tenant tenant = Tenant.getTargetTenantObject(userTextField.getText());
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/tenantHomePage.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) userTextField.getScene().getWindow();
                        TenantController tenantController = loader.getController();
            
                        // pass tenant index to the tenant Controller.
                        tenantController.setTenant(userTextField.getText(), tenant);
                        tenantController.setViewed(false);
                        tenantController.setSection("all");
                        tenantController.setPropertiesWithSearch();
                        tenantController.setStage(stage);
                        
                        //initialize Tenant UI.
                        tenantController.setProfilePic();
                        tenantController.setNotificationBtn();
                        tenantController.setNotificationIcon();

                        stage.setScene(new Scene(root));
                        primaryStage.centerOnScreen();
                        stage.setResizable(false);
                        stage.show();
        
                    }catch (Exception ex){
                        ex.printStackTrace();
                    }
                    break;
                case 1: 

                    db = new Database(); 
                    db.readTenant();
                    db.readOwner();
                    db.readAgent();
                    db.readProperty();
                    db.readFacilities();
                    db.readRental();
                    db.readRentalRequests();
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ownerAgentRootPage.fxml"));
                        OwnerAgentRootController controller = new OwnerAgentRootController();
                        controller.setStage(primaryStage);
                        controller.initData(userTextField.getText(), "Owner");
                        loader.setController(controller);
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        primaryStage.setScene(scene);
                        primaryStage.centerOnScreen();

                    }catch(IOException e){
                        System.out.println("Owner page is not found");
                    }
                    break;
                case 2: 

                    db = new Database(); 
                    db.readTenant();
                    db.readOwner();
                    db.readAgent();
                    db.readProperty();
                    db.readFacilities();
                    db.readRental();
                    db.readRentalRequests();
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ownerAgentRootPage.fxml"));
                        OwnerAgentRootController controller = new OwnerAgentRootController();
                        controller.setStage(primaryStage);
                        controller.initData(userTextField.getText(), "Agent");
                        loader.setController(controller);
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        primaryStage.setScene(scene);
                        primaryStage.centerOnScreen();

                    }catch(IOException e){
                        System.out.println("Agent page is not found");
                    }
                    break;

                case 3: 

                    db = new Database(); 
                    db.readAdmin();
                    db.readTenant();
                    db.readOwner();
                    db.readAgent();
                    db.readProperty();
                    db.readFacilities();
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/adminRootPage.fxml"));
                        AdminRootController controller = new AdminRootController();
                        controller.setStage(primaryStage);
                        controller.initData(userTextField.getText(), "Admin");
                        loader.setController(controller);
                        Parent root = loader.load();
                        Scene scene = new Scene(root);
                        primaryStage.setScene(scene);
                        primaryStage.centerOnScreen();

                    }catch(IOException e){
                        System.out.println("Admin page is not found");
                    }
                    break;
            }
        }
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * In case user wants to register new account instead of proceed with login,
     * user can click to switch to registration page.
     */
    @FXML
    void registerButtonClicked(ActionEvent event) {
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

}