import java.util.*;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.net.URL;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.*;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Parent;
import java.io.IOException;
import java.io.File;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.scene.control.ButtonType;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import javafx.scene.control.Tab;
import javafx.scene.control.ListCell;
import java.io.FilenameFilter;
import java.nio.file.Paths;

/*
    This is a controlller class of Property Homepage, used by Admin, Agent and Owner.
    It contains search method to filter properties with desired condition(s), which include comboBox,
    radio buttons and checkbox to select condition. A VBox is used to display the property list.
    User can click on "Click For Detail" of a property to proceed to the property detail page. 
*/
public class HomePropertyController implements Initializable{

    private Admin admin;
    private String username, accType;
    private Stage stage, facilityStage, addPropertyStage;
    private Tab currentTab;
    private final String section = "home";
    private ArrayList<Property> properties;
    private ArrayList<Property> searchResult;
    private ArrayList<String> selectedFacilities = new ArrayList<String>();
    private ObservableList<String> propertyTypes = FXCollections.observableArrayList("Any property type");
    private ObservableList<String> projectTypes = FXCollections.observableArrayList("Any project type");
    private ObservableList<Double> rentalRates = FXCollections.observableArrayList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0);
    private ObservableList<String> owners = FXCollections.observableArrayList("Any owner");
    private ObservableList<String> prices = FXCollections.observableArrayList("No price sorting", "Price from low to high", "Price from high to low");

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

    /**
     * Author: Adeline Fong Li Ling
     *
     * Firstly, check for the account type paased. If account is Owner or Agent, the addPropertyBtn is not
     * allowed for them, while for Admin, setVisible for addPropertyBtn and saveReportBtn.
     */
    @Override
    public void initialize(URL url, ResourceBundle resources) {

        if(accType.equals("Owner") || accType.equals("Agent")){
            addPropertyBtn.setVisible(true);
            saveReportBtn.setVisible(false);
        }else if(accType.equals("Admin")){
            addPropertyBtn.setVisible(true);
            saveReportBtn.setVisible(true);
            admin = new Admin();
        }
   
        // get all properties
        properties = new ArrayList<Property>(Database.allProperties);
        
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
     * Pass the username and account type all along from login to be used to access database
     */
    public void initData(String username, String accType){
        this.username = username;
        this.accType = accType;
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * Set the currentTab to keep track of the tab when change scene
     */
    public void setTab(Tab currentTab){
        this.currentTab = currentTab;
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * When the button for facilities is pressed, pop out a stage with multiple checkboxes to select facilities
     * to filter property result
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
        
        if(displayAll){
            searchResult.addAll(properties);
        }else{
            // obtain the common properties of all filters
            filtered1.retainAll(filtered2);
            filtered1.retainAll(filtered3);
            filtered1.retainAll(filtered4);
            filtered1.retainAll(filtered5);
            filtered1.retainAll(filtered6);

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
     * When save report button is pressed by Admin, it can generate a report in a table form that is
     * listing all the searched properties or all properties if the search is yet to be executed.
     * The report in txt can be saved to directory by using filechooser. 
     */
    @FXML
    private void saveReportPressed(ActionEvent event){
    
        admin.saveReport(searchResult, properties);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        
        //Show save file dialog
        File file = fileChooser.showSaveDialog(stage);
             
        if(file != null){
            admin.getReport().saveTable(file);
        }
    }

     /**
     * Author: Adeline Fong Li Ling
     * 
     * When add property button is pressed, load the stage of addAndEditProperty which is FXML shared by
     * both Add property and Edit property.
     */
    @FXML
    private void addPropertyPressed(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/addAndEditProperty.fxml"));
        AddAndEditPropertyController controller = new AddAndEditPropertyController();
        controller.initData(username, accType, -1);
        loader.setController(controller);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            addPropertyStage = new Stage();
            controller.setStage(addPropertyStage);
            addPropertyStage.initModality(Modality.WINDOW_MODAL);
            addPropertyStage.initStyle(StageStyle.UNDECORATED);
            addPropertyStage.setTitle("Add property");
            addPropertyStage.setScene(scene);
            addPropertyStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Author: Adeline Fong Li Ling
     * 
     * Popout the new stage to choose the facilities using checkboxes, during search feature.
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
            Button deleteBtn = new Button("Delete");
            if(accType.equals("Admin")){
                deleteBtn.setVisible(true);
                deleteBtn.setDisable(false);
            }else{
                deleteBtn.setVisible(false);
                deleteBtn.setDisable(true);
            }

            deleteBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation Dialog");
                    alert.setHeaderText(null);
                    alert.setContentText("Delete this property?");
                    Optional<ButtonType> action = alert.showAndWait();
                    
                    if(action.get() == ButtonType.OK){
                        try{
                            admin.deleteProperty(p.getPropertyID());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        for(int i=0; i< propertyList.size(); i++){
                            if(propertyList.get(i).getPropertyID() == p.getPropertyID()){
                                propertyList.remove(i);
                                break;
                            }
                        }
                        // reset the property list displayed when a property is deleted
                        propertyVBox.getChildren().clear();
                        setPropertyVBox(propertyList);
                    }
                }
            });

            ImageView propertyImageView = null;
            try{
                String pathStr = Paths.get("propertyImages").toAbsolutePath().toString();
                File dir = new File(pathStr);
      
                // filter to get all images of a property
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
            EventHandler<ActionEvent> viewDetailBtnHandler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent){
                    try{
                        handlePropertyDetailBtn(p.getPropertyID());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            viewDetailBtn.setOnAction(viewDetailBtnHandler);

            VBox vbox1 = new VBox(propertyName, propertyType, status);
            VBox vbox2 = new VBox(project, propertyRentPrice, pane1, viewDetailBtn);
            vbox1.setMinWidth(185);
            vbox2.setMinWidth(185);
            propertyBox = new HBox(propertyImageView, vbox1, vbox2, pane2, deleteBtn);
            HBox.setHgrow(pane2, Priority.ALWAYS);
            propertyBox.setSpacing(16);
            propertyVBox.getChildren().add(propertyBox);
        }
    }

     /**
     * Author: Adeline Fong Li Ling
     * 
     * When the button "Click For Detail" is clicked, the propertyDetail page is loaded to show more detail
     * about the property.
     */
    private void handlePropertyDetailBtn(int propertyID) {
       
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/propertyDetailPage.fxml"));
            PropertyDetailController controller = new PropertyDetailController();
            controller.initData(username, accType);
            controller.setSection(section);
            controller.setPropertyID(propertyID);
            controller.setTab(currentTab);
            loader.setController(controller);
            Parent root = loader.load();
            currentTab.setContent(root);

        }catch(IOException e){
            System.out.println("Property detail page is not found");
        }
    }
}
