import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.*;
import javafx.scene.control.ListCell;
import javafx.util.Callback;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser;

/*
    This is a controller class which is used by Admin to manage registration request send from tenants, owners or agents.
    Admin will either approve or decline the registration. Listview is used to display the registration requests.
*/
public class ManageRegisterRequestController implements Initializable{

    private Admin admin; 
    private ArrayList<Person> registerRequests;
    private Stage stage;
    private ObservableList<CustomContent> data = FXCollections.observableArrayList();

    @FXML private ListView<CustomContent> listView;

    @FXML private Label notFoundLabel;

    /**
     * Author: Adeline Fong Li Ling
     *
     * Create the admin instance and read all registration request into ArrayList of Person.
     * If there is existing registration request, make a custom list for listview
     */
    @Override
    public void initialize(URL url, ResourceBundle resources){

        admin = new Admin();
        registerRequests = Database.readRegisterRequest();
        if(registerRequests.isEmpty()){
            notFoundLabel.setText("No register request is found");
            notFoundLabel.setVisible(true);
        }else{
            makeCustomList();
        }
    }
    
     /**
     * Author: Adeline Fong Li Ling
     *
     * This is a method to set all data to the listview and set cell factory of the listview , prepare to
     * populate the register request info. It calls a private class named CustomListCell that manage the cells.
     */
    public void makeCustomList(){
        for(int i=0; i<registerRequests.size(); i++){
            data.add(new CustomContent(registerRequests.get(i).getUsername(), registerRequests.get(i).getName(), 
            registerRequests.get(i).getEmail(), registerRequests.get(i).getPhoneNumber(),
            registerRequests.get(i).getAccType(), registerRequests.get(i).getOwner()));
        }
        listView.setItems(data);
        listView.setCellFactory(new Callback<ListView<CustomContent>, ListCell<CustomContent>>() {
            @Override
            public ListCell<CustomContent> call(ListView<CustomContent> accountListView) {
                return new CustomListCell();
            }
        });
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * This is a private class that extends the ListCell of CustomContent type.
     * CustomContent is a class where the desired register request data are set and will be get from there as well.
     * Data fields in this private class are the items we require in listview of resgiter requests such as label, button and hbox.
     */
    private class CustomListCell extends ListCell<CustomContent>{

        private HBox hbox;
        private Label name, username, email, phoneNumber, accType, owner;
        private Button approveBtn, declineBtn, docBtn;
        private Pane pane;

         /**
         * Author: Adeline Fong Li Ling
         *
         * This is a constructor which defines each listcell's contens and function. Value of labels and buttons
         * are set here.
        */
        public CustomListCell() {
            super();
            pane = new Pane();
            approveBtn = new Button("Approve");
            declineBtn = new Button("Decline"); 
            docBtn = new Button("Get Owner File");

            approveBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    
                    RegisterModel.addAccount(registerRequests.get(getIndex()).getName(), registerRequests.get(getIndex()).getUsername(), 
                    registerRequests.get(getIndex()).getPwd(), registerRequests.get(getIndex()).getEmail(), registerRequests.get(getIndex()).getPhoneNumber(),
                    registerRequests.get(getIndex()).getAccType(), registerRequests.get(getIndex()).getOwner());
                    admin.deleteRequest(getIndex());
                    
                    admin.deleteRequest(getIndex());
                    getListView().getItems().remove(getItem());
                }
            });

            declineBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    admin.deleteRequest(getIndex());
                    getListView().getItems().remove(getItem());
                }
            });

            docBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    
                    FileChooser fileChooser = new FileChooser();    
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.PDF", "*.pdf"),
                    new ExtensionFilter("Word Files", "*.doc", "*.docx"));
                    try{
                        String pathStr = Paths.get("ownerDocuments").toAbsolutePath().toString();
                        File dir = new File(pathStr);
              
                        // filter to get all images of a property
                        FilenameFilter filter = new FilenameFilter(){
                            public boolean accept(File dir, String name){
                                return name.startsWith(registerRequests.get(getIndex()).getUsername() + "_doc");
                            }
                        };
               
                        File[] files = dir.listFiles(filter);
                        File dest = fileChooser.showSaveDialog(stage);
                     
                        if(dest != null){
                            try {
                                Files.copy(files[0].toPath(), dest.toPath());
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            }
                        }     
                        
                    }catch (Exception ex){
                        Logger.getLogger(
                            ManageRegisterRequestController.class.getName()).log(
                                Level.SEVERE, null, ex
                            );
                    }
                }
            });

            Image requestIcon = Database.readIcon('a');
            ImageView imageView = new ImageView(requestIcon);
            name = new Label();
            name.setFont(new Font("Verdana", 14));
            username = new Label();
            username.setFont(new Font("Verdana", 14));
            email = new Label();
            email.setFont(new Font("Verdana", 14));
            phoneNumber = new Label();
            phoneNumber.setFont(new Font("Verdana", 14));
            accType = new Label("", imageView);
            accType.setMinWidth(190);
            accType.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            owner = new Label();
            owner.setFont(new Font("Verdana", 14));
            VBox vBox = new VBox(name, username, email, phoneNumber);
            vBox.setMinWidth(250);
            hbox = new HBox(accType, vBox, owner, docBtn, pane, approveBtn, declineBtn);
            HBox.setHgrow(pane, Priority.ALWAYS);
            hbox.setSpacing(16);
        }

         /**
        * Author: Adeline Fong Li Ling
        *
        * Override the updateItem() method. This method is called whenever the item in the cell changes.
        * It will update the registration request info changed (approved/declined) in the listview. 
        */
        @Override
        protected void updateItem(CustomContent item, boolean empty){

            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty){ 
                accType.setText(item.getAccType());
                name.setText("Name: " + item.getName());
                username.setText("Username: " + item.getUsername());
                email.setText("Email: " + item.getEmail());
                phoneNumber.setText("Phone number: " + item.getPhoneNumber());
                if(item.getAccType().equals("Property owner")){
                    docBtn.setVisible(true);
                    docBtn.setDisable(false);
                }else{
                    docBtn.setVisible(false);
                    docBtn.setDisable(true);
                }
                if(item.getAccType().equals("Property agent"))
                    owner.setText("Hired by: " + item.getOwner());
                setGraphic(hbox);
            } 
        }
    }
}
