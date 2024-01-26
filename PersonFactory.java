/*
    This is a part of Factory Design Pattern.
    This PersonFactory class is responsible for instantiating concrete class for Admin, Owner, Agent and Tenant.
    It will decide what type of object to create during creation of registration request and the user accounts.
    The abstract class used is Person, while the subclasses involved are Admin, Owner, Agent and Tenant.
    The client is RegisterModel, which will use this PersonFactory to get appropriate object of concreate class
    and pass the account data. This Design Pattern makes object creation easy to handle and create object without 
    exposing the creation logic to the client (RegisterModel) and refer to newly created object using a 
    common interface (Person).
*/
public class PersonFactory{

    /* 
    Author: Adeline Fong Li Ling

    A method that return new object according to account type during the creation of user account.
    Object of Person type is returned.
    */
    public Person createAccount(String username, String name, String pwd, String email, 
    String phoneNumber, String accType, String owner){
        
        if(accType.equals("Tenant")){
            return new Tenant(username, name, pwd, email, phoneNumber, accType);
        
        }else if(accType.equals("Property owner")){
            return new Owner(username, name, pwd, email, phoneNumber, accType);
            
        }else if(accType.equals("Property agent")){
            return new Agent(username, name, pwd, email, phoneNumber, accType, owner);
    
        }else if(accType.equals("Admin")){
            return new Admin(username, name, pwd, email, phoneNumber, accType);

        }
        return null;
    }

    /* 
    Author: Adeline Fong Li Ling

    A method that return new object according to account type during the creation of registration request.
    Object of Person type is returned.
    */
    public Person createRegisterRequest(String username, String name, String pwd, String email, 
    String phoneNumber, String accType, String owner){

        if(accType.equals("Tenant")){
            return new Tenant(username, name, pwd, email, phoneNumber, accType);
    
        }else if(accType.equals("Property owner")){
            return new Owner(username, name, pwd, email, phoneNumber, accType);
           
        }else if(accType.equals("Property agent")){
            return new Agent(username, name, pwd, email, phoneNumber, accType, owner);
            
        }
        return null;
    }
    
}
