import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/* 
    This is a controller class for Admin's View (TabPane that contains multiple FXML for different tabs).
    Different tabs will perform different functions.   
*/
public class AdminRootController implements Initializable{

    Stage primaryStage;
    private String username, accType;

    @FXML private Tab propertyTab;

    @FXML private AnchorPane propertyPane;

    @FXML private Tab requestTab;

    @FXML private AnchorPane requestPane;

    @FXML private Tab accountTab;

    @FXML private AnchorPane accountPane;

    @FXML private Tab createAdminTab;

    @FXML private AnchorPane createAdminPane;

    /* 
    Author: Adeline Fong Li Ling

    Set stage that is passed from previous scene.
    */
    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set username and account type that is obtained from login.
    */
    public void initData(String username, String accType){
        this.username = username;
        this.accType = accType;
    }

     /* 
    Author: Adeline Fong Li Ling

    A method to initialize tabpane. First tab, which is property.fxml is loaded and home icon is set.
    For each tab, event handler is set so that whenever user change tab, the targeted FXML file is loaded.
    Tabs for admin include: Property page, register request page, user account page and create admin page. 
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/property.fxml"));
            HomePropertyController controller = new HomePropertyController();
            controller.initData(username, accType);
            loader.setController(controller);
            propertyPane = loader.load();
            propertyTab.setContent(propertyPane);
            controller.setTab(propertyTab);
            
        }catch(IOException e){
            System.out.println("Property page is not found");
        }

        Image home = Database.readIcon('d');
        ImageView imgView = new ImageView(home);
        propertyTab.setGraphic(imgView);
    
        propertyTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if(propertyTab.isSelected()) {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/property.fxml"));
                        HomePropertyController controller = new HomePropertyController();
                        controller.initData(username, accType);
                        loader.setController(controller);
                        propertyPane = loader.load();
                        propertyTab.setContent(propertyPane);
                        controller.setTab(propertyTab);
                       
                    }catch(IOException e){
                        System.out.println("Property page is not found");
                    }
                }
            }
        });

        requestTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if(requestTab.isSelected()) {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/listviewUI.fxml"));
                        ManageRegisterRequestController controller = new ManageRegisterRequestController();
                        loader.setController(controller);
                        requestPane = loader.load();
                        requestTab.setContent(requestPane);
                    }catch(IOException e){
                        System.out.println("Register request page is not found");
                    }
                }
            }
        });

        accountTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if(accountTab.isSelected()) {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/listviewUI.fxml"));
                        ManageAccountController controller = new ManageAccountController();
                        loader.setController(controller);
                        accountPane = loader.load();
                        accountTab.setContent(accountPane);
                    }catch(IOException e){
                        System.out.println("Manage account page is not found");
                    }
                }
            }
        });

        createAdminTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if(createAdminTab.isSelected()) {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/adminRegister.fxml"));
                        AdminRegisterController controller = new AdminRegisterController(); 
                        loader.setController(controller);
                        createAdminPane = loader.load();
                        createAdminTab.setContent(createAdminPane);
                    }catch(IOException e){
                        System.out.println("Create admin page is not found");
                    }
                }
            }
        });
    }

    /* 
    Author: Adeline Fong Li Ling

    When logout button is clicked, back to menu page.
    */
    @FXML
    private void logoutClicked(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/menu.fxml"));
            MenuController controller = new MenuController();
            controller.setStage(primaryStage);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

}
