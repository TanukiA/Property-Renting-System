import java.util.ArrayList;

/* This class is used by multiple custom listviews. It stores the custom type of listcells. 
Variables are set in this external class for updating purpose and easy access to updated values for the View. 
Since this class is shared by multiple listviews, we create public class instead of using private class 
for each listview. 
Used by: list of user accounts, list of registration requests, list of rental requests, list of rental. 
*/ 
public class CustomContent extends Person{

    private String projectType, propertyName, propertyType;
    private int id;    // can be rentalRequestID or propertyID

    /* 
    Author: Adeline Fong Li Ling

    Constructor for user account listview & registration request listview (admin, tenant, owner & agent)
    */
    public CustomContent(String username, String name, String email, String phoneNumber, String accType, String owner){
        setUsername(username);
        setName(name);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setAccType(accType);
        setOwner(owner);
    }

    /* 
    Author: Adeline Fong Li Ling

    Constructor for rental request listview & rental listview (viewed by owner/agent)
    */
    public CustomContent(int id, String propertyName, String projectType, String propertyType, String name, String phoneNumber, String email){
        setID(id);  
        setPropertyName(propertyName);
        setProjectType(projectType);
        setPropertyType(propertyType);
        setName(name);
        setPhoneNumber(phoneNumber);
        setEmail(email);
    }

     /* 
    Author: Adeline Fong Li Ling

    Set rentalRequestID or propertyID for listview
    */
    public void setID(int id){
        this.id = id;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set project type of the property
    */
    public void setProjectType(String projectType){
        this.projectType = projectType;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set property name
    */
    public void setPropertyName(String propertyName){
        this.propertyName = propertyName;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set property type of property
    */
    public void setPropertyType(String propertyType){
        this.propertyType = propertyType;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get rentalRequestID or propertyID
    */
    public int getID(){
        return id;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get project type of property
    */
    public String getProjectType(){
        return projectType;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get property name value
    */
    public String getPropertyName(){
        return propertyName;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get property type value
    */
    public String getPropertyType(){
        return propertyType;
    }

    /* 
    Author: Adeline Fong Li Ling

    Implement abstract method of Person, but never been used
    */
    public void addProperty(String projectType, String propertyType, String propertyName, String address, 
    int size, double rentPrice, double rentRate, int numOfRoom, int numOfBathroom, String ownedBy, String status, String state, ArrayList<String> facilities){
        throw new UnsupportedOperationException();
    }

    /* 
    Author: Adeline Fong Li Ling

    Implement abstract method of Person, but never been used
    */
    public void deleteProperty(int propertyID){
        throw new UnsupportedOperationException();
    }
}
