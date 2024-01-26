import java.io.IOException;

/*
    This is a class for managing rental request, send by Tenant and received by Owner/ Agent who own that
    property. Each of the request has a rentalRequestID and also status (Requesting, Approved, Declined)
*/
public class RentalRequest{
    /**
     * Final field for propertyID , request sender & request receiver.
     */
    private int rentalRequestID;
    private int propertyID;
    private String requestSender;
    private String propertyOwner;
    private String status;
    
    public static int counter = 0; 

    public RentalRequest() {}
    
    /* 
    Author: Lew Zi Xuan

    Constructor to read the rental request data from csv file
    */
    public RentalRequest(int rentalRequestID, int propertyID, String requestSender, String propertyOwner, String status) {
        this.rentalRequestID = rentalRequestID;
        this.propertyID = propertyID;
        this.status = status;
        this.requestSender = requestSender;
        this.propertyOwner = propertyOwner;
    }

    /* 
    Author: Lew Zi Xuan

    Constructor used during creation of new rental requests by tenant. The status is always Requesting.
    */
    public RentalRequest(int propertyID, String requestSender, String propertyOwner) {
        this.rentalRequestID = ++counter;
        this.propertyID = propertyID;
        this.requestSender = requestSender;
        this.propertyOwner = propertyOwner;
        this.status = "Requesting";
    }

     /* 
    Author: Lew Zi Xuan

    Get the rentalRequestID
    */
    public int getRentalRequestID() {
        return rentalRequestID;
    }

     /* 
    Author: Lew Zi Xuan

    Get propertyID of rental requested
    */
    public int getPropertyID() {
        return propertyID;
    }

     /* 
    Author: Lew Zi Xuan

    Get status (Requesting/Approved/Declined)
    */
    public String getStatus() {
        return status;
    }

     /* 
    Author: Lew Zi Xuan

    Get the username of request sender (tenant)
    */
    public String getRequestSender() {
        return requestSender;
    }

     /* 
    Author: Lew Zi Xuan

    Get the property owner username who receive the rental request
    */
    public String getPropertyOwner() {
        return propertyOwner;
    }
    

     /* 
    Author: Lew Zi Xuan

   Set status when the status is changed
    */
    public void setStatus(String status) {
        this.status = status;
    }
    

     /**
     * Authoer: Lew Zi Xuan 
     *
     * this method is for inform request sender after request receiver respond the rental request.
     * Is method for doing the Observer design pattern for the notification node in the UI.
     * Initial tenant instance, get the particular request using rental request ID.
     * From the request, get the sender username.
     * get the target tenant by using sender username.
     * generate a message with rental request id, property name, and property status.
     * save the message into target tenant rental request arraylist.
     * In this case, that's mean target tenant is not asking whether the rental request is respond.
     * The target tenant rental request arraylist is being inform and update when there is an update.
     * After adding the message into the arraylist, save the result into file.
     * if the request status is Approved, then add into tenant rented property arraylist. save.
     */
    public void informRequestSender(int requestID, String propertyName)throws IOException{
        RentalRequest target = null;
        for(RentalRequest r: Database.allRentalRequests){
            if(r.getRentalRequestID() == requestID){
                target = r;
            }
        }
        
        String senderUsername = target.getRequestSender();
        Tenant tenant = Tenant.getTargetTenantObject(senderUsername);
        
        String message = "Rental Request ID: "  + requestID + ","
                + "Property Name: " + propertyName + ","
                + "Request Status: " + target.getStatus();
        tenant.getRentalRequestMessages().add(message);

        tenant.saveRentalRequestTxt(); //save

    }

     /* 
    Author: Lew Zi Xuan

    Override toString() method to arrange th rental request into comma-delimited String to be saved in csv
    */
    @Override
    public String toString(){
        return rentalRequestID + "," + propertyID + "," + requestSender + "," + propertyOwner + "," + status;
    }
}
