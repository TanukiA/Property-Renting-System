import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;
import java.nio.file.Files;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.File;
import java.io.PrintWriter;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.io.*;

/*
    This is a class which stores all Data of this project, including user data, property data, rental requests and 
    so on. It contains static ArrayLists that store all data read from the csv file. It can be easily accessed, 
    modified and saved again by using these static ArrayLists.
*/
public class Database{

    public static ArrayList<Tenant> allTenants = new ArrayList<>();
    public static ArrayList<Admin> allAdmins = new ArrayList<>();
    public static ArrayList<Owner> allOwners = new ArrayList<>();
    public static ArrayList<Agent> allAgents = new ArrayList<>();
    public static ArrayList<Property> allProperties = new ArrayList<>();
    public static ArrayList<String[]> allFacilities = new ArrayList<>();
    public static ArrayList<RentalRequest> allRentalRequests = new ArrayList<>();
    public static ArrayList<String[]> allRentals = new ArrayList<>();

    /* 
    Author: Adeline Fong Li Ling, Pavitra

    Method for read property data from csv. It converts String items into double or int according to what we defined
    in the Property constructor. Property retrieved is added to the allProperties ArrayList that can be accessed by 
    all classes.
    */
    public void readProperty(){
        allProperties.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/property.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                int propertyID = Integer.parseInt(items[0].trim()); 
                int size = Integer.parseInt(items[5].trim());
                double rentPrice = Double.parseDouble(items[6].trim());
                double rentRate = Double.parseDouble(items[7].trim());
                int numOfRoom = Integer.parseInt(items[8].trim());
                int numOfBathroom = Integer.parseInt(items[9].trim());
                allProperties.add(new Property(propertyID, items[1], items[2], items[3].replace("\"",""), items[4].replace("\"",""), 
                size, rentPrice, rentRate, numOfRoom, numOfBathroom, items[10], items[11], items[12]));
            }  
        }catch(IOException e){
            System.out.println("Property file is not found.");
        }                
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for read facilities from csv. It stores each row of data into a String array. 
    The array is added to the allFacilities ArrayList. 
    Format of each row: [propertyID, facility1, facility2, facility3,...]
    */
    public void readFacilities(){
        allFacilities.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/facility.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",");
                allFacilities.add(items);
            }
        }catch(IOException e){
            System.out.println("Facility file is not found.");
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for read rental requests from csv. It converts String items into int according to what we defined
    in the RentalRequest contructor. Rental requests are retrieved and added to the allRentalRequests ArrayList.
    */
    public void readRentalRequests(){
        allRentalRequests.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/rentalRequest.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",");
                int rentalRequestID = Integer.parseInt(items[0].trim());
                int propertyID = Integer.parseInt(items[1].trim()); 
                allRentalRequests.add(new RentalRequest(rentalRequestID, propertyID, items[2], items[3], items[4]));
            }
            RentalRequest.counter =  allRentalRequests.size();
        }catch(IOException ex){
            System.out.println("Rental request file is not found.");
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for read tenant's rental record. Called by owner, agent and tenants. Each row in file indicates
    each tenant's rental. Format: [username, propertyID, propertyID, proepertyID,...]
    */
    public void readRental(){
        allRentals.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/rental.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",");
                allRentals.add(items);
            }
        }catch(IOException e){
            System.out.println("Rental file is not found.");
        }
    }

    /* 
    Author: Nurshara Batrisyia, Adeline Fong Li Ling

    Method for read owner's account data. Owners retrieved from file is stored to allOwners ArrayList
    */
    public void readOwner(){
        allOwners.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/owner.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",");
                allOwners.add(new Owner(items[0], items[1], items[2], items[3], items[4], items[5]));
            }
        }catch(IOException e){
            System.out.println("Owner file is not found.");
        }
    }

    /* 
    Author: Pavitra 

    Method for read agent's account data. Agents retrieved from file is stored to allAgents ArrayList.
    items[6] is an extra compared to Owner. It contains owner's username who hire that particular agent
    */
    public void readAgent(){
        allAgents.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/agent.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",");
                allAgents.add(new Agent(items[0], items[1], items[2], items[3], items[4], items[5], items[6]));
            }
        }catch(IOException e){
            System.out.println("Agent file is not found.");
        }
    }

    /* 
    Author: Lew Zi Xuan, Pavitra

    Method for read tenant's account data. Tenants retrieved from file is stored to allTenants ArrayList.
    */
    public void readTenant(){
        allTenants.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/tenant.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(","); 
                allTenants.add(new Tenant(items[0], items[1], items[2], items[3], items[4], items[5]));
            } 
        }catch(IOException e){
            System.out.println("Tenant file is not found.");
        }               
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for read admin's account data. Admins retrieved from file is stored to allAdmins ArrayList.
    */
    public void readAdmin(){
        allAdmins.clear();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/admin.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",");
                allAdmins.add(new Admin(items[0], items[1], items[2], items[3], items[4], items[5]));
            }  
        }catch(IOException e){
            System.out.println("Admin file is not found.");
        }                
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for read registration request. This data will be used to create account when registration is 
    approved by admin. The items[5] varies according to account type, which is property agent, property owner
    and tenant. Admin data is not included here because admin do not have to request.
    */
    public static ArrayList<Person> readRegisterRequest(){
        ArrayList<Person> registerRequests = new ArrayList<>();
        try{
            List<String> lines = Files.readAllLines(Paths.get("csv/registerRequest.csv"));
            for (int i = 0; i < lines.size(); i++) {
                String[] items = lines.get(i).split(",");
                if(items[5].equals("Property agent")){
                    registerRequests.add(new Agent(items[0], items[1], items[2], items[3], items[4], items[5], items[6]));
                }else if(items[5].equals("Property owner")){
                    registerRequests.add(new Owner(items[0], items[1], items[2], items[3], items[4], items[5]));
                }else if(items[5].equals("Tenant")){
                    registerRequests.add(new Tenant(items[0], items[1], items[2], items[3], items[4], items[5]));
                }
            }                
        }catch(IOException ex){
            System.out.println("Register Request file is not found.");
        }

        return registerRequests;
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for save properties into csv file. toString() method is overrided in Property class to return
    comma-delimited rows of data.
    */
    public void saveProperty(){
        try{
            StringBuilder sb = new StringBuilder();
            for (Property p: allProperties) {
                sb.append (p.toString() + "\n");
            }
            Files.write(Paths.get("csv/property.csv"), sb.toString().getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for save facilities into csv file. The facilities data is formatted using PrintWriter.
    By mapping the ArrayList of Array to convertToCSV() method, the data is formatted to be comma-delimited and
    ready to write to file.
    */
    public void saveFacilities(){
        File outputFile = new File("csv/facility.csv");
        try(PrintWriter pw = new PrintWriter(outputFile)) {
            allFacilities.stream()
                .map(this::convertToCSV)
                .forEach(pw::println);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    This method joins each data in String Array using comma, and filter to ensure no null or empty
    value will enter file.
    */
    public String convertToCSV(String[] data) {
        return Stream.of(data)
            .filter(s -> (s != null && s.length() > 0))
            .collect(Collectors.joining(","));
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for save tenant's rental into csv file. The rental data is formatted using PrintWriter.
    By mapping the ArrayList of Array to convertToCSV() method, the data is formatted to be comma-delimited and
    ready to write to file.
    */
    public void saveRental(){
        File outputFile = new File("csv/rental.csv");
        try(PrintWriter pw = new PrintWriter(outputFile)) {
            allRentals.stream()
                .map(this::convertToCSV)
                .forEach(pw::println);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    /* 
    Author: Adeline Fong Li Ling

    Method for save owner's account data into csv file. It is called by admin since admin manges the account data.
    */
    public void saveOwner(){
        try{
            StringBuilder sb = new StringBuilder();
            for (Owner o: allOwners) {
                sb.append (o.toString() + "\n");
            }
            Files.write(Paths.get("csv/owner.csv"), sb.toString().getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for save agent's account data into csv file. It is called by admin since admin manges the account data.
    */
    public void saveAgent(){
        try{
            StringBuilder sb = new StringBuilder();
            for (Agent a: allAgents) {
                sb.append (a.toString() + "\n");
            }
            Files.write(Paths.get("csv/agent.csv"), sb.toString().getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Lew Zi Xuan, Pavitra

    Method for save tenant's account data into csv file. It is called by admin since admin manges the account data.
    Tenant class will also call it when profile is updated.
    */
    public void saveTenant(){
        try{
            StringBuilder sb = new StringBuilder();
            for (Tenant t : allTenants) {
                sb.append (t.toString() + "\n");
            }
            Files.write(Paths.get("csv/tenant.csv"), sb.toString().getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for save admin's account data into csv file. It is called by admin since admin manges the account data.
    */
    public void saveAdmin(){
        try{
            StringBuilder sb = new StringBuilder();
            for (Admin a: allAdmins) {
                sb.append (a.toString() + "\n");
            }
            Files.write(Paths.get("csv/admin.csv"), sb.toString().getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for save all users' account data that send request for registration into csv file.
    toString() method in Person class (superclass of Tenant, Admin, Owner and Agent) overrided to return rows of 
    comma-delimited data.
    */
    public static void saveRegisterRequest(ArrayList<Person> users){
        try{
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < users.size(); i++){
                sb.append (users.get(i).toString() + "\n");
            }
            Files.write(Paths.get("csv/registerRequest.csv"), sb.toString().getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for save rental request into csv file. It is called by owner, agent and tenant when dealing with
    requesting or responding to rental request.
    */
    public void saveRentalRequest(){
        try{
            StringBuilder sb = new StringBuilder();
            for (RentalRequest r: allRentalRequests) {
                sb.append (r.toString() + "\n");
            }
            Files.write(Paths.get("csv/rentalRequest.csv"), sb.toString().getBytes());
        }catch(IOException e){
            e.printStackTrace();
        }
    }

     /**
     * Author: Lew Zi XUan
     *
     * this method return the index of target tenant in the static arraylist.
     * Target tenant is the tenant who want to do some actions within the system.
     */
    public int getTargetTenantByUserName(String userName) {
        for (int i = 0; i < allTenants.size(); i++) {
            if (allTenants.get(i).getUsername().equals(userName))
                return i;
        }
        return -1;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * This method is use in login UI for reading all the tenant rental request.
     * Each tenant rental request arraylist is updated when there is a file.
     * If there is no file exist, means the tenant haven't send any rental request.
     * The tenant rental request arraylist will store nothing.
     */
    public void readAllTenantRentalRequest() throws IOException {
        for(Tenant t : allTenants)
            t.readRentalRequestTxt();
    }
    

     /**
     *  Author: Lew Zi Xuan
     * 
     *  This method is to read property comment txt file.
     *  the file name is using propertyName + Comment.txt.
     *  if file exist, then update the particular property's comments arraylist.
     */
    public static ArrayList<String> readPropertyCommentTxt(int propertyID) {
        ArrayList<String> comments = null;
        try{
            String fileName = "comments/" + propertyID + "_Comment.txt";
            comments = new ArrayList<String>();

            File temp = new File(fileName);
            if(temp.exists()) {
                FileReader fileReader = new FileReader(temp);
                try {
                    BufferedReader bufferedReader = new BufferedReader(fileReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        comments.add(line);
                    }
                    fileReader.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }catch(IOException e){
            System.out.println("Property comments is not found");
        }
        return comments;
    }

    /**
     * Author: Lew Zi Xuan
     * 
     * Save property comment txt file method.
     * After leaving comment on the property, the comment is store in the comment arraylist.
     * Then it will required to save arraylist to txt file.
     */
    public static void savePropertyCommentTxt(int propertyID, ArrayList<String> comments) throws IOException {
        String fileName = "comments/" + propertyID + "_Comment.txt";

        StringBuilder sb = new StringBuilder();
        for(String str : comments){
            sb.append(str).append("\n");
        }

        Files.write(Paths.get(fileName),sb.toString().getBytes());
    }

    /* 
    Author: Adeline Fong Li Ling

    Method for reading icons to setImage in the controller class. char type is used to simplify the process
    */
    public static Image readIcon(char type){
        Image image = null;
        if(type == 'a'){
            try{
                FileInputStream input = new FileInputStream("icon/requestIcon.png");
                image = new Image(input);
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if(type == 'b'){
            try{
                FileInputStream input = new FileInputStream("icon/accountIcon.png");
                image = new Image(input);
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if(type == 'c'){
            try{
                FileInputStream input = new FileInputStream("icon/trashbin.png");
                image = new Image(input);
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if(type == 'd'){
            try{
                FileInputStream input = new FileInputStream("icon/homeIcon.png");
                image = new Image(input);
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if(type == 'e'){
            try{
                FileInputStream input = new FileInputStream("icon/rentalRequest.png");
                image = new Image(input);
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if(type == 'f'){
            try{
                FileInputStream input = new FileInputStream("icon/rent.png");
                image = new Image(input);
            }catch(IOException e){
                e.printStackTrace();
            }
        }else if(type == 'g'){
            try{
                FileInputStream input = new FileInputStream("icon/goBack.png");
                image = new Image(input);
            }catch(IOException e){
                e.printStackTrace();
            }
        }
       
        return image;
    }
}