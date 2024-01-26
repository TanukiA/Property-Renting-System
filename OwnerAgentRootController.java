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
    This s a controller class used by property owner and agent. It is a tabpane that contains multiple FXML
    which is to perform different tasks.
*/
public class OwnerAgentRootController implements Initializable{

    Stage primaryStage;
    private String username, accType;

    @FXML private Tab propertyTab;

    @FXML private AnchorPane propertyPane;

    @FXML private Tab myPropertyTab;

    @FXML private AnchorPane myPropertyPane;

    @FXML private Tab rentalRequestTab;

    @FXML private AnchorPane rentalRequestPane;

    @FXML private Tab rentalListTab;

    @FXML private AnchorPane rentalListPane;

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
    Tabs for owner and agent include: property homepage, owned properties page, rental request page and
    manage rental page. 
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

        myPropertyTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if(myPropertyTab.isSelected()) {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ownPropertyPage.fxml"));
                        OwnerAgentPropertyController controller = new OwnerAgentPropertyController();
                        controller.initData(username, accType);
                        loader.setController(controller);
                        myPropertyPane = loader.load();
                        myPropertyTab.setContent(myPropertyPane);
                        controller.setTab(myPropertyTab);
                    }catch(IOException e){
                        System.out.println("My Properties page is not found");
                    }
                }
            }
        });

        rentalRequestTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if(rentalRequestTab.isSelected()) {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/listviewUI.fxml"));
                        ManageRentalRequestController controller = new ManageRentalRequestController();
                        controller.initData(username, accType);
                        loader.setController(controller);
                        rentalRequestPane = loader.load();
                        rentalRequestTab.setContent(rentalRequestPane);
                    }catch(IOException e){
                        System.out.println("Rental Request page is not found");
                    }
                }
            }
        });

        rentalListTab.setOnSelectionChanged(new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                if(rentalListTab.isSelected()) {
                    try{
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("view/listviewUI.fxml"));
                        ManageRentalController controller = new ManageRentalController(); 
                        controller.initData(username, accType);
                        loader.setController(controller);
                        rentalListPane = loader.load();
                        rentalListTab.setContent(rentalListPane);
                    }catch(IOException e){
                        System.out.println("List of Rental page is not found");
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

