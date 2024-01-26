import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.io.File;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.control.ListCell;
import java.io.FilenameFilter;
import java.nio.file.Paths;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.stage.*;
import javafx.scene.control.Alert;

/*
    This is a main controller used by Tenant for activities within the system, includes loading property list,
    viewing bookmark, history and view notification on the top pane.
    Tenant has a connected scenes, unlike the use of tabPane by Admin, Owner and Agent. Hence it has
    different nature of controller. 
*/
public class TenantController implements Initializable{

    private String username, section;
    private int propertyID;
    private boolean isViewed;
    private Tenant tenant;
    private Stage primaryStage, facilityStage, profileStage;
    private ArrayList<Property> properties;
    private ArrayList<Property> searchResult;
    private ArrayList<String> selectedFacilities = new ArrayList<String>();
    private ObservableList<String> propertyTypes = FXCollections.observableArrayList("Any property type");
    private ObservableList<String> projectTypes = FXCollections.observableArrayList("Any project type");
    private ObservableList<Double> rentalRates = FXCollections.observableArrayList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
    private ObservableList<String> owners = FXCollections.observableArrayList("Any owner");
    private ObservableList<String> prices = FXCollections.observableArrayList("No price sorting", "Price from low to high", "Price from high to low");

    /** Top pane */
    @FXML private MenuButton functions;
    @FXML private MenuButton notification;
    @FXML private ImageView notificationIcon;
    @FXML private ImageView profilePic;
    @FXML private Button profileBtn;
    @FXML private Button homeBtn;

    /** view properties with search (homepage) */
    @FXML private ComboBox<String> propertyTypeCombo;
    @FXML private ComboBox<String> projectTypeCombo;
    @FXML private ComboBox<Double> minRateCombo;
    @FXML private ComboBox<Double> maxRateCombo;
    @FXML private ComboBox<String> ownerCombo;
    @FXML private ComboBox<String> priceCombo;
    @FXML private ToggleGroup statusGroup;
    @FXML private RadioButton inactiveRadio;
    @FXML private RadioButton allRadio;
    @FXML private RadioButton activeRadio;
    @FXML private Button addPropertyBtn;
    @FXML private Button saveReportBtn;
    @FXML private VBox propertyVBox;

    /** view Bookmark / History / Rented properties */
    @FXML private Label titleLabel;

    /** PropertyDetailPage */
    @FXML private Label propertyNameLabel;
    @FXML private Label projectLabel;
    @FXML private Label rentPrice;
    @FXML private Label rentRate;
    @FXML private Label sizeLabel;
    @FXML private Label propertyTypeLabel;
    @FXML private Label roomNum;
    @FXML private Label bathNum;
    @FXML private Label addressLabel;
    @FXML private Label phoneAndEmail;
    @FXML private Label contactName;
    @FXML private Button rentRequestBtn;
    @FXML private Button commentBtn;
    @FXML private Button backBtn;
    @FXML private VBox imageVBox;
    @FXML private Label faci1, faci2, faci3, faci4, faci5, faci6, faci7, faci8, faci9, faci10, faci11, faci12, 
    faci13, faci14, faci15, faci16, faci17, faci18;
    @FXML private VBox commentVBox;
    @FXML private ImageView bookmarkIcon;

     /**
     * Author: Adeline Fong Li Ling
     * 
     * Initialize content in homepage, a list of properties with search feature
     */
    public void setPropertiesWithSearch(){

        properties = Property.filterActivated();
        setPropertyVBox(properties);  // Display list of properties when page is first loaded
        Set<String> set = new HashSet<>();  // Set is used to remove duplicates

        // Set items for all comboBox 
        for(int i=0; i<properties.size(); i++)
            set.add(properties.get(i).getPropertyType());
        for(String s: set)
            propertyTypes.add(s);
        propertyTypeCombo.setItems(propertyTypes);
        set.clear();

        for(int i=0; i<properties.size(); i++)
            set.add(properties.get(i).getProjectType());
        for(String s: set)
            projectTypes.add(s);
        projectTypeCombo.setItems(projectTypes);
        set.clear();

        for(int i=0; i<properties.size(); i++)
            set.add(properties.get(i).getOwnedBy());
        for(String s: set)
            owners.add(s);
        ownerCombo.setItems(owners);
        set.clear();

        minRateCombo.setCellFactory(lv -> new MinRateCell());
        maxRateCombo.setCellFactory(lv -> new MaxRateCell());
        maxRateCombo.setItems(rentalRates);
        minRateCombo.setItems(rentalRates);
        priceCombo.setItems(prices);
    }

     /**
     * Author: Adeline Fong Li Ling
     * 
     * When the button for filter by facilities is pressed, a popup stage will appear to let tenant
     * to select desired facilities. Later will check if a property contains any of the selected facilities.
     */
    @FXML
    private void filterFacilitiesPressed(ActionEvent event) {
        selectedFacilities = showFacilities();
    }

     /**
     * Author: Adeline Fong Li Ling
     * 
     * This is a private class for managing the comboBox of minimum rental rate.
     * It is to update the item in comboBox, so that only valid item is enabled, others are disabled.
     * For example, if currently selected maximum rental rate is 3.0, then the minimum rate have to be
     * lower than 3.0, so the items from 3.0 onwards are disabled.
     */
    private class MinRateCell extends ListCell<Double>{

        MinRateCell(){
            maxRateCombo.valueProperty().addListener((obs, oldmaxRate, newMaxRate) -> updateDisableState());
        }

        @Override
        protected void updateItem(Double rate, boolean empty){
            super.updateItem(rate, empty);
            if(empty){
                setText(null);
            }else{
                setText(String.valueOf(rate));
                updateDisableState();
            }
        }

        private void updateDisableState(){
            boolean disable = getItem() != null && maxRateCombo.getValue() != null && 
            getItem().intValue() >= maxRateCombo.getValue();
            setDisable(disable);
            setOpacity(disable ? 0.5 : 1);
        }
    }

     /**
     * Author: Adeline Fong Li Ling
     * 
     * This is a private class for managing the comboBox of maximum rental rate.
     * It is to update the item in comboBox, so that only valid item is enabled, others are disabled.
     * For example, if currently selected minimum rental rate is 5.0, then the maximum rate have to be
     * higher then 5.0, so the items lower and equal to 5.0 are disabled.
     */
    private class MaxRateCell extends ListCell<Double>{

        MaxRateCell(){
            minRateCombo.valueProperty().addListener((obs, oldMaxRate, newMaxRate) -> updateDisableState());
        }

        @Override
        protected void updateItem(Double rate, boolean empty){
            super.updateItem(rate, empty);
            if(empty){
                setText(null);
            }else{
                setText(String.valueOf(rate));
                updateDisableState();
            }
        }

        private void updateDisableState(){
            boolean disable = getItem() != null && minRateCombo.getValue() != null && 
            getItem().intValue() <= minRateCombo.getValue();
            setDisable(disable);
            setOpacity(disable ? 0.5 : 1);
        }
    }

     /**
     * Author: Adeline Fong Li Ling
     * 
     * When search button is pressed, the currently displayed properties are cleared.
     * Then, all filters are done to get the selected result of properties. 
     * Tenant will only see properties that are in "Activated" state.
     * Sorting of rental price is done at the last is search result is not empty.
     */
    @FXML
    private void searchButtonPressed(ActionEvent event) {

        // clear all previous display
        propertyVBox.getChildren().clear();

        searchResult = new ArrayList<Property>();
        boolean displayAll = true;
        String selected = null;
        ArrayList<Property> filtered1 = new ArrayList<Property>();
        ArrayList<Property> filtered2 = new ArrayList<Property>();
        ArrayList<Property> filtered3 = new ArrayList<Property>();
        ArrayList<Property> filtered4 = new ArrayList<Property>();
        ArrayList<Property> filtered5 = new ArrayList<Property>();
        ArrayList<Property> filtered6 = new ArrayList<Property>();
        ArrayList<Property> filtered7 = new ArrayList<Property>();

        if(!propertyTypeCombo.getSelectionModel().isEmpty()){
            selected = propertyTypeCombo.getSelectionModel().getSelectedItem();
            if(!selected.equals("Any property type")){
                displayAll = false;
                filtered1 = Property.filterType(selected);
            }else{
                filtered1.addAll(properties);
            }
        }else{
            filtered1.addAll(properties);
        }
    
        if(!projectTypeCombo.getSelectionModel().isEmpty()){
            selected = projectTypeCombo.getSelectionModel().getSelectedItem();
            if(!selected.equals("Any project type")){
                displayAll = false;
                filtered2 = Property.filterProject(selected);
            }else{
                filtered2.addAll(properties);
            }
        }else{
            filtered2.addAll(properties);
        }
    
        if(!minRateCombo.getSelectionModel().isEmpty() || !maxRateCombo.getSelectionModel().isEmpty()){

            double selected1 = -1, selected2 = -1;
            if(!minRateCombo.getSelectionModel().isEmpty()){
                Double selectedDouble = minRateCombo.getSelectionModel().getSelectedItem();
                selected1 = selectedDouble.doubleValue();
            }
            if(!maxRateCombo.getSelectionModel().isEmpty()){
                Double selectedDouble = maxRateCombo.getSelectionModel().getSelectedItem();
                selected2 = selectedDouble.doubleValue();
            }
            displayAll = false;
            filtered3 = Property.filterRentalRate(selected1, selected2);
            
        }else{
            filtered3.addAll(properties);
        }
        
        if(!ownerCombo.getSelectionModel().isEmpty()){
            selected = ownerCombo.getSelectionModel().getSelectedItem();
            if(!selected.equals("Any owner")){
                displayAll = false;
                filtered4 = Property.filterOwner(selected);
            }else{
                filtered4.addAll(properties);
            }
        }else{
            filtered4.addAll(properties);
        }
        
        // If any of the selected facilities are included in property, that property is included in search
        if(!selectedFacilities.isEmpty()){
            displayAll = false;
            filtered5 = Property.filterFacility(selectedFacilities);
        }else{
            filtered5.addAll(properties);
        }
       
        RadioButton selectedRadio = (RadioButton)statusGroup.getSelectedToggle();
        String radioText = selectedRadio.getText();
        if(radioText.equals("Active property")){
            selected = "Active";
        }else if(radioText.equals("Inactive property")){
            selected = "Inactive";
        }
        if(!radioText.equals("All")){
            displayAll = false;
            filtered6 = Property.filterStatus(selected);
        }else{
            filtered6.addAll(properties);
        }

        // For tenant, will only browse Activated properties
        filtered7 = Property.filterActivated();
        
        if(displayAll){
            searchResult.addAll(properties);
        }else{
            // obtain the common properties of all filters
            filtered1.retainAll(filtered2);
            filtered1.retainAll(filtered3);
            filtered1.retainAll(filtered4);
            filtered1.retainAll(filtered5);
            filtered1.retainAll(filtered6);
            filtered1.retainAll(filtered7);

            for(int i=0; i<filtered1.size(); i++){
                searchResult.add(filtered1.get(i));
            }
        }
        
        if(!searchResult.isEmpty()){
            // after all filter, sort the result if sort price is selected
            if(!priceCombo.getSelectionModel().isEmpty()){
                selected = priceCombo.getSelectionModel().getSelectedItem();
                if(selected.equals("Price from low to high")){
                    searchResult = Property.sortACS_price(searchResult);
                }else if(selected.equals("Price from high to low")){
                    searchResult = Property.sortDESC_price(searchResult);
                }
            }
            // Display filtered properties
            setPropertyVBox(searchResult);  
            selectedFacilities.clear();  
        }
    }

     /**
     * Author: Adeline Fong Li Ling
     * 
     * Popput the new stage to choose the facilities using checkboxes, during search feature.
     * ArrayList of selected facilities is returned.
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
            facilityStage.setTitle("Filter by Facilities");
            facilityStage.setScene(scene);
            facilityStage.showAndWait();
            controller = loader.getController();
            selectedFacilities = controller.getFacilities();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return selectedFacilities;
    }

     /**
     * Author: Lew Zi Xuan, Adeline Fong Li Ling
     * 
     * Set the display of Hbox and Vbox of property list, including the property image. 
     * Filename Filter is used to get the image in either PNG or JPG format.
     * When the button "Click For Detail" is pressed, the method handlePropertyDetailBtn() is called.
     */
    private void setPropertyVBox(ArrayList<Property> propertyList){

        for (Property p : propertyList) {
            HBox propertyBox = new HBox();
            propertyBox.setSpacing(100);
            propertyBox.setPrefHeight(300);
            Pane pane1 = new Pane();
            pane1.setPrefHeight(16);
            Pane pane2 = new Pane();

            ImageView propertyImageView = null;
            try{
                String pathStr = Paths.get("propertyImages").toAbsolutePath().toString();
                File dir = new File(pathStr);
      
                // filter to get first image of each property
                FilenameFilter filter = new FilenameFilter(){
                    public boolean accept(File dir, String name){
                        return name.startsWith(p.getPropertyID() + "_1");
                    }
                };
       
                File[] files = dir.listFiles(filter);
                for(File f: files){
                    BufferedImage img = ImageIO.read(f);
                    Image propertyImage = SwingFXUtils.toFXImage(img, null);
                    propertyImageView = new ImageView(propertyImage);
                    propertyImageView.setFitHeight(180);
                    propertyImageView.setFitWidth(200);
                }
                
            }catch (Exception e){
                e.printStackTrace();
            }
           
            //Click on btn to view property.
            Label propertyName = new Label(p.getPropertyName());
            propertyName.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
            Label propertyType = new Label(p.getPropertyType());
            propertyType.setFont(new Font("Verdana", 14));
            Label project = new Label("Project: " + p.getProjectType());
            project.setFont(new Font("Verdana", 14));
            Label propertyRentPrice = new Label("RM " + String.format("%.2f", p.getRentPrice()));
            propertyRentPrice.setFont(new Font("Verdana", 16));
            Label status = new Label("[" + p.getStatus() + "]");
            status.setFont(new Font("Verdana", 12));

            Button viewDetailBtn = new Button("Click For Detail");
            viewDetailBtn.setFont(Font.font("Verdana", FontPosture.ITALIC, 14));
            viewDetailBtn.setUnderline(true);
            viewDetailBtn.setBackground(null);
            viewDetailBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    try{
                        handlePropertyDetailBtn(p.getPropertyID());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });

            VBox vbox1 = new VBox(propertyName, propertyType, status);
            VBox vbox2 = new VBox(project, propertyRentPrice, pane1, viewDetailBtn);
            vbox1.setMinWidth(185);
            vbox2.setMinWidth(185);
            propertyBox = new HBox(propertyImageView, vbox1, vbox2);
            HBox.setHgrow(pane2, Priority.ALWAYS);
            propertyBox.setSpacing(16);
            propertyVBox.getChildren().add(propertyBox);
        }
    }

     /**
     * Author: Lew Zi Xuan 
     * 
     * Switch to property detail view.
     * Before switching, the target property is 1st filter here.
     * We get the target tenant using the tenant username.
     * Check whether the target property is a history / view before.
     * if false, no history before, add the property id into the target tenant arraylist.
     * NOTE THAT, this method cannot put inside tenant because it required the propertyID from property object parameter.
     * It must set using the controller, so that property object(from parameter) has references.
     * After finish checking the history and initialized the propertyUI.
     * Target tenant read the bookmark file(note the method passing type 1).
     * After reading the file, we will set the bookmark icon according to the result from targetTenant.isBookmark()....
     */
    public void handlePropertyDetailBtn(int propertyID) throws Exception{
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/tenantPropertyDetailPage.fxml"));
            Stage stage = (Stage) profileBtn.getScene().getWindow();
            Parent root = loader.load();

            //Pass all data to the new controller.
            TenantController tenantController = loader.getController();
            tenantController.setTenant(this.username, this.tenant);
            tenantController.setPropertyID(propertyID);
            tenantController.setViewed(this.isViewed);
            tenantController.setSection(this.section);
            tenantController.setStage(stage);

            //filter history.
            Tenant targetTenant = Tenant.getTargetTenantObject(username);
            boolean isHistory = targetTenant.isHistory(propertyID);
            if(!isHistory)
                tenantController.addHistory(propertyID); //Save to target tenant history.

            //initialized the propertyUI
            tenantController.setProfilePic();
            tenantController.setPropertyDetail();
            tenantController.setNotificationBtn();
            tenantController.setNotificationIcon();
            
            targetTenant.readBookmarkHistory(1);
            tenantController.setBookmarkIcon(targetTenant.isBookmark(propertyID));
            
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

     /**
     * Author: Lew Zi Xuan
     * 
     * Initialize the property detail page. It will check whether a property is rented by a tenant.
     * If yes, rentRequestBtn will be hided.
     * Back button image, property images, all text of detail and comments are set. 
     */
    private void setPropertyDetail(){
        boolean rented = tenant.rentedOrNot(propertyID, username);
        if(rented){
            rentRequestBtn.setVisible(false);
        }

        Image icon = Database.readIcon('g');
        ImageView imageView = new ImageView(icon);
        backBtn.setGraphic(imageView);
        setPropertyPhotos();
        setAllText();
        setPropertyComment();
    }

     /**
     * Author: Lew Zi Xuan, Adeline Fong Li Ling
     * 
     * Set all text of property detail, including the contact info.
     * For facilities, all labels are store in a List so that the text can be assigned in loop.
     */
    private void setAllText(){
        Property prop = null;
        properties = Property.filterActivated();
        
        for(int i=0; i<properties.size(); i++){
            if(properties.get(i).getPropertyID() == propertyID){
                prop = properties.get(i);
            }
        }
        propertyNameLabel.setText(prop.getPropertyName());
        projectLabel.setText(prop.getProjectType());
        rentPrice.setText("RM " + String.format("%.2f", prop.getRentPrice()));
        rentRate.setText("(RM " + prop.getRentRate() + " /sqft.)");
        sizeLabel.setText(String.valueOf(prop.getSize()));
        propertyTypeLabel.setText(prop.getPropertyType());
        roomNum.setText(String.valueOf(prop.getNumOfRoom()));
        bathNum.setText(String.valueOf(prop.getNumOfBathroom()));
        addressLabel.setText(prop.getAddress());
        // set name, email and phone number of person who own this property
        Property p = new Property(prop.getOwnedBy()); 
        phoneAndEmail.setText(p.getContactNum() + " / " + p.getEmail());
        addressLabel.setText(prop.getAddress());
        contactName.setText(p.getNameToContact());
        
        // setText for facilities
        List<String> facilityList = null;
        for(String[] f: Database.allFacilities){
            String[] eachProperty = f;
            if(eachProperty[0].equals(String.valueOf(propertyID))){
                facilityList = Arrays.asList(eachProperty);  
            }
        }

        ArrayList<Label> facilityLabels = new ArrayList<Label>();
        List<Label> list = Arrays.asList(faci1, faci2, faci3, faci4, faci5, faci6, faci7, faci8, faci9, faci10,
        faci11, faci12, faci13, faci14, faci15, faci16, faci17, faci18);
        facilityLabels.addAll(list);
           
        for(int i=1; i<facilityList.size(); i++){
            facilityLabels.get(i-1).setText(facilityList.get(i));
            facilityLabels.get(i-1).setFont(new Font(12.5));
        }
    }

    /**
     * Author: Adeline Fong Li Ling
     * 
     * Set the property photos by getting all images using FilenameFilter.
     * Add to HBox and then to imageVBox.
     */
    private void setPropertyPhotos(){
        HBox imageHBox = new HBox();
        try{
            String pathStr = Paths.get("propertyImages").toAbsolutePath().toString();
            File dir = new File(pathStr);
  
            // filter to get all images of a property
            FilenameFilter filter = new FilenameFilter(){
                public boolean accept(File dir, String name){
                    return name.startsWith(propertyID + "_");
                }
            };
   
            File[] files = dir.listFiles(filter);
            for(File f: files){
                BufferedImage img = ImageIO.read(f);
                Image propertyImage = SwingFXUtils.toFXImage(img, null);
                ImageView propertyImageView = new ImageView(propertyImage);
                propertyImageView.setFitHeight(220);
                propertyImageView.setFitWidth(300);
                imageHBox.getChildren().add(propertyImageView);
            }
           
        }catch (Exception e){
            e.printStackTrace();
        }

        imageVBox.getChildren().add(imageHBox);
    }

    /**
     * Author: Adeline Fong Li Ling
     * 
     * Set the property comments. Split the username and content and put them into different labels to display
     */
    private void setPropertyComment(){
        ArrayList<String> comments = Database.readPropertyCommentTxt(propertyID);
        for(int i=0; i<comments.size(); i++){
            String [] splitted = comments.get(i).split(",");
            HBox commentHBox = new HBox();
            Label username = new Label(splitted[0] + ": ");
            username.setFont(new Font("Verdana", 14));
            Label content = new Label(splitted[1]);
            content.setFont(new Font("Verdana", 14));
            commentHBox.getChildren().add(username);
            commentHBox.getChildren().add(content);
            commentHBox.setSpacing(50);
            commentHBox.setMinWidth(100);
            commentHBox.setPrefHeight(50);
            commentVBox.setSpacing(16);
            commentVBox.getChildren().add(commentHBox);
        }
    }
    
    /**
     * Author: Lew Zi Xuan, Adeline Fong Li Ling
     * 
     * When back button is pressed in property detail page, it will go back to the list of properties.
     * They are 4 conditions, where the tenant go back to homepage, rented view, bookmark view or history view,
     * according to previous scene. section is used to store the previous section: all/rented/bookmark/history
     * to back to the correct scene.
     */
    @FXML
    private void backBtnPressed(ActionEvent event) {
       if(section.equals("all")){
            loadHomepage();
       }else if(section.equals("rented")){
            loadViewRented();
       }else if(section.equals("bookmark")){
            loadViewBookmark();
       }else if(section.equals("history")){
            loadViewHistories();
       }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Handle Bookmark button.
     * Create a information alert.
     * check whether the property is bookmark.
     * if is true, reset the bookmark icon, alert set header with bookmark success.
     * else reset the bookmark icon, alert set header with remove bookmark success.
     * save the arraylist to txt file.
     */
    @FXML
    private void handleBookmarkBtn() throws IOException {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        boolean result = tenant.bookmarkProperty(propertyID);

        if(result){
            setBookmarkIcon(true);
            alert.setHeaderText("Bookmark success.");
        }else {
            setBookmarkIcon(false);
            alert.setHeaderText("Remove bookmark success.");
        }

        tenant.saveTenantBookmarkHistoryText(1); //save
        alert.showAndWait();
    }

     /**
     * Author: Lew Zi Xuan 
     * 
     * Handle send rental request btn.
     * Get target tenant object using tenant index.
     * run method sendRentalRequest(propertyID).
     * Modify the isViewed flag to false, indicate there is an update.
     * Reset the notification btn & icon.
     */
    @FXML
    private void handleSendRentalRequestBtn() throws IOException {
        tenant.sendRentalRequest(propertyID);

        //Modify isViewed flag.
        isViewed = false;
        setNotificationBtn();
        setNotificationIcon();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Rental request sent success.");
        alert.setContentText("Click on bell to view the rental request status.");
        alert.showAndWait();
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Handle action of leave comments to a property.
     */
    @FXML
    private void handleLeaveCommentBtn() throws IOException {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);      //create alert

        Property property = Property.getPropertyByID(propertyID);

        property.readComments();                          //get previous records.
        String result = leaveComment();                //tenant leave comment.

        if(result.length() != 0) {
            property.getComments().add(username + "," + result);
            Database.savePropertyCommentTxt(propertyID, property.getComments()); // save
            alert.setHeaderText("Leave comment success.");          //Show leave comment success.
            commentVBox.getChildren().clear();  
            setPropertyComment();   // reset the comments
        }else
            alert.setHeaderText("Leave comment fail.");

        alert.showAndWait();
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method is for leave comment on the property.
     * Create text input dialog , and return the input.
     */
    public String leaveComment() {
        TextInputDialog inputDialog = new TextInputDialog("Enter your comment...");
        inputDialog.setHeaderText("Leave comment: ");
        inputDialog.showAndWait();

        return inputDialog.getEditor().getText();
    }
    
    /**
     * Author: Lew Zi Xuan
     * 
     * Handle isViewed action on mouse clicked. 
     * When notification is clicked and viewed, the icon change to viewed version
     */
    @FXML
    private void handleIsViewed(){
        setViewed(true);
        Image icon = new Image("icon/Notification.png");
        notificationIcon.setImage(icon);
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Popup a tenant profile stage when profile button is clicked
     */
    @FXML
    private void handleProfileBtn() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/tenantProfilePage.fxml"));
        TenantProfileController controller = new TenantProfileController();
        controller.initData(username);
        controller.setStage(profileStage);
        loader.setController(controller);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            profileStage = new Stage();
            controller.setStage(profileStage);
            profileStage.initModality(Modality.WINDOW_MODAL);
            profileStage.initStyle(StageStyle.UNDECORATED);
            profileStage.setTitle("User Profile");
            profileStage.setScene(scene);
            profileStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Switch scene to view rented properties. 
     */
    private void loadViewRented(){
        try {
            FXMLLoader loader = new FXMLLoader (getClass().getResource("view/tenantPropertyPage.fxml"));
            Stage stage = (Stage) functions.getScene().getWindow();
            Parent root = loader.load();
            TenantController tenantController = loader.getController();
            
            //Pass data to the controller.
            tenantController.setTenant(this.username, this.tenant);
            tenantController.setViewed(this.isViewed);
            tenantController.setSection("rented");
            tenantController.setStage(stage);

            //get tenant's rental
            
            tenant.retrieveRentedPropertyID(username);
            if(!tenant.getRentedPropertyID().isEmpty()){
                ArrayList<Property> rentedProperties = tenant.filterRentedProperties();
                //If the arraylist size is not 0, the arraylist is then pass for setting the property VBox.
                if(rentedProperties.size() != 0) { 
                    tenantController.setPropertyVBox(rentedProperties);
                }
            }

            //initialized the FXML
            tenantController.setProfilePic();
            tenantController.setNotificationBtn();
            tenantController.setNotificationIcon();
            tenantController.setTitleLabel("Rented Properties");

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

   /**
     * Author: Lew Zi Xuan
     * 
     * When MenuItem is clicked, load the view rented properties scene.
     */
    @FXML
    private void handleViewRented(ActionEvent event) {
        loadViewRented();
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * When MenuItem is clicked, switch to view bookmark scene.
     * After passing data to the controller, we need to get target tenant and read its bookmark file.
     * After that, we check the arraylist size whether is 0 or not.
     * if 0, indicate no bookmark before, no need to set propertyVBox.
     * if non 0, create a property arraylist, store all the property using tenant bookmarkHistory arraylist.
     * Setting the propertyVbox.
     * After that is the normal initialized for the UI.
     */
    private void loadViewBookmark(){
        try {
            FXMLLoader loader = new FXMLLoader (getClass().getResource("view/tenantPropertyPage.fxml"));
            Stage stage = (Stage) functions.getScene().getWindow();
            Parent root = loader.load();
            TenantController tenantController = loader.getController();
            
            //Pass data to the controller.
            tenantController.setTenant(this.username, this.tenant);
            tenantController.setViewed(this.isViewed);
            tenantController.setSection("bookmark");
            tenantController.setStage(stage);

            //get target tenant and read the bookmark file.
            tenant.readBookmarkHistory(1);
            ArrayList<Integer> bookmarkTenant = tenant.getBookmarkHistory();

            //If the arraylist size is not 0, there is a file.
            //From property arraylist get the bookmarked property.
            //The arraylist is then pass for setting the property VBox.
            if(bookmarkTenant.size() != 0) {
                ArrayList<Property> bookmark = new ArrayList<>();
                for (Integer id : bookmarkTenant) {
                    bookmark.add(Property.getPropertyByID(id));
                }
                tenantController.setPropertyVBox(bookmark);
            }

            //initialized the FXML
            tenantController.setProfilePic();
            tenantController.setNotificationBtn();
            tenantController.setNotificationIcon();
            tenantController.setTitleLabel("Bookmark");

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

     /**
     * Author: Lew Zi Xuan 
     * 
     * When MenuItem is clicked, load the view bookmark scene.
     */
    @FXML
    private void handleViewBookmark() throws Exception{
        loadViewBookmark();
    }

    /**
     * Author: Lew Zi Xuan 
     * 
     * When MenuItem is clicked, load the view history scene.
     * After passing data to the controller, we need to get target tenant and read its history file.
     * After that, we check the arraylist size whether is 0 or not.
     * if 0, indicate no history before, no need to set propertyVBox.
     * if non 0, create a property arraylist, store all the property using tenant bookmarkHistory arraylist.
     * Setting the propertyVbox.
     * After that is the normal initialized for the UI.
     */
    private void loadViewHistories(){
        try {
            FXMLLoader loader = new FXMLLoader (getClass().getResource("view/tenantPropertyPage.fxml"));
            Stage stage = (Stage) functions.getScene().getWindow();
            Parent root = loader.load();
            TenantController tenantController = loader.getController();
            
            //pass all data to the controller.
            tenantController.setTenant(this.username, this.tenant);
            tenantController.setViewed(this.isViewed);
            tenantController.setSection("history");
            tenantController.setStage(stage);

            //get target tenant and read the history file.
            tenant.readBookmarkHistory(2);
            ArrayList<Integer> historyTenant = tenant.getBookmarkHistory();

            //If the arraylist size is not 0, there is a file.
            //From property arraylist get the bookmarked property.
            //The arraylist is then pass for setting the property VBox.
            if(historyTenant.size() != 0){
                ArrayList<Property> histories = new ArrayList<>();
                for(Integer id : historyTenant){
                    histories.add(Property.getPropertyByID(id));
                }
                tenantController.setPropertyVBox(histories);
            }

            //initialized the FXML
            tenantController.setProfilePic();
            tenantController.setNotificationBtn();
            tenantController.setNotificationIcon();
            tenantController.setTitleLabel("History");

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

      /**
     * Author: Lew Zi Xuan
     * 
     * Switch to view histories when MenuItem is clicked.
     */
    @FXML
    private void handleViewHistories() throws Exception{
        loadViewHistories();
    }

     /**
     * Author: Lew Zi Xuan
     * 
     * Switch back to homepage of properties
     */
    @FXML
    private void handleHomeBtn(){
        loadHomepage();
    }

     /**
     * Author: Lew Zi Xuan
     * 
     * Load homepage
     */
    private void loadHomepage(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/tenantHomePage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) homeBtn.getScene().getWindow();

            TenantController tenantController = loader.getController();
            tenantController.setTenant(this.username, this.tenant);
            tenantController.setViewed(this.isViewed);
            tenantController.setSection("all");
            tenantController.setPropertiesWithSearch();
            tenantController.setStage(stage);
            
            tenantController.setProfilePic();
            tenantController.setNotificationBtn();
            tenantController.setNotificationIcon();

            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
    
     /**
     * Author: Lew Zi Xuan
     * 
     * Initialize notification button on top pane
     */
    public void setNotificationBtn() {
        notification.getItems().clear();
       
        ArrayList<String> senderNotification = tenant.getRentalRequestMessages();

        for(String str : senderNotification){
            String[] splitStr = str.split(",");
            String message = "";

            for(String s : splitStr)
                message = message.concat(s).concat("\n");

            MenuItem item = new MenuItem();
            item.textProperty().setValue(message);
            notification.getItems().add(item);
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Initialize notification icon on top pane
     */
    public void setNotificationIcon(){
        ArrayList<String> senderNotification = tenant.getRentalRequestMessages();
        Image icon;
        if(senderNotification.size() == 0 || isViewed){
            icon = new Image("icon/Notification.png");
        }else {
            icon = new Image("icon/NotificationNotView.png");
        }
        notificationIcon.setImage(icon);
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Initialize profile picture on top pane
     */
    public void setProfilePic(){
        String location = "TenantProfilePic/".concat(username).concat(".png");
        Image profileRaw;
        try {
            profileRaw = new Image(location);
        }catch (IllegalArgumentException ex){
            profileRaw = new Image("icon/Profile.png");
        }
        profilePic.setImage(profileRaw);
    }

    /**
     * Author: Adeline Fong Li Ling
     * 
     * Set section to be used by back button to determine back to which page that is previously in
     */
    public void setSection(String section){
        this.section = section;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Add history of tenant
     */
    private void addHistory(int propertyID) throws IOException {
        tenant.getBookmarkHistory().add(propertyID);
        tenant.saveTenantBookmarkHistoryText(2); //save
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Initialize bookmark icon on the propertyDetail page
     */
    private void setBookmarkIcon(boolean isBookmark) {
        Image imageIcon;
        if (isBookmark) {
            imageIcon = new Image("Icon/BookmarkDone.png");
        } else {
            imageIcon = new Image("Icon/BookmarkNone.png");
        }
        bookmarkIcon.setImage(imageIcon);
    }
    
    /**
     * Author: Lew Zi Xuan
     * 
     * Initialize the title of each scene
     */
    private void setTitleLabel(String text){
        titleLabel.textProperty().setValue(text);
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Pass necessary data to this controller from login
     */
    public void setTenant(String username, Tenant tenant) {
        this.username = username;
        this.tenant = tenant;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Set currently used propertyID
     */
    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Set whether notification is viewed or not
     */
    public void setViewed(boolean viewed) {
        isViewed = viewed;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Pass stage from previous, before homepage
     */
    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * When logout button is clicked, go to the menu page
     */
    @FXML
    private void logoutClicked(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/menu.fxml"));
            MenuController controller = new MenuController();
            controller.setStage(this.primaryStage);
            loader.setController(controller);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
       
    }
}