import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.Parent;
import java.io.IOException;
import java.io.File;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.FontPosture;
import javafx.scene.control.Tab;
import javafx.scene.control.Alert.AlertType;
import java.io.FilenameFilter;
import java.nio.file.Paths;

/*
    This is a controller class used by property owner/agent in viewing owned list of properties with Activate, 
    Deactivate and Delete features 
    (Activate: make property browsable by tenants, Deactivate: hide property from tenant's view)
*/
public class OwnerAgentPropertyController implements Initializable{

    private Owner owner;
    private Agent agent;
    private String username, accType;
    private final String section = "own";
    private ArrayList<Property> ownedProperties;
    private Tab currentTab;

    @FXML
    private VBox propertyVBox;

    /**
     * Author: Adeline Fong Li Ling
     *
     * Retrieve all owned properties of either owner or agent and assign to ownedProperties ArrayList.
     * If there is existing properties, set the VBox of property list.
     */
    @Override
    public void initialize(URL url, ResourceBundle resources){

        if(accType.equals("Owner")){
            owner = new Owner();
            ownedProperties = owner.retrieveOwnProperties(username);
        }else if(accType.equals("Agent")){
            agent = new Agent();
            ownedProperties = agent.retrieveOwnProperties(username);
        }

        if(!ownedProperties.isEmpty())
            setPropertyVBox(ownedProperties);
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * Pass username and account type after login
     */
    public void initData(String username, String accType){
        this.username = username;
        this.accType = accType;
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * Set the tab from OwnerAgentRootController. The currentTab value is used later in the propertyDetail page.
     * When the user click back button in propertyDetail page, the user can go back to this page on the same tab.
     * It enables loading FXML on the same tab as previous.
     */
    public void setTab(Tab currentTab){
        this.currentTab = currentTab;
    }

    /**
     * Author: Adeline Fong Li Ling
     *
     * A loop to set up HBox and Vbox for property list.
     * It contains multiple labels with setText regarding property info.
     * There are 3 buttons available for each property: Activate, Deactivate and Delete.
     * Whenever a button is clicked, there will be changes in the list. propertyVBox will be cleared and
     * set again when button is clicked to update values. 
     * When the button "Click For Detail" is clicked, the user will go the propertyDetail page
     * (by calling handlePropertyDetailBtn).
     */
    private void setPropertyVBox(ArrayList<Property> propertyList){

        for (Property p : propertyList) {
            HBox propertyBox = new HBox();
            propertyBox.setSpacing(100);
            propertyBox.setPrefHeight(300);
            Pane pane1 = new Pane();
            pane1.setPrefHeight(16);
            Pane pane2 = new Pane();

            Button flagStateBtn = new Button();
            if(p.getState().equals("Activated")){
                flagStateBtn.setText("Deactivate");
            }else if(p.getState().equals("Deactivated")){
                flagStateBtn.setText("Activate");
            }

            flagStateBtn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    if(p.getState().equals("Activated")){
                        if(accType.equals("Owner")){
                            owner.deactivateProperty(p.getPropertyID());
                        }else if(accType.equals("Agent")){
                            agent.deactivateProperty(p.getPropertyID());
                        }
                        // change the state of property from "Activated" to "Deactivated"
                        for(int i=0; i< propertyList.size(); i++){
                            if(propertyList.get(i).getPropertyID() == p.getPropertyID()){
                                propertyList.get(i).setState("Deactivated");
                                break;
                            }
                        }

                    }else if(p.getState().equals("Deactivated")){
                        if(accType.equals("Owner")){
                            owner.activateProperty(p.getPropertyID());
                        }else if(accType.equals("Agent")){
                            agent.activateProperty(p.getPropertyID());
                        }
                        // change the state of property from "Deactivated" to "Activated"
                        for(int i=0; i< propertyList.size(); i++){
                            if(propertyList.get(i).getPropertyID() == p.getPropertyID()){
                                propertyList.get(i).setState("Activated");
                                break;
                            }
                        }
                    }
                    // reset the property list displayed when a property state is changed
                    propertyVBox.getChildren().clear();
                    setPropertyVBox(propertyList);
                }
            });
            
            Button deleteBtn = new Button("Delete");
            
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
                            if(accType.equals("Owner")){
                                owner.deleteProperty(p.getPropertyID());
                            }else if(accType.equals("Agent")){
                                agent.deleteProperty(p.getPropertyID());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        // remove the deleted property from this list
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
            Label state = new Label("State: " + p.getState());
            state.setFont(new Font("Verdana", 13));

            Button viewDetailBtn = new Button("Click For Detail");
            viewDetailBtn.setFont(Font.font("Verdana", FontPosture.ITALIC, 14));
            viewDetailBtn.setUnderline(true);
            viewDetailBtn.setBackground(null);
            EventHandler<ActionEvent> propertyNameBtnHandler = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent){
                    try{
                        handlePropertyDetailBtn(p.getPropertyID());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };
            viewDetailBtn.setOnAction(propertyNameBtnHandler);

            VBox vbox1 = new VBox(propertyName, propertyType, status, state);
            VBox vbox2 = new VBox(project, propertyRentPrice, pane1, viewDetailBtn);
            vbox1.setMinWidth(185);
            vbox2.setMinWidth(185);
            propertyBox = new HBox(propertyImageView, vbox1, vbox2, pane2, flagStateBtn, deleteBtn);
            HBox.setHgrow(pane2, Priority.ALWAYS);
            propertyBox.setSpacing(16);
            propertyVBox.getChildren().add(propertyBox);
        }
    }

     /**
     * Author: Adeline Fong Li Ling
     *
     * Load propertyDetailPage for the selected property in the property list, using propertyID
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
