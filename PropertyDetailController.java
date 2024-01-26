import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.ArrayList;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.control.Tab;
import java.io.File;
import java.nio.file.Paths;
import java.io.FilenameFilter;
import java.util.*;
import javafx.stage.Stage;
import javafx.stage.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.text.Font;

/*
    This is a controller class for displaying Property detail (Page where one of the property in list is chosen
    to view the detail such as facilities, address, contact info, property images, also provide features for 
    viewing comments and edit property (for owner/agent).
    This controller is used by Admin, Owner and Agent. Meanwhile, Tenant has a different controller because
    it has dictinct features such as send rental request, bookmark property and leaving comments. To avoid confusion,
    Tenant is using TenantController for similar interface.
*/
public class PropertyDetailController implements Initializable{

    private String username, accType, section;
    private Tab currentTab;
    private Stage editPropertyStage;
    private int propertyID;
    private ArrayList<Property> properties;
    private ArrayList<String[]> facilities;
    // store all facility labels into ArrayList, then assign facility text if there is text available
    private ArrayList<Label> facilityLabels = new ArrayList<Label>();

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
    @FXML private Button editButton;
    @FXML private Button backBtn;
    @FXML private VBox imageVBox;
    @FXML private Label faci1, faci2, faci3, faci4, faci5, faci6, faci7, faci8, faci9, faci10, faci11, faci12, 
    faci13, faci14, faci15, faci16, faci17, faci18;
    @FXML private VBox commentVBox;

    /* 
    Author: Adeline Fong Li Ling

    Initialize ArrayList of properties to be used. For Admin and Guest, they cannot use Edit button.
    For Owner and Agent, if the property chosen is owned by them, they are allowed to edit
    */
    @Override
    public void initialize(URL url, ResourceBundle resources){

        properties = new ArrayList<Property>(Database.allProperties);
        facilities = new ArrayList<String[]>(Database.allFacilities);

        if(accType.equals("Admin") || accType.equals("Guest")){
            editButton.setVisible(false);
            editButton.setDisable(true);

        }else if(accType.equals("Owner") || accType.equals("Agent")){
            boolean canEdit = false;
            for(int i=0; i<properties.size(); i++){
                if(properties.get(i).getPropertyID() == propertyID){
                    if(properties.get(i).getOwnedBy().equals(username)){
                        canEdit = true;
                        break;
                    }
                }
            }
            if(!canEdit){
                editButton.setVisible(false);
            }else{
                editButton.setVisible(true);
            }
        }

        List<Label> list = Arrays.asList(faci1, faci2, faci3, faci4, faci5, faci6, faci7, faci8, faci9, faci10,
        faci11, faci12, faci13, faci14, faci15, faci16, faci17, faci18);
        facilityLabels.addAll(list);

        Image icon = Database.readIcon('g');
        ImageView imageView = new ImageView(icon);
        backBtn.setGraphic(imageView);
        setPropertyPhotos();
        setAllText(properties, facilities);
        setPropertyComment();
    }

    /* 
    Author: Adeline Fong Li Ling

    Pass the username and account type of user to be used fo accessing data
    */
    public void initData(String username, String accType){
        this.username = username;
        this.accType = accType;
    }

    /* 
    Author: Adeline Fong Li Ling

    Set section from the previous scene, in order to back to the correct scene
    If the section is "home", when back button is pressed, user will go to home property page
    If the section is "own", when back button is pressed, user will go to own property page
    */
    public void setSection(String section){
        this.section = section;
    }

    /* 
    Author: Adeline Fong Li Ling

    Set tab from the previous scene, in order to load FXML on the same tab
    */
    public void setTab(Tab currentTab){
        this.currentTab = currentTab;
    }

    /* 
    Author: Adeline Fong Li Ling

    Pass propertyID from the property list when a property is chosen
    This propertyID is used to display property data in detail page
    */
    public void setPropertyID(int propertyID){
        this.propertyID = propertyID;
    }

    /* 
    Author: Pavitra, Adeline Fong Li Ling

    Set text on all the labels on property detail page.
    */
    public void setAllText(ArrayList<Property> propertiesPassed, ArrayList<String[]> facilitiesPassed){
        Property prop = null;
        for(int i=0; i<propertiesPassed.size(); i++){
            if(propertiesPassed.get(i).getPropertyID() == propertyID){
                prop = propertiesPassed.get(i);
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
        List<String> facilityList = new ArrayList<>();
        for(int i=0; i<facilitiesPassed.size(); i++){
            String[] eachProperty = facilitiesPassed.get(i);
            if(eachProperty[0].equals(String.valueOf(propertyID))){
                facilityList = Arrays.asList(eachProperty);  
            }
        }
           
        for(int i=1; i<facilityList.size(); i++){
            facilityLabels.get(i-1).setText(facilityList.get(i));
            facilityLabels.get(i-1).setFont(new Font(12.5));
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Set property photo(s) on the HBox, after that, set on the propertyVBox.
    FilenameFilter is used to obtain property photos that startsWith "propertyID_" in the propertyImages folder.
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
    
    /* 
    Author: Adeline Fong Li Ling

    Set property comment on the VBox. It will first read the comments into ArrayList. The ArrayList contains
    elements which are having comma to seperate tenant's username and comment content. Hence, each element
    is splitted and set to the Label to display.
    */
    private void setPropertyComment(){
        Property p = Property.getPropertyByID(propertyID);
        p.readComments();
        ArrayList<String> comments = p.getComments();
        for(int i=0; i<comments.size(); i++){
            String[] splitted = comments.get(i).split(",");
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

    /* 
    Author: Adeline Fong Li Ling

    When edit button is pressed by owner/agent, the edit property stage will comne out.
    After the user responded and close the stage, the editStatus is returned.
    If editStatus is true, it means that edit is done, so the updatePropertyDetail() is called.
    */
    @FXML
    private void editBtnPressed(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("view/addAndEditProperty.fxml"));
        AddAndEditPropertyController controller = new AddAndEditPropertyController();
        controller.initData(username, accType, propertyID);
        loader.setController(controller);
        Parent layout;
        try {
            layout = loader.load();
            Scene scene = new Scene(layout);
            editPropertyStage = new Stage();
            controller.setStage(editPropertyStage);
            editPropertyStage.initModality(Modality.WINDOW_MODAL);
            editPropertyStage.initStyle(StageStyle.UNDECORATED);
            editPropertyStage.setTitle("Edit property");
            editPropertyStage.setScene(scene);
            editPropertyStage.showAndWait();
            controller = loader.getController();
            boolean done = controller.getEditStatus();
            if(done){
                updatePropertyDetail();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method to read the updated ArrayList of property after edit is done.
    It will reset the text and property photos on the property detail page.
    */
    private void updatePropertyDetail(){
        properties = new ArrayList<Property>(Database.allProperties);
        facilities = new ArrayList<String[]>(Database.allFacilities);
        imageVBox.getChildren().clear();
        for(Label faci: facilityLabels){
            faci.setText("");
        }
        setPropertyPhotos();
        setAllText(properties, facilities);
    }
    
     /* 
    Author: Adeline Fong Li Ling

    When back button is press, it will go to either home property or own property according to the
    section value passed.
    */
    @FXML
    private void backBtnPressed(ActionEvent event) {

        if(section.equals("home")){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("view/property.fxml"));
                HomePropertyController controller = new HomePropertyController();
                controller.initData(username, accType);
                controller.setTab(currentTab);
                loader.setController(controller);
                Parent root = loader.load();
                currentTab.setContent(root);
            }catch(IOException e){
                System.out.println("Property page is not found");
            }

        }else if(section.equals("own")){
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("view/ownPropertyPage.fxml"));
                OwnerAgentPropertyController controller = new OwnerAgentPropertyController();
                controller.initData(username, accType);
                controller.setTab(currentTab);
                loader.setController(controller);
                Parent root = loader.load();
                currentTab.setContent(root);
            }catch(IOException e){
                System.out.println("My Properties page is not found");
            }
        }
        
    }
    
}
