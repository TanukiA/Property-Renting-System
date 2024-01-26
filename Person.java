import java.util.*;

/*
    This is an abstract class which is implemented by Admin, Owner, Agent and Tenant (all user) and also CustomContent.
    It contains variables which is the account data of all user: name, username, pwd, email, phoneNumber, accType, owner.
    The setters and getters are also common to all these classes to be used.
    There are 2 abstract methods, which is addProperty() and deleteProperty. They are important methods to be
    implemented by most of the subclasses.
*/
public abstract class Person{

    private String name, username, pwd, email, phoneNumber, accType, owner;

     /* 
    Author: Adeline Fong Li Ling

    Set person's full name
    */
    public void setName(String name){
        this.name = name;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set person's username
    */
    public void setUsername(String username){
        this.username = username;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set person's email address
    */
    public void setEmail(String email){
        this.email = email;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set person's password
    */
    public void setPwd(String pwd){
        this.pwd = pwd;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set person's password
    */
    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber= phoneNumber;
    }

     /* 
    Author: Adeline Fong Li Ling

    Set person's account type
    */
    public void setAccType(String accType){
        this.accType = accType;
    }

     /* 
    Author: Adeline Fong Li Ling

    This is for Agent only, who need to set owner's username who hire the agent
    */
    public void setOwner(String owner){
        this.owner = owner; 
    }

     /* 
    Author: Adeline Fong Li Ling

    Get person's name
    */
    public String getName(){
        return name;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get person's username
    */
    public String getUsername(){
        return username;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get person's email address
    */
    public String getEmail(){
        return email;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get person's password
    */
    public String getPwd(){
        return pwd;
    }

     /* 
    Author: Adeline Fong Li Ling

    Get person's phone number
    */
    public String getPhoneNumber(){
        return phoneNumber;
    }

    public String getAccType(){
        return accType;
    }

    /* 
    Author: Adeline Fong Li Ling

    This is for Agent only, who need to get owner's username who hire the agent
    */
    public String getOwner(){
        return owner;
    }

     /* 
    Author: Adeline Fong Li Ling

    An abstract method to be implemented for adding property
    */
    public abstract void addProperty(String projectType, String propertyType, String propertyName, String address, 
    int size, double rentPrice, double rentRate, int numOfRoom, int numOfBathroom, String ownedBy, String status, String state, ArrayList<String> facilities);

    /* 
    Author: Adeline Fong Li Ling

    An abstract method to be implemented for deleting property
    */
    public abstract void deleteProperty(int propertyID);

    /* 
    Author: Adeline Fong Li Ling

    A method common to Admin, Tenant, Owner and Agent, which is to check username and password upon login
    */
    public static int checkLogin(String username, String pwd){

        Database db = new Database();
        db.readTenant();
        db.readOwner();
        db.readAgent();
        db.readAdmin();
        
        // Check tenant
        for(Tenant t: Database.allTenants){
            if(t.getUsername().equals(username) && t.getPwd().equals(pwd)){
                return 0;
            }
        }
        // Check owner
        for(Owner o: Database.allOwners){
            if(o.getUsername().equals(username) && o.getPwd().equals(pwd)){
                return 1;
            }
        }

        // Check agent
        for(Agent a: Database.allAgents){
            if(a.getUsername().equals(username) && a.getPwd().equals(pwd)){
                return 2;
            }
        }

        // Check admin
        for(Admin a: Database.allAdmins){
            if(a.getUsername().equals(username) && a.getPwd().equals(pwd)){
                return 3;
            }
        }
        return -1;
    }

    /* 
    Author: Adeline Fong Li Ling

    toString() method overrided to arrange the person's account data into comma-delimited String to save in csv.
    This is used when sending registration request to the admin. If the account type is not owner, the first 
    condition applies, which is a registration request without owner's username column.
    */
    @Override
    public String toString() {
        if(owner.equals(null))
            return username + "," + name + "," + pwd + "," + email + "," + phoneNumber + "," + accType;
        else
            return username + "," + name + "," + pwd + "," + email + "," + phoneNumber + "," + accType + "," + owner;
    }

}
