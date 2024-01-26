import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

/*
    This is the controller class for popout dialog for notifying user about registration request sent,
    registration successful, registration failed and rental requested.
*/
public class PopOutDialogController implements Initializable{

    private Stage stage = null;
    private String labelID;

    @FXML
    private Button closeBtn;

    @FXML
    private Label registerRequestedLabel;

    @FXML
    private Label registerFailLabel;

    @FXML
    private Label registerSuccessLabel;

    @FXML
    private Label rentalRequestedLabel;

    /* 
    Author: Adeline Fong Li Ling

    When button is closed, the popout dialog is closed
    */
    @FXML
    private void closeButton(ActionEvent event) {
        stage.close();
    }

    /* 
    Author: Adeline Fong Li Ling

    Set visible for notifying labels when condition matched (according to labelID passed), then close stage
    */
    @Override
    public void initialize(URL url, ResourceBundle resources){
        if(labelID.equals("registerRequested")){
            registerRequestedLabel.setVisible(true);
        }else if(labelID.equals("fail")){
            registerFailLabel.setVisible(true);
        }else if(labelID.equals("success")){
            registerSuccessLabel.setVisible(true);
        }else if(labelID.equals("rentalRequested")){
            rentalRequestedLabel.setVisible(true);
        }
        closeStage();
    }

    /* 
    Author: Adeline Fong Li Ling

    Set the popup stage
    */
    public void setStage(Stage stage){
        this.stage = stage;
    }

    /* 
    Author: Adeline Fong Li Ling

    Set labelID from AdminRegisterController, RegisterController and TenantController for desired label
    */
    public void setLabelID(String labelID){
        this.labelID = labelID;
    }

    /* 
    Author: Adeline Fong Li Ling

    Close the stage
    */
    private void closeStage() {
        if(stage!=null) {
            stage.close();
        }
    }

}
