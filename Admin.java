import java.util.*;
import java.io.*;
import java.nio.file.Paths;

/* 
    Admin class is a subclass of Person. It inherits variables of account data such as username, name, password, email,
    phoneNumber, account type. It also inherits methods such as setters, getters, addProperty() and deleteProperty().  
*/
public class Admin extends Person{

    private ArrayList<Person> registerRequests = new ArrayList<Person>();
    private ArrayList<Person> userAccounts = new ArrayList<Person>();
    private Report report;
    private Database db = new Database();

    public Admin() {}

    /* 
    Author: Adeline Fong Li Ling

    This is a constructor used for account resgiatration, also used when reading admin from csv file 
    */
    public Admin(String username, String name, String pwd, String email, String phoneNumber, String accType){
        setUsername(username);
        setName(name);
        setPwd(pwd);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setAccType(accType);
    }

    /* 
    Author: Adeline Fong Li Ling

    Method that return all users' data in Person object 
    */
    public ArrayList<Person> getUserAccounts(){
        return userAccounts;
    }

    /* 
    Author: Adeline Fong Li Ling, Nurshara Batrisyia

    A method for adding a property. All data are passed by parameter. The propertyID is obtained by getting current 
    last ID in the file and add by 1. addFacilities() method is called for adding facilities in another file.
    */
    public void addProperty(String projectType, String propertyType, String propertyName, String address, 
    int size, double rentPrice, double rentRate, int numOfRoom, int numOfBathroom, String ownedBy, String status, 
    String state, ArrayList<String> facilities){

        int lastID = Database.allProperties.get(Database.allProperties.size()-1).getPropertyID();
        Database.allProperties.add(new Property(lastID + 1, projectType, propertyType, propertyName, address, size, rentPrice, rentRate, 
        numOfRoom, numOfBathroom, ownedBy, status, state));
        db.saveProperty();
        facilities.add(0, String.valueOf(lastID + 1));
        addFacilities(facilities);
    }

    /* 
    Author: Adeline Fong Li Ling

    A method for adding facilities of a property. Since ArrayList of facilities is passed, it is converted into
    array to match the write file method.
    */
    public void addFacilities(ArrayList<String> facilityToAdd){
        String[] facilityArr = facilityToAdd.toArray(new String[facilityToAdd.size()]);
        Database.allFacilities.add(facilityArr);
        db.saveFacilities();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method for deleting a property. It compares propertyID of delete with all properties, remove the Property object
    when propertyID is found. It also call deleteFacilities(), deletePhotos() and deleteComment() as to completely 
    delete the property data. 
    */
    public void deleteProperty(int propertyID){
        for(int i=0; i< Database.allProperties.size(); i++){
            if(Database.allProperties.get(i).getPropertyID() == propertyID){
                Database.allProperties.remove(i);
                break;
            }
        }
        db.saveProperty();
        deleteFacilities(propertyID);
        deletePhotos(propertyID);
        deleteComment(propertyID);
    }

    /* 
    Author: Adeline Fong Li Ling

    A method for deleting facilities of a property. It compares propertyID in String because Array of String is used.
    When the targeted propertyID is found at the first index, the array of String is removed.
    */
    public void deleteFacilities(int propertyID){
        for(int i=0; i<Database.allFacilities.size(); i++){
            String[] eachProperty = Database.allFacilities.get(i);
            if(eachProperty[0].equals(String.valueOf(propertyID))){
                Database.allFacilities.remove(eachProperty);
                break;
            }
        }
        db.saveFacilities();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method for deleting property images. When property is deleted, image is also deleted.
    FilenameFilter is used to search images of targeted property, which starts with "propertyID_", delete image
    by getting absolute path of propertyImage folder.
    */
    public void deletePhotos(int propertyID){
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
                f.delete();
            }
           
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    A method for deleting comment txt file when property is deleted. Comment file located inside folder "comments"
    is deleted if exist.
    */
    public void deleteComment(int propertyID){
        String fileName = "comments/" + propertyID + "_Comment.txt";
        File file = new File(fileName);
        if(file.exists()) {
            file.delete();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to add tenant account when registration is approved  
    */
    public static void addTenant(Tenant tenantToAdd){
        Database.allTenants.add(tenantToAdd);   
        Database db = new Database();
        db.saveTenant();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to add owner account when registration is approved 
    */
    public static void addOwner(Owner ownerToAdd){
        Database.allOwners.add(ownerToAdd);   
        Database db = new Database();
        db.saveOwner();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to add agent account when registration is approved
    */
    public static void addAgent(Agent agentToAdd){
        Database.allAgents.add(agentToAdd);   
        Database db = new Database();
        db.saveAgent();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to add admin account when registration is approved
    */
    public static void addAdmin(Admin adminToAdd){
        Database.allAdmins.add(adminToAdd);
        Database db = new Database();
        db.saveAdmin();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to delete registration request. It is used after both approved and declined condition.
    */
    public void deleteRequest(int index){
        registerRequests = Database.readRegisterRequest();
        registerRequests.remove(index);
        Database.saveRegisterRequest(registerRequests);
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to combine all user accounts into single arraylist for displaying user accounts.
    The list is sorted in alphabetical order of user's name.
    */
    public void readAllAccounts(){
        userAccounts.addAll(Database.allAdmins);
        userAccounts.addAll(Database.allOwners);
        userAccounts.addAll(Database.allAgents);
        userAccounts.addAll(Database.allTenants);
        // Sort all accounts in alphabetical order (ignore case)
        Collections.sort(userAccounts, new Comparator<Person>() {
            @Override
            public int compare(Person p1, Person p2) {
                return p1.getName().compareToIgnoreCase(p2.getName());
            }
        });
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to delete tenant account. The index for all userAccounts is passed, so it needs to compare and search
    matched tenant by ussername. After tenant is removed, it is also removed from userAccounts list.
    */
    public void deleteTenant(int index){
        for(int i=0; i<Database.allTenants.size(); i++){
            if(userAccounts.get(index).getUsername().equals(Database.allTenants.get(i).getUsername())){
                Database.allTenants.remove(i);
                userAccounts.remove(index);
                break;
            }
        }  
        db.saveTenant();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to delete owner account. The index for all userAccounts is passed, so it needs to compare and search
    matched owner by ussername. After owner is removed, it is also removed from userAccounts list.
    */
    public void deleteOwner(int index){
        for(int i=0; i<Database.allOwners.size(); i++){
            if(userAccounts.get(index).getUsername().equals(Database.allOwners.get(i).getUsername())){
                Database.allOwners.remove(i);
                userAccounts.remove(index);
                break;
            }
        }     
        db.saveOwner();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to delete agent account. The index for all userAccounts is passed, so it needs to compare and search
    matched agent by ussername. After agent is removed, it is also removed from userAccounts list.
    */
    public void deleteAgent(int index){
        for(int i=0; i<Database.allAgents.size(); i++){
            if(userAccounts.get(index).getUsername().equals(Database.allAgents.get(i).getUsername())){
                Database.allAgents.remove(i);
                userAccounts.remove(index);
                break;
            }
        }     
        db.saveAgent();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to delete admin account. The index for all userAccounts is passed, so it needs to compare and search
    matched admin by ussername. After admin is removed, it is also removed from userAccounts list.
    */
    public void deleteAdmin(int index){
        for(int i=0; i<Database.allAdmins.size(); i++){
            if(userAccounts.get(index).getUsername().equals(Database.allAdmins.get(i).getUsername())){
                Database.allAdmins.remove(i);
                userAccounts.remove(index);
                break;
            }
        }  
        db.saveAdmin();
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to delete account. It first get all user accounts regardless of account type.
    Then, it deletes account after validating the account type by instance.
    */
    public void deleteAccount(int index){
      
        readAllAccounts();
        if(userAccounts.get(index) instanceof Owner){
            deleteOwner(index);
        }else if(userAccounts.get(index) instanceof Agent){
            deleteAgent(index);   
        }else if(userAccounts.get(index) instanceof Tenant){
            deleteTenant(index);
        }else if(userAccounts.get(index) instanceof Admin){
            deleteAdmin(index);
        }
    }

    public int getIndex(Person user){
        for(int i=0; i<userAccounts.size(); i++){
            if(user.getUsername().equals(userAccounts.get(i).getUsername())) {
                return i;
            }
        }
        return -1;
    }

    /* 
    Author: Adeline Fong Li Ling

    A method to save report of properties by admin. The parameter get searchResult of properties in homepage and 
    the list of properties without search. Report class is initialized.
    */
    public void saveReport(ArrayList<Property> searchResult, ArrayList<Property> properties){
        report = new Report(searchResult, properties);
    }

     /* 
    Author: Adeline Fong Li Ling

    Return report generated
    */
    public Report getReport(){
        return report;
    }

     /* 
    Author: Adeline Fong Li Ling

    toString() method is overrided to arrange admin account data into a comma delimited string to save in csv
    */
    @Override
    public String toString() {
        return getUsername() + "," + getName() + "," + getPwd() + "," + getEmail() + "," + getPhoneNumber() + "," + getAccType();
    }

}
