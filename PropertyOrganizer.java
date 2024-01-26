import java.io.IOException;
import java.util.ArrayList;

/*
    This is an interface which is implemented by Owner and Agent class.
    It specify what Owner and Agent will do and provide abstraction. 
    Both of the class implement the methods here upon tasks of organizing peroperties.
    For explanation on each method, please refer to Owner and Agent class. 
*/
public interface PropertyOrganizer {

    public void editProperty(int propertyID, String projectType, String propertyType, String propertyName, String address, 
    int size, double rentPrice, double rentRate, int numOfRoom, int numOfBathroom, ArrayList<String> facilities);

    public void editFacilities(int propertyID, ArrayList<String> facilityToEdit);

    public ArrayList<RentalRequest> filterRentalRequest(String username);

    public void assignRental(int rentalRequestID, int propertyID, String username);

    public void removeRentingTenant(int propertyID, String username);

    public ArrayList<Property> retrieveOwnProperties(String agent);

    public ArrayList<Property> retrieveRentals(String agent);

    public ArrayList<Tenant> retrieveTenants();

    public void setActiveProperty(int propertyID);

    public void setInactiveProperty(int propertyID);

    public void activateProperty(int propertyID);

    public void deactivateProperty(int propertyID);

    public void rentalApproved(int rentalRequestID);

    public void rentalDeclined(int rentalRequestID);

    public void respondRentalRequest(int requestID, String status) throws IOException;

    public ArrayList<Integer> retrieveOwnPropertyID(String agent);

}
