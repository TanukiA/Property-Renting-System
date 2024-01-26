import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
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

/*
    This is a controller class that is used by Property Owner/Agent to manage rental requests sent by tenants 
    (assign or decline tenant's rental request).
    Listview is used to display the rental requests and update the list if changed.
*/
public class ManageRentalRequestController implements Initializable{

    private Owner owner;
    private Agent agent;
    private String username, accType;
    private ArrayList<RentalRequest> rentalRequests;

    @FXML private ListView<CustomContent> listView;

    @FXML private Label notFoundLabel;

    /**
    * Author: Adeline Fong Li Ling
    *
    * Retrieve the rental requests that belong to owned properties of owner/agent. Retrieve that property 
    * and tenants' info and store into rentalRequests ArrayList.
    * If there is existing rental request, make a custom list for the listview. 
    */
    @Override
    public void initialize(URL url, ResourceBundle resources){
        if(accType.equals("Owner")){
            owner = new Owner();
            rentalRequests = owner.filterRentalRequest(username);
        }
        else if(accType.equals("Agent")){
            agent = new Agent();
            rentalRequests = agent.filterRentalRequest(username);
        }

        if(rentalRequests.isEmpty()){
            notFoundLabel.setText("No rental request is found");
            notFoundLabel.setVisible(true);
        }else{
            makeCustomList();
        }
    }

     /**
     * Author: Adeline Fong Li Ling
     *
     * Set the username and account type that are passed after login
     */
    public void initData(String username, String accType){
        this.username = username;
        this.accType = accType;
    }
    
    /**
    * Author: Adeline Fong Li Ling
    *
    * This is a method to set all data to the listview and set cell factory of the listview , prepare to
    * populate the rental requests info. It calls a private class named CustomListCell that manage the cells.
    */
    public void makeCustomList(){
        ObservableList<CustomContent> data = FXCollections.observableArrayList();
        String propertyName = null, projectType = null, propertyType = null, senderName = null, phoneNumber = null, email = null;
        
        for(int i=0; i<rentalRequests.size(); i++){
            for(Property p: Database.allProperties){
                if(p.getPropertyID() == rentalRequests.get(i).getPropertyID()){
                    propertyName = p.getPropertyName();
                    projectType = p.getProjectType();
                    propertyType = p.getPropertyType();
                    break;
                }
            }
            for(Tenant t: Database.allTenants){
                if(t.getUsername().equals(rentalRequests.get(i).getRequestSender())){
                    senderName = t.getName();
                    phoneNumber = t.getPhoneNumber();
                    email = t.getEmail();
                    break;
                }
            }
            data.add(new CustomContent(rentalRequests.get(i).getRentalRequestID(),  propertyName, projectType, propertyType, senderName, phoneNumber, email));

        }
        listView.setItems(data);
        listView.setCellFactory(new Callback<ListView<CustomContent>, ListCell<CustomContent>>() {
            @Override
            public ListCell<CustomContent> call(ListView<CustomContent> requestListView) {
                return new CustomListCell();
            }
        });
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * This is a private class that extends the ListCell of CustomContent type.
     * CustomContent is a class where the desired rental requests info are set and will be get from there as well.
     * Data fields in this private class are the items we require in listview of rental requests, such as label, 
     * button and hbox.
     */
    private class CustomListCell extends ListCell<CustomContent>{

        private HBox hbox;
        private Label rentalRequestID, propertyName, projectType, propertyType, name, phoneNumber, email;
        private Button assignBtn;
        private Button declineBtn;
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
            assignBtn = new Button("Assign Rental");
            declineBtn = new Button("Decline");
            assignBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // assign propertyID to tenant in rentalRequest.csv, change rental request status to approved,
                    // change property status to inactive & remove the cell
                    if(accType.equals("Owner")){
                        owner.assignRental(rentalRequests.get(getIndex()).getRentalRequestID(), rentalRequests.get(getIndex()).getPropertyID(),
                        rentalRequests.get(getIndex()).getRequestSender());
                    }else{
                        agent.assignRental(rentalRequests.get(getIndex()).getRentalRequestID(), rentalRequests.get(getIndex()).getPropertyID(),
                        rentalRequests.get(getIndex()).getRequestSender());
                    }
                    getListView().getItems().remove(getItem());
                }
            });
            declineBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // change rental request status to declined & remove the cell
                    if(accType.equals("Owner")){
                        owner.rentalDeclined(rentalRequests.get(getIndex()).getRentalRequestID());
                    }else{
                        agent.rentalDeclined(rentalRequests.get(getIndex()).getRentalRequestID());
                    }
                    getListView().getItems().remove(getItem());
                }
            });
            
            Image icon = Database.readIcon('e');
            ImageView imageView = new ImageView(icon);
            rentalRequestID = new Label("", imageView);
            rentalRequestID.setMinWidth(50);
            propertyName = new Label();
            propertyName.setFont(new Font("Verdana", 14));
            projectType = new Label();
            projectType.setFont(new Font("Verdana", 14));
            propertyType = new Label();
            propertyType.setFont(new Font("Verdana", 14));
            name = new Label();
            name.setFont(new Font("Verdana", 14));
            phoneNumber = new Label();
            phoneNumber.setFont(new Font("Verdana", 14));
            email = new Label();
            email.setFont(new Font("Verdana", 14));
            VBox vBox1 = new VBox(propertyName, projectType, propertyType);
            vBox1.setMinWidth(200);
            VBox vBox2 = new VBox(name, phoneNumber, email);
            vBox2.setMinWidth(160);
            hbox = new HBox(rentalRequestID, vBox1, vBox2, pane, assignBtn, declineBtn);
            HBox.setHgrow(pane, Priority.ALWAYS);
            hbox.setSpacing(16);
        }

         /**
        * Author: Adeline Fong Li Ling
        *
        * Override the updateItem() method. This method is called whenever the item in the cell changes.
        * It will update the list of rental requests if changed (assign/ decline) in the listview. 
        */
        @Override
        protected void updateItem(CustomContent item, boolean empty){

            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty){ 
                rentalRequestID.setText(String.valueOf(item.getID()));
                propertyName.setText(item.getPropertyName());
                projectType.setText("Under Project: " + item.getProjectType());
                propertyType.setText(item.getPropertyType());
                name.setText("Tenant: " + item.getName());
                phoneNumber.setText("Phone number: " + item.getPhoneNumber());
                email.setText("Email: " + item.getEmail());
                setGraphic(hbox);
            } 
        }
    }

}
