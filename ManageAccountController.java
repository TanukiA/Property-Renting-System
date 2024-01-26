import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.fxml.Initializable;
import java.net.URL;
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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Paths;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.FileChooser;

/*
    This is a controller class that is used by Admin to manage user account (view and delete).
    Listview is used to display user account in alphabetical order and update the user account data.
*/
public class ManageAccountController implements Initializable{

    private Admin admin;
    private Stage stage;
    private ArrayList<Person> accounts;

    @FXML private Label notFoundLabel;

    @FXML private ListView<CustomContent> listView;

    /**
     * Author: Adeline Fong Li Ling
     *
     * Initialize the admin instance and read all accounts data into an ArrayList.
     * Then, check whether there is user account, if yes, proceed to making custom list for listview.
     */
    @Override
    public void initialize(URL url, ResourceBundle resources){

        admin = new Admin();
        admin.readAllAccounts();
        accounts = admin.getUserAccounts();
        if(accounts.isEmpty()){
            notFoundLabel.setText("No account is found");
            notFoundLabel.setVisible(true);
        }else{
            makeCustomList();
        }
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * This is a method to set all data to the listview and set cell factory of the listview , prepare to
     * populate the user account info. It calls a private class named CustomListCell that manage the cells.
     */
    public void makeCustomList(){
        ObservableList<CustomContent> data = FXCollections.observableArrayList();
        for(int i=0; i<accounts.size(); i++){
            data.add(new CustomContent(accounts.get(i).getUsername(), accounts.get(i).getName(), 
            accounts.get(i).getEmail(), accounts.get(i).getPhoneNumber(),
            accounts.get(i).getAccType(), accounts.get(i).getOwner()));
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
     * CustomContent is a class where the desired user account data are set and will be get from there as well.
     * Data fields in this private class are the items we require in listview of user accounts, such as label, button and hbox.
     */
    private class CustomListCell extends ListCell<CustomContent>{

        private HBox hbox;
        private Label name, username, email, phoneNumber, accType, owner;
        private Button deleteBtn, docBtn;
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
            Image trashIcon = Database.readIcon('c');
            ImageView imageView1 = new ImageView(trashIcon);
            deleteBtn = new Button("", imageView1); 
            docBtn = new Button("Get Owner File");

            deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Delete this account?");
                    Optional<ButtonType> action = alert.showAndWait();
                    
                    if(action.get() == ButtonType.OK){
                        // remove the cell & delete account
                        admin.deleteAccount(getIndex());
                        getListView().getItems().remove(getItem());
                    }
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
                                return name.startsWith(accounts.get(getIndex()).getUsername() + "_doc");
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

            Image accIcon = Database.readIcon('b');
            ImageView imageView2 = new ImageView(accIcon);
            name = new Label();
            name.setFont(new Font("Verdana", 14));
            username = new Label();
            username.setFont(new Font("Verdana", 14));
            email = new Label();
            email.setFont(new Font("Verdana", 14));
            phoneNumber = new Label();
            phoneNumber.setFont(new Font("Verdana", 14));
            accType = new Label("", imageView2);
            accType.setMinWidth(190);
            accType.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            owner = new Label();
            owner.setFont(new Font("Verdana", 14));
            VBox vBox = new VBox(name, username, email, phoneNumber);
            vBox.setMinWidth(250);
            hbox = new HBox(accType, vBox, owner, docBtn, pane, deleteBtn);
            HBox.setHgrow(pane, Priority.ALWAYS);
            hbox.setSpacing(16);
        }

         /**
        * Author: Adeline Fong Li Ling
        *
        * Override the updateItem() method. This method is called whenever the item in the cell changes.
        * It will update the account info changed (deleted) in the listview. 
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
