import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import java.util.*;
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
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;

/* 
This is a controller class for property owner, agent and admin to add property; and also for property owner and agent
to edit property.
Add property and Edit property feature shares the same controller because they are submitting similar form.
*/
public class AddAndEditPropertyController implements Initializable{

    private Owner owner;
    private Agent agent;
    private Admin admin;
    private String username, accType;
    private Stage stage, facilityStage;
    private int idToEdit;
    private final String defaultStatus = "Active";
    private final String defaultState = "Activated";
    private List<File> files;
    private boolean editStatus;
    private ArrayList<String> selectedFacilities = new ArrayList<String>();

    @FXML private TextField projectTF, nameTF, sizeTF, priceTF, rateTF, ownerTF;

    @FXML private ComboBox<String> propertyTypeCombo;

    @FXML private ComboBox<Integer> bathCombo;

    @FXML private ComboBox<Integer> roomCombo;

    @FXML private TextArea addressTextArea;

    @FXML private Label promptLabel1, promptLabel2, ownerLabel, photoAttached, addOrEditLabel;

    @FXML private Button submitOrUpdateBtn;

    /* 
    Author: Adeline Fong Li Ling

    This is an initialize method that decides whether go to add or edit condition by using value passed: idToEdit. 
    For edit, idToEdit is the propertyID, while for add, idToEdit is -1.
    Observable list are used to set items for the comboBox.
    */
    @Override
    public void initialize(URL url, ResourceBundle resources){
        if(idToEdit == -1){
            addOrEditLabel.setText("Add Property");
            submitOrUpdateBtn.setText("Submit");
        }else{
            editStatus = false;
            addOrEditLabel.setText("Edit Property");
            submitOrUpdateBtn.setText("Update");
            setTextOnEditForm();
        }
        if(accType.equals("Owner")){
            owner = new Owner();
        }else if(accType.equals("Agent")){
            agent = new Agent();
        }else if(accType.equals("Admin")){
            ownerLabel.setVisible(true);
            ownerTF.setVisible(true);
            admin = new Admin();
        }
        ObservableList<String>propertyTypeList = FXCollections.observableArrayList("Condominium", "Apartment", "Service Residence", "Semi-D", 
        "Cluster House", "Bungalow", "Studio", "Terrace", "2-storey Terrace", "3-storey Terrace", "Flat", "Townhouse");
        propertyTypeCombo.setItems(propertyTypeList);
        ObservableList<Integer>roomNumberList = FXCollections.observableArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18);
        roomCombo.setItems(roomNumberList);
        ObservableList<Integer>bathNumberList = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        bathCombo.setItems(bathNumberList);

    }

    /* 
    Author: Adeline Fong Li Ling

    Pass stage from previous page.
    */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /* 
    Author: Adeline Fong Li Ling

    Pass username, account type(Admin/Owner/Agent) and idToEdit from previous stage
    */
    public void initData(String username, String accType, int idToEdit){
        this.username = username;
        this.accType = accType;
        this.idToEdit = idToEdit;
    }

    public boolean getEditStatus(){
        return editStatus;
    }

    /* 
    Author: Adeline Fong Li Ling

    Clear all previously selected facilities. Pop out a new stage to select facilities in checkboxes.
    */
    @FXML
    private void chooseFacilitiesPressed(ActionEvent event) {
        selectedFacilities.clear();
        selectedFacilities = showFacilities();
    }

    /* 
    Author: Adeline Fong Li Ling

    Upload photo(s) for property. Either JPG or PNG is allowed. When a file is selected, label shows "Attached"
    */
    @FXML
    private void uploadPhotos(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("JPG", "*.jpg"),
            new FileChooser.ExtensionFilter("PNG", "*.png")
        );
        files = fc.showOpenMultipleDialog(null);
        if(!files.isEmpty() && files != null)
            photoAttached.setText("Attached");
    }

    /* 
    Author: Adeline Fong Li Ling, Nurshara Batrisyia

    This is action when submit button is pressed (for Add), Update button is pressed (for Edit).
    It checks for any empty field, validate number format for size, rentPrice and rentRate.
    For Add Property only, property photo is a must to attach.
    For Edit Property, if the account type is Admin, it requires submitting owner's name to assign that property.
    If all inputs are valid, either addProperty() or editProperty() method is called. After that, clear all fields.
    */
    @FXML
    private void submitOrUpdatePressed(ActionEvent event) {

        promptLabel1.setText("");
        promptLabel2.setText("");

        boolean pass = true;
        if(projectTF.getText().isBlank() || nameTF.getText().isBlank() || sizeTF.getText().isBlank() || priceTF.getText().isBlank() || 
        rateTF.getText().isBlank() || propertyTypeCombo.getSelectionModel().isEmpty() || roomCombo.getSelectionModel().isEmpty() || 
        bathCombo.getSelectionModel().isEmpty() || addressTextArea.getText().isBlank() || selectedFacilities.isEmpty()){
            
            promptLabel1.setText("All sections must be filled. Please try again.");
            pass = false;
        }
        // For Add property, photos must be attached
        if(idToEdit == -1){
            if(files.isEmpty() || files == null){
                promptLabel2.setText("Please attach property photos.");
                pass = false;
            } 
        }
       
        if(accType.equals("Admin")){
            if(ownerTF.getText().isBlank()){
                promptLabel1.setText("All sections must be filled. Please try again.");
                pass = false;
            }
        }

        if(pass){
    
            boolean valid1 = false, valid2 = false;
            try{
                int size = Integer.parseInt(sizeTF.getText());
                double rentPrice = Double.parseDouble(priceTF.getText());
                double rentRate = Double.parseDouble(rateTF.getText());
                valid1 = true;
            
            }catch(NumberFormatException ex){
                promptLabel1.setText("Only number is allowed for size, rental price & rental rate");
            }

            if(accType.equals("Admin")){
                valid2 = RegisterModel.validOwner(ownerTF.getText());
                if(!valid2)
                    promptLabel2.setText("Please ensure owner's username is correct");
            }else{
                valid2 = true;
            }

            if(valid1 && valid2){
                int size = Integer.parseInt(sizeTF.getText());
                double rentPrice = Double.parseDouble(priceTF.getText());
                double rentRate = Double.parseDouble(rateTF.getText());
                if(idToEdit == -1){
                    addProperty(size, rentPrice, rentRate);
                    promptLabel1.setText("Property is added successfully!");
                }else{
                    editProperty(size, rentPrice, rentRate);
                    promptLabel1.setText("Property is edited successfully!");
                }

                projectTF.clear();
                nameTF.clear();
                addressTextArea.clear();
                sizeTF.clear();
                priceTF.clear();
                rateTF.clear();
                if(accType.equals("Admin"))
                    ownerTF.clear();
                if(idToEdit == -1)
                    files.clear();
                selectedFacilities.clear();
                photoAttached.setText("No attachment");
                propertyTypeCombo.getSelectionModel().clearSelection();
                roomCombo.getSelectionModel().clearSelection();
                bathCombo.getSelectionModel().clearSelection();
            }  
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    For Edit Property, it will set text when initialized. It retrieves existing property data and 
    display inside the textfield or select comboBox. 
    */
    private void setTextOnEditForm(){
        Property prop = null;
        for(int i=0; i<Database.allProperties.size(); i++){
            if(Database.allProperties.get(i).getPropertyID() == idToEdit){
                prop = Database.allProperties.get(i);
            }
        }
        projectTF.setText(prop.getProjectType());
        nameTF.setText(prop.getPropertyName());
        addressTextArea.setText(prop.getAddress());
        sizeTF.setText(String.valueOf(prop.getSize()));
        priceTF.setText(String.valueOf(prop.getRentPrice()));
        rateTF.setText(String.valueOf(prop.getRentRate())); 
        propertyTypeCombo.getSelectionModel().select(prop.getPropertyType());
        roomCombo.getSelectionModel().select(prop.getNumOfRoom());
        bathCombo.getSelectionModel().select(prop.getNumOfBathroom());
        if(accType.equals("Admin")){
            ownerTF.setText(prop.getOwnedBy());
        }
    }

    /* 
    Author: Nurshare Batrisyia, Adeline Fong Li Ling

    This is the method for Add Property. Call method according to account type.
    After adding property data in text, it will obtain propertyID generated and add property photos according to id. 
    */
    private void addProperty(int size, double rentPrice, double rentRate){
    
        if(accType.equals("Owner")){
            owner.addProperty(projectTF.getText(), propertyTypeCombo.getSelectionModel().getSelectedItem(), nameTF.getText(),
            addressTextArea.getText(), size, rentPrice, rentRate, roomCombo.getSelectionModel().getSelectedItem(), 
            bathCombo.getSelectionModel().getSelectedItem(), username, defaultStatus, defaultState, selectedFacilities);
            
        }else if(accType.equals("Agent")){
            agent.addProperty(projectTF.getText(), propertyTypeCombo.getSelectionModel().getSelectedItem(), nameTF.getText(),
            addressTextArea.getText(), size, rentPrice, rentRate, roomCombo.getSelectionModel().getSelectedItem(), 
            bathCombo.getSelectionModel().getSelectedItem(), username, defaultStatus, defaultState, selectedFacilities);
      
        }else if(accType.equals("Admin")){
            admin.addProperty(projectTF.getText(), propertyTypeCombo.getSelectionModel().getSelectedItem(), nameTF.getText(),
            addressTextArea.getText(), size, rentPrice, rentRate, roomCombo.getSelectionModel().getSelectedItem(), 
            bathCombo.getSelectionModel().getSelectedItem(), ownerTF.getText(), defaultStatus, defaultState, selectedFacilities);
        }
        int currentID = Database.allProperties.get(Database.allProperties.size()-1).getPropertyID();
        // save all property photo(s) with appropriate name
        addPhotos(currentID);
    }

    /* 
    Author: Nurshare Batrisyia, Adeline Fong Li Ling

    This is the method for Edit Property. Call method according to account type (applicable for Owner and Agent).
    If new photos are attached, replace the current photo with the new one.
    */
    private void editProperty(int size, double rentPrice, double rentRate){

        if(accType.equals("Owner")){
            owner.editProperty(idToEdit, projectTF.getText(), propertyTypeCombo.getSelectionModel().getSelectedItem(), nameTF.getText(),
            addressTextArea.getText(), size, rentPrice, rentRate, roomCombo.getSelectionModel().getSelectedItem(), 
            bathCombo.getSelectionModel().getSelectedItem(), selectedFacilities);
        
        }else if(accType.equals("Agent")){
            agent.editProperty(idToEdit, projectTF.getText(), propertyTypeCombo.getSelectionModel().getSelectedItem(), nameTF.getText(),
            addressTextArea.getText(), size, rentPrice, rentRate, roomCombo.getSelectionModel().getSelectedItem(), 
            bathCombo.getSelectionModel().getSelectedItem(), selectedFacilities);
        }

        if(files != null && !files.isEmpty()){
            updatePhotos(idToEdit);
        }
        editStatus = true;
    }

    /* 
    Author: Adeline Fong Li Ling

    A method for adding property images. The format of image is "propertyID_NoOfPhoto".
    This method save image to a folder called propertyImages  
    */
    public void addPhotos(int id){
        try{
            for(int i=0; i<files.size(); i++){
                String name = files.get(i).getName();
                String extension = name.substring(1+name.lastIndexOf(".")).toLowerCase();
                Path target = Paths.get("propertyImages", (id + "_" + (i+1) + "." + extension));
                Files.copy(files.get(i).toPath(), target);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    A method for editing property images. The format of image is "propertyID_NoOfPhoto".
    This method replace existing image in a folder called propertyImages  
    */
    public void updatePhotos(int id){
        try{
            for(int i=0; i<files.size(); i++){
                String name = files.get(i).getName();
                String extension = name.substring(1+name.lastIndexOf(".")).toLowerCase();
                Path target = Paths.get("propertyImages", (id + "_" + (i+1) + "." + extension));
                Files.copy(files.get(i).toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Pop out a new stage for selecting facilities. It will showAndWait for user to press OK, after that, 
    it will return ArrayList called selectedFacilities to be included for add or edit property.
    */
    private ArrayList<String> showFacilities(){
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/facilityOptions.fxml"));
        FacilityOptionsController controller = new FacilityOptionsController();
        loader.setController(controller);
        Parent layout;
        ArrayList<String> selectedFacilities = new ArrayList<String>();
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            facilityStage = new Stage();
            controller.setStage(facilityStage);
            facilityStage.initModality(Modality.WINDOW_MODAL);
            facilityStage.setTitle("Select Facilities");
            facilityStage.setScene(scene);
            facilityStage.showAndWait();
            controller = loader.getController();
            selectedFacilities = controller.getFacilities();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return selectedFacilities;
    }

    /* 
    Author: Adeline Fong Li Ling

    When close button is pressed, add/edit property stage is closed
    */
    @FXML
    private void close(ActionEvent event) {
        stage.close();
    }

}

  
