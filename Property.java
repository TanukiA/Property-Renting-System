import java.util.*;

/*
    Property has many data such as size, propertyTyoe, address, status and so on.
    propertyID is considered unique in file to determine the property.
    Each property also has an arraylist of String for comments.
    Contains methods for filter functions (Search feature)
*/
public class Property {
   
    private int propertyID, size, numOfRoom, numOfBathroom;
    private String propertyType, projectType, propertyName, address, ownedBy, status, state, nameToContact, contactNum, email;
    private double rentPrice, rentRate;
    private ArrayList<String> comments = new ArrayList<String>();

    /* 
    Author: Pavitra

    A contructor used for adding property, editing property and so on, with all the necessary data
    */
    public Property(int propertyID, String projectType, String propertyType, String propertyName, String address, 
    int size, double rentPrice, double rentRate, int numOfRoom, int numOfBathroom, String ownedBy, String status, String state) {
        setPropertyID(propertyID);
        setPropertyType(propertyType);
        setProjectType(projectType);
        setPropertyName(propertyName);
        setAddress(address);
        setSize(size);
        setRentPrice(rentPrice);
        setRentRate(rentRate);
        setNumOfRoom(numOfRoom);
        setNumOfBathroom(numOfBathroom);
        setOwnedBy(ownedBy);
        setStatus(status);
        setState(state);
    }

    /* 
    Author: Adeline Fong Li Ling

    A constructor to be callled by propertyDetail page, it is to set contact info specifically for
    the property detail page
    */
    public Property(String username){
        setContactInfo(username);
    }

    /* 
    Author: Pavitra

    Set propertyID
    */
    public void setPropertyID(int propertyID) {
        this.propertyID = propertyID;
    }

    /* 
    Author: Pavitra

    Set propertyType
    */
    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    /* 
    Author: Pavitra

    Set project type
    */
    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    /* 
    Author: Pavitra

    Set property name
    */
    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    /* 
    Author: Pavitra

    Set address of property
    */
    public void setAddress(String address) {
        this.address = address;
    }

    /* 
    Author: Pavitra

    Set size of property
    */
    public void setSize(int size) {
        this.size = size;
    }

    /* 
    Author: Pavitra

    Set rental price of property
    */
    public void setRentPrice(double rentPrice) {
        this.rentPrice = rentPrice;
    }

    /* 
    Author: Pavitra

    Set rental price of property
    */
    public void setRentRate(double rentRate) {
        this.rentRate = rentRate;
    }

    /* 
    Author: Pavitra

    Set number of room of property
    */
    public void setNumOfRoom(int numOfRoom) {
        this.numOfRoom = numOfRoom;
    }

    /* 
    Author: Pavitra

    Set number of bathroom of property
    */
    public void setNumOfBathroom(int numOfBathroom) {
        this.numOfBathroom = numOfBathroom;
    }

    /* 
    Author: Pavitra

    Set owner/agent who own the property in system
    */
    public void setOwnedBy(String ownedBy){
        this.ownedBy = ownedBy;
    }

    /* 
    Author: Pavitra

    Set status of property (Active/Inactive)
    */
    public void setStatus(String status){
        this.status = status;
    }

    /* 
    Author: Pavitra

    Set state of property (Activated/Deactivated)
    */
    public void setState(String state){
        this.state = state;
    }

    /* 
    Author: Adeline Fong Li Ling

    Set contact info of owner/agent to be displayed in propertyDetail page.
    Include phone number, email and name of owner/agent
    */
    public void setContactInfo(String username){
        boolean done = false;
        for(Owner owner: Database.allOwners){
            if(owner.getUsername().equals(username)){
                contactNum = owner.getPhoneNumber();
                email = owner.getEmail();
                nameToContact = owner.getName();
                done = true;
                break;
            }
        }
        if(!done){
            for(Agent agent: Database.allAgents){
                if(agent.getUsername().equals(username)){
                    contactNum = agent.getPhoneNumber();
                    email = agent.getEmail();
                    nameToContact = agent.getName();
                    break;
                }
            }
        }
    }

    /* 
    Author: Pavitra

    Get propertyID
    */
    public int getPropertyID() {
        return propertyID;
    }

    /* 
    Author: Pavitra

    Get property type
    */
    public String getPropertyType() {
        return propertyType;
    }

    /* 
    Author: Pavitra

    Get project type
    */
    public String getProjectType() {
        return projectType;
    }

    /* 
    Author: Pavitra

    Get property name
    */
    public String getPropertyName() {
        return propertyName;
    }

    /* 
    Author: Pavitra

    Get address of property
    */
    public String getAddress() {
        return address;
    }

    /* 
    Author: Pavitra

    Get size of property
    */
    public int getSize() {
        return size;
    }

    /* 
    Author: Pavitra

    Get rental price property
    */
    public double getRentPrice() {
        return rentPrice;
    }

    /* 
    Author: Pavitra

    Get rental rate of property
    */
    public double getRentRate() {
        return rentRate;
    }

    /* 
    Author: Pavitra

    Get number of room of property
    */
    public int getNumOfRoom() {
        return numOfRoom;
    }

    /* 
    Author: Pavitra

    Get number of bathroom of property
    */
    public int getNumOfBathroom() {
        return numOfBathroom;
    }

    /* 
    Author: Pavitra

    Get owner or agent who own the property in system
    */
    public String getOwnedBy(){
        return ownedBy;
    }

    /* 
    Author: Pavitra

    Get state of property
    */
    public String getStatus(){
        return status;
    }

    /* 
    Author: Pavitra

    Get state of property
    */
    public String getState(){
        return state;
    }

    /* 
    Author: Adeline Fong Li Ling

    Get the name to contact (for propertyDetail page)
    */
    public String getNameToContact(){
        return nameToContact;
    }

    /* 
    Author: Adeline Fong Li Ling

    Get contact number of owner/agent of property
    */
    public String getContactNum(){
        return contactNum;
    }

    /* 
    Author: Adeline Fong Li Ling

    Get email addres of the owner/agent of property
    */
    public String getEmail(){
        return email;
    }

    /* 
    Author: Adeline Fong Li Ling

    Filter and return properties that are in "Activated" state (can be browsed by tenants)
    */
    public static ArrayList<Property> filterActivated(){
        ArrayList<Property> filtered = new ArrayList<Property>();
        for(Property p: Database.allProperties){
            if(p.getState().equals("Activated")){
                filtered.add(p);
            } 
        }
        return filtered;
    }

    /* 
    Author: Adeline Fong Li Ling

    Filter and return properties with specific property type
    */
    public static ArrayList<Property> filterType(String type){
        ArrayList<Property> filtered = new ArrayList<Property>();
        for(Property p: Database.allProperties){
            if(p.getPropertyType().equals(type)){
                filtered.add(p);
            } 
        }
        return filtered;
    }

    /* 
    Author: Adeline Fong Li Ling

    Filter and return properties with specific owner
    */
    public static ArrayList<Property> filterOwner(String owner){
        ArrayList<Property> filtered = new ArrayList<Property>();
        for(Property p: Database.allProperties){
            if(p.getOwnedBy().equals(owner)){
                filtered.add(p);
            } 
        }
        return filtered;
    }

    /* 
    Author: Adeline Fong Li Ling

    Filter and return properties with specific status
    */
    public static ArrayList<Property> filterStatus(String status){
        ArrayList<Property> filtered = new ArrayList<Property>();
        for(Property p: Database.allProperties){
            if(p.getStatus().equals(status)){
                filtered.add(p);
            } 
        }
        
        return filtered;
    }

    /* 
    Author: Adeline Fong Li Ling

    Filter and return properties with specific project
    */
    public static ArrayList<Property> filterProject(String project){
        ArrayList<Property> filtered = new ArrayList<Property>();
        for(Property p: Database.allProperties){
            if(p.getProjectType().equals(project)){
                filtered.add(p);
            } 
        }
        return filtered;
    }

    /* 
    Author: Adeline Fong Li Ling

    Filter and return properties with specific facilities(if any one of the facilities match, is included)
    */
    public static ArrayList<Property> filterFacility(ArrayList<String> selectedFacilities){
        ArrayList<String> filteredID = new ArrayList<String>();
        ArrayList<Property> filteredProperty = new ArrayList<Property>();

        for(String f[]: Database.allFacilities){
            List<String> eachProperty = Arrays.asList(f);
            for(int j=0; j<selectedFacilities.size(); j++){
                if(eachProperty.contains(selectedFacilities.get(j))){
                    filteredID.add(eachProperty.get(0));
                    break;  
                }
            }
        } 

        // If there is no propertyID matched, will return an empty list
        // If there is matched propertyID, store the Property object into filteredProperty ArrayList
        if(filteredID.isEmpty()){
            return filteredProperty;
        }else{
            for(Property p: Database.allProperties){
                for(int j=0; j<filteredID.size(); j++){
                    int id = Integer.parseInt(filteredID.get(j));  
                    if(p.getPropertyID() == id){
                        filteredProperty.add(p);
                    }
                }
            }
        }
        
        return filteredProperty;
    }

    /* 
    Author: Adeline Fong Li Ling

    Filter and return properties with specific range of rental rate
    */
    public static ArrayList<Property> filterRentalRate(double min, double max){
        ArrayList<Property> filtered = new ArrayList<Property>();
        
        if(min == -1 && max != -1){
            for(Property p: Database.allProperties){
                if(p.getRentRate() <= max){
                    filtered.add(p);
                }
            }
        }else if(max == -1 && min != -1){
            for(Property p: Database.allProperties){
                if(p.getRentRate() >= min){
                    filtered.add(p);
                }
            }
        }else{
            for(Property p: Database.allProperties){
                if((p.getRentRate() >= min) && (p.getRentRate() <= max)){
                    filtered.add(p);
                }
            }
        }
        
        return filtered;
    }
   
    /* 
    Author: Adeline Fong Li Ling

    Sort the rental price in ascending order
    */
    public static ArrayList<Property> sortACS_price(ArrayList<Property> unsorted){
        ArrayList<Property> sortList = new ArrayList<Property>(unsorted);
        Collections.sort(sortList, RentPriceASC_Comparator);
        return sortList;
    }

    /* 
    Author: Adeline Fong Li Ling

    Sort the rental price in descending order
    */
    public static ArrayList<Property> sortDESC_price(ArrayList<Property> unsorted){
        ArrayList<Property> sortList = new ArrayList<Property>(unsorted);
        Collections.sort(sortList, RentPriceDESC_Comparator);
        return sortList;
    }

    /* 
    Author: Adeline Fong Li Ling

    Comparator used to sort the rental price in ascending order 
    */
    public static Comparator<Property> RentPriceASC_Comparator = new Comparator<Property>() {
        public int compare(Property p1, Property p2) {
           return Double.compare(p1.getRentPrice(), p2.getRentPrice());
        }
    };

    /* 
    Author: Adeline Fong Li Ling

    Comparator used to sort the rental price in descending order
    */
    public static Comparator<Property> RentPriceDESC_Comparator = new Comparator<Property>() {
        public int compare(Property p1, Property p2) {
           return Double.compare(p2.getRentPrice(), p1.getRentPrice());
        }
    };

     /**
     * Author: Lew Zi Xuan 
     *
     * This method return a property object by property id.
     * Search through the static arraylist
     * if there is match, return the property.
     * else return null
     */
    public static Property getPropertyByID(int propertyID){
        for(Property p : Database.allProperties){
            if(p.getPropertyID() == propertyID)
                return p;
        }
        return null;
    }

    /* 
    Author: Lew Zi Xuan

    Read the property's comments into ArrayList, get by propertyID
    */
    public void readComments(){
        comments = Database.readPropertyCommentTxt(getPropertyID());
    }

    /* 
    Author: Lew Zi Xuan

    Get comments read by a property
    */
    public ArrayList<String> getComments(){
        return comments;
    }

    /* 
    Author: Adeline Fong Li Ling

    Override equals() method to compare Property objects.
    Used by retainAll() that involve comparing to obtain the common objects between 2 ArrayList of 
    Property objects
    */
    @Override
    public boolean equals(Object obj) {
 
        // If the object is compared with itself  
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Property)) {
            return false;
        }
         
        Property p = (Property) obj;
         
        // Compare propertyID
        return this.propertyID == p.propertyID;
    }

    /* 
    Author: Adeline Fong Li Ling

    Override toString() method to arrange for comma-delimited property data to be saved in csv
    */
    @Override
    public String toString() {
        return  propertyID + "," + projectType + "," + propertyType + ",\"" + propertyName + "\",\"" + address 
        + "\"," + size + "," + rentPrice + "," + rentRate + "," + numOfRoom + "," + numOfBathroom + "," + ownedBy + "," + status + "," + state;
    }
}
