import javafx.scene.control.CheckBox;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.*;
import java.util.ArrayList;

/*
    This is a controller class for popout facilities option to select during Add Property, Edit property and 
    Search property. It shows and wait for user response, obtain facilities arraylist and return. 
*/
public class FacilityOptionsController implements Initializable{

    private Stage stage = null;
    private ArrayList<String> facilities;
    
    @FXML
    private CheckBox jogTrack, security, gym, surau, basketballCourt, sauna, multiHall, playground, fencing, barbeque,
    mainEntrance, landscapeGarden, lounge, pool, cafe, carPark, dropOffPoint, jacuzzi;

     /**
     * Author: Adeline Fong Li Ling
     *
     * Initialize arraylist to store selected facilities
     */
    @Override
    public void initialize(URL url, ResourceBundle resources){
       facilities = new ArrayList<String>();
    }

     /**
     * Author: Adeline Fong Li Ling
     *
     * Set the facility option stage to show the facility options.
     */
    public void setStage(Stage stage){
        this.stage = stage;
    }

     /**
     * Author: Adeline Fong Li Ling
     *
     * This method is called after the controller class is being used. It is used to return facilities arraylist
     * after user respond.
     */
    public ArrayList<String> getFacilities(){
        return facilities;
    }

     /**
     * Author: Adeline Fong Li Ling
     *
     * When OK button is pressed, it will sum up all the checkbox selected and close the stage
     */
    @FXML
    private void buttonPressed(ActionEvent event) {
        sumCheckBox();
        stage.close();
    }
    /**
     * Author: Adeline Fong Li Ling
     *
     * This method will add all checkbox into single ArrayList. After that, it retrieved selected checkbox
     * ans add them to another list that can pass data to previous stage.
     */

    @FXML
    private void sumCheckBox(){
        ArrayList<CheckBox> checkboxes = new ArrayList<CheckBox>();
        checkboxes.add(jogTrack);
        checkboxes.add(security);
        checkboxes.add(gym);
        checkboxes.add(surau);
        checkboxes.add(basketballCourt);
        checkboxes.add(sauna);
        checkboxes.add(multiHall);
        checkboxes.add(playground);
        checkboxes.add(fencing);
        checkboxes.add(barbeque);
        checkboxes.add(mainEntrance);
        checkboxes.add(landscapeGarden);
        checkboxes.add(lounge);
        checkboxes.add(pool);
        checkboxes.add(cafe);
        checkboxes.add(carPark);
        checkboxes.add(dropOffPoint);
        checkboxes.add(jacuzzi);
        
        for(CheckBox c: checkboxes){
            if(c.isSelected()){
                facilities.add(c.getText());
            }
        }
        
    }
}
