import java.io.*;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

/*
    Tenant class is a subclass of Person. It inherits variables of account data such as username, name, password, email,
    phoneNumber, account type. It also inherits methods such as setters, getters, addProperty() and deleteProperty().  
    However addProperty() and deleteProperty() cannot be used by Tenant, exception will be thrown when tried to call them.
    Tenant has features such as request for renting, bookmark a property, view browsing history and view rented properties.
*/
public class Tenant extends Person{
    /**
     * bookmarkHistory arraylist is sharing use for storing bookmark / history .
     * So it has the bookmarkHistoryMap, for determine which operation. 
     * Using map also avoid control coupling.
     */
    private ArrayList<Integer> bookmarkHistory;
    private ArrayList<RentalRequest> rentalRequests;
    private ArrayList<String> rentalRequestMessages;
    private ArrayList<Integer> rentedPropertyID;
    private ArrayList<Property> rentedProperties;
    private static HashMap<Integer,String> bookmarkHistoryMap = new HashMap<>();
    private Database db = new Database();

    public Tenant() {}

     /* 
    Author: Lew Zi Xuan

    Constructor for creating user account. It also initialize ArrayList of bookmarkHistory, rentalRequests and so on
    */
    public Tenant(String username, String name, String pwd, String email, String phoneNumber, String accType){
        setUsername(username);
        setName(name);
        setPwd(pwd);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setAccType(accType);
        bookmarkHistory = new ArrayList<>();
        rentalRequests = new ArrayList<>();
        rentalRequestMessages = new ArrayList<>();
        rentedPropertyID = new ArrayList<>();
        rentedProperties = new ArrayList<>();
    }

    /* 
    Author: Lew Zi Xuan

    Get bookmarkHistory which is the propertyID recorded
    */
    public ArrayList<Integer> getBookmarkHistory() {
        return bookmarkHistory;
    }

    /* 
    Author: Lew Zi Xuan

    Get rental requests fo a tenant
    */
    public ArrayList<RentalRequest> getRentalRequests() {
        return rentalRequests;
    }

    /* 
    Author: Lew Zi Xuan

    Get all rented properties in ID
    */
    public ArrayList<Integer> getRentedPropertyID() {
        return rentedPropertyID;
    }

    /* 
    Author: Lew Zi Xuan

    Get rental request messages which consist of String
    */
    public ArrayList<String> getRentalRequestMessages(){
        return rentalRequestMessages;
    }

    /*
    Author: Lew Zi Xuan

    This method return the index of targeted tenant in the static arraylist.
    */
    public static int getTargetTenantByUserName(String username) {
        for (int i = 0; i < Database.allTenants.size(); i++) {
            if (Database.allTenants.get(i).getUsername().equals(username))
                return i;
        }
        return -1;
    }

    /* 
    Author: Lew Zi Xuan

    This method return the Tenant object of targeted tenant
    */
    public static Tenant getTargetTenantObject(String username){
        for (int i = 0; i < Database.allTenants.size(); i++) {
            if (Database.allTenants.get(i).getUsername().equals(username))
                return Database.allTenants.get(i);
        }
        return null;
    }
    
    /**
     * Author: Lew Zi Xuan
     * 
     * This method update tenant edit profile result.
     */
    public void updateTenantProfile(String username, String name, String email, String phoneNumber)throws IOException {
        for(Tenant t: Database.allTenants){
            if(t.getUsername().equals(username)){
                t.setName(name);
                t.setEmail(email);
                t.setPhoneNumber(phoneNumber);
            }
        }
        db.saveTenant();
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method read the txt file content of the tenant's rental request.
     * In consist of sender and receiver information, and also request status, which is to be used for 
     * displaying notification.
     */
    public void readRentalRequestTxt() throws IOException {
        String fileName = "TenantRecord/".concat(getUsername()).concat("RentalRequest.txt");

        File temp = new File(fileName);
        if(temp.exists()) {
            FileReader fileReader = new FileReader(temp);
            try {
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    rentalRequestMessages.add(line);
                }
                fileReader.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }


    /**
     * Author: Lew Zi Xuan
     * 
     * This method is to filter save history when we view a property.
     * When we click on a particular property, it will switch to propertyUI.
     * Before we switch scene, we need to check whether the particular property already view before.
     * 1st, we need to readBookmarkHistory. type 2 indicate history.
     * after reading the file, it check the arraylist size 
     * if size != 0, means has history file before.
     * come to next step, loop through the arraylist
     * if the arraylist contain the propertyID, then return true.
     * else return false.
     */
    public boolean isHistory(int propertyID) throws IOException {
        readBookmarkHistory(2);
        if(bookmarkHistory.size() != 0) {
            for (Integer i : bookmarkHistory) {
                if (i == propertyID)
                    return true;
            }
        }
        return false;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * this method is use in bookmark property and initialized propertyUI.
     * it check the bookmarkHistory arraylist whether is contain propertyID.
     * if contain, return true.
     * else return false.
     */
    public boolean isBookmark(int propertyID){
        for(Integer i : bookmarkHistory){
            if(i == propertyID)
                return true;
        }
        return false;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method is used in synchronized bookmark history method in tenant.
     * It receive the property id, if there is match.
     * return true.
     * else return false.
     * 
     * (IF false, the tenant bookmarkHistory arraylist will remove the propertyID.)
     */
    public static boolean isExist(int propertyID){
        for(Property p : Database.allProperties){
            if(p.getPropertyID() == propertyID)
                return true;
        }
        return false;
    }
    
    /**
     * Author: Lew Zi Xuan
     * 
     * This method is for bookmark property.
     * 1st it read bookmark file, 1 indicate bookmark.
     * Check the arraylist size, if 0, indicate no bookmark before.
     * directly add the propertyId into the arraylist.
     * If arraylist size is not 0, indicate tenant bookmark before.
     * check whether the propertyID is bookmark, if true, then remove the propertyID return false.
     * if false, add into the arraylist return true.
     */
    public boolean bookmarkProperty(int propertyID) throws IOException {
        readBookmarkHistory(1);
        boolean result;

        //if not bookmark file created before
        //save directly.
        if(bookmarkHistory.size() == 0){
            bookmarkHistory.add(propertyID);
            result = true;
        }else {
            if(isBookmark(propertyID)){
                bookmarkHistory.remove(propertyID);
                result = false;
            }else {
                bookmarkHistory.add(propertyID);
                result = true;
            }
        }
        return result;
    }
    
    /**
     * Author: Lew Zi Xuan
     * 
     * This method is for send rental request.
     * Get the property owner /agent and save it into receiver variable.
     * if the receiver is not null, create a new request and add to the static arraylist and save.
     * Create a message with rental request ID + property name + request status.
     * the message is then save in the tenant rental request arraylist, then save the csv.
     */
    
    public void sendRentalRequest(int propertyID) throws IOException {

        //Get property owner or agent
        Person receiver = null;
        for(Owner o : Database.allOwners){
            ArrayList<Integer> ownedPropertyID = o.retrieveOwnPropertyID(o.getUsername());
            if(ownedPropertyID.contains(propertyID)) {
                receiver = o;
                break;
            }
        }
        if(receiver == null){
            for(Agent a : Database.allAgents){
                ArrayList<Integer> ownedPropertyID = a.retrieveOwnPropertyID(a.getUsername());
                if(ownedPropertyID.contains(propertyID)) {
                    receiver = a;
                    break;
                }
            }
        }
        if(receiver != null) {
            Database.allRentalRequests.add(new RentalRequest(propertyID, getUsername(), receiver.getUsername()));
            db.saveRentalRequest(); //rental request save.
        }

        //create a message to sender.
        int latestIndex = Database.allRentalRequests.size() - 1;
        String propertyName = Property.getPropertyByID(propertyID).getPropertyName();
        String message = "Request ID: " + Database.allRentalRequests.get(latestIndex).getRentalRequestID() +
                "," + "Property Name: " + propertyName + "," + "Request Status: Requesting";
        rentalRequestMessages.add(message);

        saveRentalRequestTxt(); //save tenant rental request.
    }
    
    /**
     * Author: Lew Zi Xuan
     * 
     * This method read Bookmark / history file
     */
    public void readBookmarkHistory(int type) throws IOException {
        bookmarkHistory = new ArrayList<Integer>();
        String fileName = getUsername();

        //Check type.
        if(bookmarkHistoryMap.containsKey(type))
            fileName = "TenantRecord/".concat(fileName.concat(bookmarkHistoryMap.get(type))).concat(".txt");

        //Check file exist.
        File temp = new File(fileName);
        if(temp.exists()){
            FileReader fileReader = new FileReader(temp);
            try {
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    bookmarkHistory.add(Integer.parseInt(line));
                }
                fileReader.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            synchronizeBookmarkHistory();
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method save the rental request in String to the text file in TenantRecord folder.
     * Each username can have one txt file.
     */
    public void saveRentalRequestTxt() throws IOException {
        StringBuilder sb = new StringBuilder();
        String fileName = "TenantRecord/".concat(getUsername()).concat("RentalRequest.txt");

        for(String str : rentalRequestMessages){
            sb.append(str).append("\n");
        }
        Files.write(Paths.get(fileName),sb.toString().getBytes());

    }


    /**
     * Author: Lew Zi Xuan
     * 
     * This method save the tenant's bookmark and history in String to the text file in TenantRecord folder.
     * Each username can have one txt file.
     */
    public void saveTenantBookmarkHistoryText(int type) throws IOException {
        StringBuilder sb = new StringBuilder();
        String fileName = getUsername();

        //Only save if the type is correct.
        if(bookmarkHistoryMap.containsKey(type)) {
            fileName = "TenantRecord/".concat(fileName.concat(bookmarkHistoryMap.get(type))).concat(".txt");

            for (Integer i : bookmarkHistory) {
                sb.append(i).append("\n");
            }
            Files.write(Paths.get(fileName), sb.toString().getBytes());
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method removes the propertyID in bookmarkHistory if condition not matched.
     */
    public void synchronizeBookmarkHistory(){
        bookmarkHistory.removeIf(i -> !isExist(i));
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method retrieve the rented properties in ID and add to the rentedPropertyID ArrayList
     */
    public void retrieveRentedPropertyID(String username){
        rentedPropertyID = new ArrayList<Integer>();
        for(int i=0; i<Database.allRentals.size(); i++){
            String[] tenantRow = Database.allRentals.get(i);
            if(tenantRow[0].equals(username)){
                for(int j=1; j<tenantRow.length; j++){
                    rentedPropertyID.add(Integer.parseInt(tenantRow[j]));
                }
            }
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method filter and get only the properties rented by a tenant. Return ArrayList of Property
     */
    public ArrayList<Property> filterRentedProperties(){
        rentedProperties = new ArrayList<Property>();
        for(Property p: Database.allProperties){
            for(int i=0; i<rentedPropertyID.size(); i++){
                if(p.getPropertyID() == rentedPropertyID.get(i)){
                    rentedProperties.add(p);
                }
            }
        }
        return rentedProperties;
    }

    /**
     * Author: Adeline Fong Li Ling
     * 
     * Determines whether a property is rented or not by a tenant. Return true if rented, false if not
     */
    public boolean rentedOrNot(int propertyID, String username){
        ArrayList<Integer> rentedPropertyID = getRentedPropertyID();
        if(rentedPropertyID.contains(propertyID)){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Retrieve the rental requests of a tenant and add to rentalRequests ArrayList
     */
    public void retrieveRentalRequests(String username){
        rentalRequests = new ArrayList<RentalRequest>(); 
        for(RentalRequest r: Database.allRentalRequests){
            if(r.getRequestSender().equals(username)){
                rentalRequests.add(r);
            }
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method set the map of bookmark and history
     */
    public static void setBookmarkHistoryMap(){
        bookmarkHistoryMap.put(1,"Bookmark");
        bookmarkHistoryMap.put(2,"History");
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * toString() method id overrrided to arrange the tenant's account data into comma-delimited String to save in csv.
     */
    @Override
    public String toString() {
        return getUsername() + "," + getName() + "," + getPwd() + "," + getEmail() + "," + getPhoneNumber() + "," + getAccType();
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Method implemented from Person, but prohbited from using, not applicable
     */
    public void addProperty(String projectType, String propertyType, String propertyName, String address, 
    int size, double rentPrice, double rentRate, int numOfRoom, int numOfBathroom, String ownedBy, String status, String state, ArrayList<String> facilities){
        throw new UnsupportedOperationException();
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Method implemented from Person, but prohbited from using, not applicable
     */
    public void deleteProperty(int propertyID){
        throw new UnsupportedOperationException();
    }
}

