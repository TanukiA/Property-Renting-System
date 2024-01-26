import javafx.fxml.FXML;
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

/*
    This is a controller class that is used by Property Owner/Agent to manage own properties's rental 
    (view and unassign tenant).
    Listview is used to display rental of owned properties and update the list if changed.
*/
public class ManageRentalController implements Initializable{
    
    private Owner owner;
    private Agent agent;
    private String username, accType;
    private ArrayList<Property> rentedProperties;
    private ArrayList<Tenant> rentingTenants;

    @FXML private Label notFoundLabel;

    @FXML private ListView<CustomContent> listView;

    /**
     * Author: Adeline Fong Li Ling
     *
     * Retrieve the owner or agent's owned properties' rental by using username. 
     * Retrieve the renting tenants' info and store in rentingTenants ArrayList.
     * If there is existing rental, make a custom list for the listview. 
     */
    @Override
    public void initialize(URL url, ResourceBundle resources){

        if(accType.equals("Owner")){
            owner = new Owner();
            rentedProperties = owner.retrieveRentals(username);
        }else if(accType.equals("Agent")){
            agent = new Agent();
            rentedProperties = agent.retrieveRentals(username);
        }

        if(rentedProperties.isEmpty()){
            notFoundLabel.setText("No current rental is found");
            notFoundLabel.setVisible(true);
        }else{
            if(accType.equals("Owner")){
                rentingTenants = owner.retrieveTenants();
            }else if(accType.equals("Agent")){
                rentingTenants = agent.retrieveTenants();
            }
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
     * populate the own properties' rental info. It calls a private class named CustomListCell that manage the cells.
     */
    public void makeCustomList(){
        
        ObservableList<CustomContent> data = FXCollections.observableArrayList();

        for(int i=0; i<rentedProperties.size(); i++){
            data.add(new CustomContent(rentedProperties.get(i).getPropertyID(), rentedProperties.get(i).getPropertyName(), 
            rentedProperties.get(i).getProjectType(), rentedProperties.get(i).getPropertyType(), rentingTenants.get(i).getName(),
            rentingTenants.get(i).getPhoneNumber(), rentingTenants.get(i).getEmail()));
        }
        listView.setItems(data);
        listView.setCellFactory(new Callback<ListView<CustomContent>, ListCell<CustomContent>>() {
            @Override
            public ListCell<CustomContent> call(ListView<CustomContent> rentalListView) {
                return new CustomListCell();
            }
        });
    }

     /**
     * Author: Adeline Fong Li Ling
     *
     * This is a private class that extends the ListCell of CustomContent type.
     * CustomContent is a class where the desired own properties' rental are set and will be get from there as well.
     * Data fields in this private class are the items we require in listview of own properties's rental, such as label, 
     * button and hbox.
     */
    private class CustomListCell extends ListCell<CustomContent>{

        private HBox hbox;
        private Label propertyID, propertyName, projectType, propertyType, name, phoneNumber, email;
        private Button removeBtn;
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
            removeBtn = new Button("Unassign Tenant"); 

            removeBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    // remove tenant from rental & remove the cell
                    if(accType.equals("Owner")){
                        owner.removeRentingTenant(rentedProperties.get(getIndex()).getPropertyID(), rentingTenants.get(getIndex()).getUsername());
                    
                    }else{
                        agent.removeRentingTenant(rentedProperties.get(getIndex()).getPropertyID(), rentingTenants.get(getIndex()).getUsername());
                    }
                    getListView().getItems().remove(getItem());
                }
            });

            Image rentIcon = Database.readIcon('f');
            ImageView imageView = new ImageView(rentIcon);
            propertyID = new Label("", imageView);
            propertyID.setMinWidth(50);
            //propertyID.setFont(new Font("Verdana", 14));
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
            hbox = new HBox(propertyID, vBox1, vBox2, pane, removeBtn);
            HBox.setHgrow(pane, Priority.ALWAYS);
            hbox.setSpacing(16);
        }

         /**
        * Author: Adeline Fong Li Ling
        *
        * Override the updateItem() method. This method is called whenever the item in the cell changes.
        * It will update the own properties' rental changed (unassigned) in the listview. 
        */
        @Override
        protected void updateItem(CustomContent item, boolean empty){

            super.updateItem(item, empty);
            setText(null);
            setGraphic(null);
            if (item != null && !empty){ 
                propertyID.setText(String.valueOf(item.getID()));
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


