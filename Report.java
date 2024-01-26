import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.PrintWriter;
import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/*
    This is a class aggregated by Admin to create Report for property displayed on the homepage. 
    It is associated with the report creation date and time.
*/
public class Report {

    private ArrayList<Property> properties;
    private ArrayList<Property> searchResult;
    private char[] hyphens = new char[360];

     /* 
    Author: Adeline Fong Li Ling

    Constructor that set searchResult of the properties and the properties without search.
    Either one will be used later.
    Fill the char Array with '-' which is for table display
    */
    public Report(ArrayList<Property> searchResult, ArrayList<Property> properties){
        this.searchResult = searchResult;
        this.properties = properties;
        Arrays.fill(hyphens, '-');
    }

     /* 
    Author: Adeline Fong Li Ling

    Method to save table of properties into a txt file. This method is called in HomePropertyController
    and used by Admin only. The report is being saved to the selected directory by filechooser.
    */
    public void saveTable(File file){

        ArrayList<Property> tableProperties = new ArrayList<Property>();
        // If search is not done yet, use all properties to generate report
        // If search is already executed, use searchResult to generate report
        if(searchResult == null){
            tableProperties = properties;
        }else{
            tableProperties = searchResult;
        }
        LocalDateTime dateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String reportDateTime = dateTime.format(formatter);

        try {
            FileWriter fileWriter = new FileWriter(file, true); 
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("Report created on: " + reportDateTime + "\n");
            for(char hyphen: hyphens)
                printWriter.print(hyphen);
            printWriter.println();
            printWriter.println(String.format("%-12s  %-33s  %-24s  %-43s  %-110s  %-10s  %-15s  %-13s  %-12s  %-16s  %-25s  %-12s  %-15s", 
            "Property ID", "Project", "Property Type", "Property Name", "Address", "Size", "Rental Price", "Rental Rate", "No. of Room", 
            "No. of Bathroom", "Owned By", "Status", "State"));  
            printWriter.println();  
            for(char hyphen: hyphens)
                printWriter.print(hyphen);
            printWriter.println();
            for(Property prop: tableProperties){  
                printWriter.println(String.format("%-12d  %-33s  %-24s  %-43s  %-110s  %-10d  %-15.2f  %-13.1f  %-12d  %-16d  %-25s  %-12s  %-15s", 
                prop.getPropertyID(), prop.getProjectType(), prop.getPropertyType(), prop.getPropertyName(), prop.getAddress(), 
                prop.getSize(), prop.getRentPrice(), prop.getRentRate(), prop.getNumOfRoom(), prop.getNumOfBathroom(), prop.getOwnedBy(), prop.getStatus(), prop.getState()));  
            }  
            for(char hyphen: hyphens)
                printWriter.print(hyphen);
            printWriter.close();
          
        }catch (IOException ex){
            Logger.getLogger(HomePropertyController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
