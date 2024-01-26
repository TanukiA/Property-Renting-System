import java.util.ArrayList;

/* 
    This is a part of Factory Design Pattern.
    It is a model class for registration, used for all actors (Tenant, Owner, Agent, Admin).
    It asks the PersonFactory to create objects for registration request and account creation. 
    Also provides static methods for input validation on the registration form.
*/
public class RegisterModel {

     /* 
    Author: Adeline Fong Li Ling
    
    A method that call PersonFactory, obtain different objects in Person (superclass) type.
    After that, it add registration request for all account types (Tenant, Owner, Agent)
    */
    public static void addRegisterRequest(String name, String username, String pwd, String email, String phoneNumber, 
    String accType, String owner){
        PersonFactory perFactory = new PersonFactory();
        Person person = perFactory.createRegisterRequest(name, username, pwd, email, phoneNumber, accType, owner);
        addRequest(person);
    }

     /* 
    Author: Adeline Fong Li Ling

    A method that call PersonFactory, obtain the objects in Person (superclass) type.
    The created object is used to add the particular account according to type.
    So, the Person object is downcasted to add the user account by method in Admin.
    */
    public static void addAccount(String name, String username, String pwd, String email, String phoneNumber, 
    String accType, String owner){
        PersonFactory perFactory = new PersonFactory();
        Person person = perFactory.createAccount(name, username, pwd, email, phoneNumber, accType, owner);
        if(accType.equals("Tenant")){
            Admin.addTenant((Tenant)person);
        }else if(accType.equals("Property owner")){
            Admin.addOwner((Owner)person);
        }else if(accType.equals("Property agent")){
            Admin.addAgent((Agent)person);
        }else if(accType.equals("Admin")){
            Admin.addAdmin((Admin)person);
        }
    }

     /* 
    Author: Adeline Fong Li Ling

    A method for adding registration request to database
    */
    public static void addRequest(Person user){
        ArrayList<Person> users = Database.readRegisterRequest();
        users.add(user);   
        Database.saveRegisterRequest(users);
    }

     /* 
    Author: Adeline Fong Li Ling

    Check and ensure password length is at least 6 characters.
    */
    public static boolean checkPwdLength(String pwd){
        
        int pwd_sz = 0; 
        for (int i=0; i<pwd.length(); i++)
            pwd_sz++;
    
        if (pwd_sz < 6)
            return false;
        else
            return true;
        
    }

     /* 
    Author: Adeline Fong Li Ling

    Check and ensure phone number is all in digit
    */
    public static boolean checkPhoneNo(String phone){
  
        for (int i=0; i<phone.length(); i++){
            if(!Character.isDigit(phone.charAt(i)))
                return false;
        }
        return true;
    }

     /* 
    Author: Adeline Fong Li Ling

    Validate the existence of Owner's username in the system. Used when Agent want to register along with
    the hiring owner's username
    */
    public static boolean validOwner(String owner){
        Database db = new Database();
        db.readOwner();
        for(Owner o: Database.allOwners){
            if(o.getUsername().equals(owner)){
                return true;
            }
        }
        return false;
    }

     /* 
    Author: Adeline Fong Li Ling

    Check existence of username in current system. Checked for all account types and also the registration request.
    Because username must be unique to be created.
    */
    public static boolean checkExistence(String username){

        Database db = new Database();
        db.readTenant();
        db.readOwner();
        db.readAgent();
        db.readAdmin();

        // Check tenant
        for(Tenant t: Database.allTenants){
            if(t.getUsername().equals(username)){
                return false;
            }
        }
        // Check owner
        for(Owner o: Database.allOwners){
            if(o.getUsername().equals(username)){
                return false;
            }
        }

        // Check agent
        for(Agent a: Database.allAgents){
            if(a.getUsername().equals(username)){
                return false;
            }
        }

        // Check admin
        for(Admin a: Database.allAdmins){
            if(a.getUsername().equals(username)){
                return false;
            }
        }
    
        // Check request
        ArrayList<Person> requests = Database.readRegisterRequest();
        for(int i=0; i<requests.size(); i++){
            if(requests.get(i).getUsername().equals(username)){
                return false;
            }
        }

        return true;
    }
}
