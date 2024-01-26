import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.Path;


public class TenantProfileController implements Initializable{

    Stage stage;
    private String username;
    private Tenant tenant;
    private File file;

    @FXML
    private ImageView profilePic;

    @FXML
    private Label tenantUserName;

    @FXML
    private TextField tenantEmail, tenantPhoneNum, tenantName;

    @FXML
    private Button updateProfileBtn;

    @FXML
    private Button uploadPhotoBtn;

    @FXML
    private Label fileAttach;

    @Override
    public void initialize(URL url, ResourceBundle resources) {

        unsetSubmitBtn();
        uploadPhotoBtn.setVisible(false);
        fileAttach.setVisible(false);
        for(Tenant t: Database.allTenants){
            if(t.getUsername().equals(username)){
                tenant = t;
            }
        }
        setTenantName();
        setTenantUserName();
        setTenantEmail();
        setTenantPhoneNumber();
        setProfilePic();
        unsetSubmitBtn();
    }

    public void initData(String username){
        this.username = username;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    private void setTenantName() {
        tenantName.textProperty().setValue(tenant.getName());
        tenantName.setEditable(false);
    }

    private void setTenantUserName(){
        tenantUserName.textProperty().setValue(tenant.getUsername());
    }

    private void setTenantEmail() {
        tenantEmail.textProperty().setValue(tenant.getEmail());
        tenantEmail.setEditable(false);
    }

    private void setTenantPhoneNumber() {
        tenantPhoneNum.textProperty().setValue(tenant.getPhoneNumber());
        tenantPhoneNum.setEditable(false);
    }

    private void setProfilePic(){
        String location = "TenantProfilePic/".concat(tenant.getUsername()).concat(".png");
        Image profileRaw;
        try {
            profileRaw = new Image(location);
        }catch (IllegalArgumentException ex){
            profileRaw = new Image("icon/Profile.png");
        }
        profilePic.setImage(profileRaw);
    }

    private void setSubmitBtn(){
        updateProfileBtn.setVisible(true);
        updateProfileBtn.setDisable(false);
    }

    private void unsetSubmitBtn(){
        updateProfileBtn.setVisible(false);
        updateProfileBtn.setDisable(true);
    }

    @FXML
    private void startEditPressed(ActionEvent event){
        tenantName.setEditable(true);
        tenantEmail.setEditable(true);
        tenantPhoneNum.setEditable(true);
        setSubmitBtn();
        uploadPhotoBtn.setVisible(true);
        fileAttach.setVisible(true);
    }

    @FXML
    private void uploadPhotos(ActionEvent event) {
        file = null;
        FileChooser fc= new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
        file = fc.showOpenDialog(null);
        if(file != null)
            fileAttach.setText("Attached");
    }

    @FXML
    private void updateProfileBtnPressed(ActionEvent event) {

        tenantName.setEditable(false);
        tenantEmail.setEditable(false);
        tenantPhoneNum.setEditable(false);
        if(tenantName.getText().isEmpty() || tenantEmail.getText().isEmpty() || tenantPhoneNum.getText().isEmpty()){
            tenantName.textProperty().setValue(tenant.getName());
            tenantEmail.textProperty().setValue(tenant.getEmail());
            tenantPhoneNum.textProperty().setValue(tenant.getPhoneNumber());
        
        }else{
            try{
                tenant.updateTenantProfile(tenant.getUsername(), tenantName.getText(), tenantEmail.getText(),
                tenantPhoneNum.getText());
                // save photo if attached
                if(file != null){
                    String name = file.getName();
                    String extension = name.substring(1 + name.lastIndexOf(".")).toLowerCase();
                    Path target = Paths.get("TenantProfilePic", tenant.getUsername() + extension);
                    Files.copy(file.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
                }
            }catch(IOException e){
                e.printStackTrace();
            }

            file = null;
            tenantName.textProperty().setValue(tenantName.getText());
            tenantEmail.textProperty().setValue(tenantEmail.getText());
            tenantPhoneNum.textProperty().setValue(tenantPhoneNum.getText());
            setProfilePic();
        }

        unsetSubmitBtn();
        uploadPhotoBtn.setVisible(false);
        fileAttach.setVisible(false);
        file = null;
        fileAttach.setText("No attachment");
    }

    @FXML
    private void close(ActionEvent event) {
        stage.close();
    }

}
