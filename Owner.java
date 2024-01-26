import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.io.IOException;
import java.nio.file.Paths;

/* 
    Owner class is a subclass of Person. It inherits variables of account data such as username, name, password, email,
    phoneNumber, account type. It also inherits methods such as setters, getters, addProperty() and deleteProperty().  
    Owner class also implements interface which is PropertyOrganizer. It implements methods define in PropertyOrganizer. 
*/
public class Owner extends Person implements PropertyOrganizer{
    private ArrayList<Property> ownedProperties = new ArrayList<Property>();
    private ArrayList<Integer> ownedPropertyID = new ArrayList<Integer>();
    private ArrayList<Property> rentedProperties = new ArrayList<Property>();
    private ArrayList<Tenant> rentingTenants = new ArrayList<Tenant>();
    private Database db = new Database();

    public Owner() {}

    /* 
        Author: Nurshara Batrisyia, Pavitra 
        Contructor used for sending registration request and create owner account
        Owner has different constructor with Agent, hence they are seperated
    */
    public Owner(String username, String name, String pwd, String email, String phoneNumber, String accType){
        setUsername(username);
        setName(name);
        setPwd(pwd);
        setEmail(email);
        setPhoneNumber(phoneNumber);
        setAccType(accType);
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
        Database.allProperties.add(new Property(lastID + 1, projectType, propertyType, propertyName, address, size, rentPrice, rentRate, numOfRoom, numOfBathroom, ownedBy, status, state));
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
        Author: Nurshara Batrisyia

        A method for editing data of owned property. Property data are passed in parameter.
        The targeted propertyID is compared with all properties. When matched, the new values are set and saved.
        It also call the editFacilities().
    */
    public void editProperty(int propertyID, String projectType, String propertyType, String propertyName, String address, 
    int size, double rentPrice, double rentRate, int numOfRoom, int numOfBathroom, ArrayList<String> facilities){
        for(Property p: Database.allProperties){
            if(p.getPropertyID() == propertyID){
                p.setProjectType(projectType);
                p.setPropertyType(propertyType);
                p.setPropertyName(propertyName);
                p.setAddress(address);
                p.setSize(size);
                p.setRentPrice(rentPrice);
                p.setRentRate(rentRate);
                p.setNumOfRoom(numOfRoom);
                p.setNumOfBathroom(numOfBathroom);
                break;
            }
        }
        db.saveProperty();
        editFacilities(propertyID, facilities);
    }

     /* 
        Author: Adeline Fong Li Ling

        A method for editing facilities of a property. The propertyID and facilityToEdit ArrayList are passed in parameter.
        Targeted propertyID is compared in String and when matched, the facilities array (in format: facility1, facility2, ...) 
        is copied to the original eachProperty array (in format: propertyID, facility1, facility2, ...)
    */
    public void editFacilities(int propertyID, ArrayList<String> facilityToEdit){

        for(int i=0; i<Database.allFacilities.size(); i++){
            String[] eachProperty = Database.allFacilities.get(i);
            if(eachProperty[0].equals(String.valueOf(propertyID))){
                String[] temp = new String[facilityToEdit.size() + 1];
                ArrayList<String> newTemp = new ArrayList<String>(Arrays.asList(temp));  
                newTemp.add(String.valueOf(propertyID));
                newTemp.addAll(facilityToEdit);  
                temp = newTemp.toArray(temp); 
                Database.allFacilities.set(i, temp);
                break;
            }
        }
        db.saveFacilities();
    }

    /* 
        Author: Adeline Fong Li Ling

        A method for deleting a property. It compares propertyID of delete with all properties, remove the Property object
        when propertyID is found. It also call deleteFacilities(), deletePhotos() and deleteComment() as to 
        completely delete the property data. 
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

        A method of getting rental requests of an agent. 
        Only rental requests of a given agent that are in "Requesting" status is retrieved
    */
     public ArrayList<RentalRequest> filterRentalRequest(String username){

        ArrayList<RentalRequest> filtered = new ArrayList<RentalRequest>();
        for(RentalRequest r: Database.allRentalRequests){
            if((r.getPropertyOwner().equals(username)) && (r.getStatus().equals("Requesting"))){
                filtered.add(r);
            } 
        }
        return filtered;
    }

    /* 
        Author: Adeline Fong Li Ling

        A method for assigning property rental to tenant. Firstly, the all rentals are read into ArrayList of String[].
        It then search for username of the tenant. If the tenant has existing rental record, a new propertyID is added. 
        Else, if the tenant do not have any rental record yet, a new row is added in format: 
        [username, propertyID]. Rental is saved. After that, it also need to flag rental status to "Approved" and
        flag property status to "Inactive".
    */
    public void assignRental(int rentalRequestID, int propertyID, String username){
        
        boolean found = false;
        for(int i=0; i<Database.allRentals.size(); i++){
            String[] tenantRow = Database.allRentals.get(i);
            if(tenantRow[0].equals(username)){
                String[] temp = new String[tenantRow.length + 1];
                System.arraycopy(tenantRow, 0, temp, 0, tenantRow.length);
                ArrayList<String> newTemp = new ArrayList<String>(Arrays.asList(temp));  
                newTemp.add(String.valueOf(propertyID));  
                temp = newTemp.toArray(temp);  
                Database.allRentals.set(i, temp);
            
                found = true;
                break;
            }
        }
        if(found == false){
            String[] newRow = {username, String.valueOf(propertyID)};
            Database.allRentals.add(newRow);
        }
        db.saveRental();
        rentalApproved(rentalRequestID);
        setInactiveProperty(propertyID);
    }

    /* 
        Author: Adeline Fong Li Ling

        A method used by removeRentingTenant() to convert Array (row of a tenant's rental) into List, 
        remove a propertyID, then return again the Array
    */
    public static String[] removeFromArray(int index, String[] arr){
        List<String> list = new ArrayList<String>(Arrays.asList(arr));
        list.remove(index);
        arr = list.toArray(arr);
    
        return arr;
    }

    /* 
        Author: Pavitra, Adeline Fong Li Ling

        A method to remove a propertyID from the tenant's rental. It is called when tenant is unassigned by
        owner. It removes the propertyID from thw specific row of a tenant's rental.
        For example, when original row is ["Ali", 2, 3], after property 3 is removed, it become ["Ali", 2]
    */
    public void removeRentingTenant(int propertyID, String username){
        for(int i=0; i<Database.allRentals.size(); i++){
            String[] tenantRow = Database.allRentals.get(i);
            if(tenantRow[0].equals(username)){
                for(int j=1; j<Database.allRentals.get(i).length; j++){
                    if(tenantRow[j].equals(String.valueOf(propertyID))){
                        tenantRow = removeFromArray(j, Database.allRentals.get(i));
                        break;
                    }
                }
            }
        }
        db.saveRental();
        setActiveProperty(propertyID);
    }

    /* 
        Author: Adeline Fong Li Ling

        A method that compare owner's username with all properties's ownedBy variables.
        If matched, the property is added to ownedProperties ArrayList to be displayed in View (My Properties).
    */
    public ArrayList<Property> retrieveOwnProperties(String agent){
        for(Property p: Database.allProperties){
            if(p.getOwnedBy().equals(agent)){
                ownedProperties.add(p);
            }
        }
        return ownedProperties;
    }

     /* 
        Author: Adeline Fong Li Ling

        A method that retrieves rented properties of an owner.
        It compares and obtain only properties that is owned by the agent and in status "Inactive" (assigned to tenant).
        They are stored in rentedProperties ArrayList
    */
    public ArrayList<Property> retrieveRentals(String owner){
        for(Property p: Database.allProperties){
            if(p.getOwnedBy().equals(owner) && p.getStatus().equals("Inactive")){
                rentedProperties.add(p);
            }
        }
        return rentedProperties;
    }

    /* 
        Author: Adeline Fong Li Ling

        A method to retrieve tenants who are renting properties managed by an owner.
        It first compare to get the tenant's username who are renting, by checking whether the row contains 
        rented propertyID. After that, if the tenantUsernames ArrayList is not empty, it compare with all tenants 
        using username. If matched, the Tenant object is retrieved and added to rentingTenants ArrayList.
    */
    public ArrayList<Tenant> retrieveTenants(){
        // get tenant that rent owned properties
        ArrayList<String> tenantUsernames = new ArrayList<String>();
        for(Property p: rentedProperties){
            for(int i=0; i<Database.allRentals.size(); i++){
                String[] tenantRow = Database.allRentals.get(i);
                List<String> newTenantRow = new ArrayList<String>(Arrays.asList(tenantRow));
                if(newTenantRow.contains(String.valueOf(p.getPropertyID()))){
                    tenantUsernames.add(tenantRow[0]);
                    break;
                }
            } 
        }

        if(!tenantUsernames.isEmpty()){
            for(String s: tenantUsernames){
                for(Tenant t: Database.allTenants){
                    if(t.getUsername().equals(s)){
                        rentingTenants.add(t);
                        break;
                    }
                }
            }
        }

        return rentingTenants;
    }

    /* 
        Author: Pavitra 

        Change status of property from "Inactive" to "Active" when tenant is removed from rental.
    */
    public void setActiveProperty(int propertyID){
        for(Property p: Database.allProperties){
            if(p.getPropertyID() == propertyID){
                p.setStatus("Active");
            }
        }
        db.saveProperty();
    }

    /* 
        Author: Pavitra

        Change status of property from "Active" to "Inactive" when tenant rent it
    */
    public void setInactiveProperty(int propertyID){
        for(Property p: Database.allProperties){
            if(p.getPropertyID() == propertyID){
                p.setStatus("Inactive");
            }
        }
        db.saveProperty();
    }

    /* 
        Author: Adeline Fong Li Ling

        Change the state of property from "Deactivated" to "Activated".
        Activated property can be browsed by tenants.
    */
    public void activateProperty(int propertyID){
        for(Property p: Database.allProperties){
            if(p.getPropertyID() == propertyID){
                p.setState("Activated");
            }
        }
        db.saveProperty();
    }

    /* 
        Author: Adeline Fong Li Ling

        Change the state of property from "Activated" to "Deactivate".
        Deactivated property cannot be browsed or viewed by tenants.
    */
    public void deactivateProperty(int propertyID){
        for(Property p: Database.allProperties){
            if(p.getPropertyID() == propertyID){
                p.setState("Deactivated");
            }
        }
        db.saveProperty();
    }

    /* 
        Author: Adeline Fong Li Ling

        Change rentalRequestID from "Requesting" to "Approved" when owner assign tenant to a property
    */
    public void rentalApproved(int rentalRequestID){
        for(RentalRequest r: Database.allRentalRequests){
            if(r.getRentalRequestID() == rentalRequestID){
                r.setStatus("Approved");
            }
        }
        db.saveRentalRequest();
        try{
            respondRentalRequest(rentalRequestID, "Approved");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
        Author: Adeline Fong Li Ling

        Change rentalRequestID from "Requesting" to "Declined" when agent decline the rental request
    */
    public void rentalDeclined(int rentalRequestID){
        for(RentalRequest r: Database.allRentalRequests){
            if(r.getRentalRequestID() == rentalRequestID){
                r.setStatus("Declined");
            }
        }
        db.saveRentalRequest();
        try{
            respondRentalRequest(rentalRequestID, "Declined");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method is illustrating how owner respond to rental request send by tenant.
     * Create a rental request DB & property DB instance.
     * Since the requestID is created from 1 - n.
     * So we just use requestID - 1 to get the particular rental request instance.
     * set the status from parameter on the rental request.
     * Get the property name using the property ID from the rental request instnace.
     * Save the latest arraylist to CSV.
     * After responding the request, inform the sender.
     */
    public void respondRentalRequest(int requestID, String status) throws IOException {
        RentalRequest request = null;
        for(RentalRequest r: Database.allRentalRequests){
            if(r.getRentalRequestID() == requestID){
                request = r;
            }
        }

        String propertyName = Property.getPropertyByID(request.getPropertyID()).getPropertyName();

        RentalRequest r = new RentalRequest();
        r.informRequestSender(requestID, propertyName);
    }

    /* 
        Author: Adeline Fong Li Ling

        Compare and retrieve the owner's owned propertyID. When the property is owned by owner, it is added to
        an ArrayList and returned.
    */
    public ArrayList<Integer> retrieveOwnPropertyID(String owner){
        for(Property p: Database.allProperties){
            if(p.getOwnedBy().equals(owner)){
                ownedPropertyID.add(p.getPropertyID());
            }
        }
        return ownedPropertyID;
    }

    /* 
        Author: Adeline Fong Li Ling

        toString() method is overrided to arrange owner account data into a comma delimited string to save in csv
    */
    @Override
    public String toString() {
        return getUsername() + "," + getName() + "," + getPwd() + "," + getEmail() + "," + getPhoneNumber() + "," + getAccType();
    }

}
