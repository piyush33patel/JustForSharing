package randomdb;

import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class DisplayTransactions {
    public static void main(String [] args){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            out.println("Drivers Found!");
            
            final String URL = "jdbc:mysql://localhost:3306/randomdb";
            
            Connection con = DriverManager.getConnection(URL, "root", "");
            out.println("Connection Established");
            
            final String query = "Select * from transactions";
            PreparedStatement stmt = con.prepareStatement(query);
            ResultSet result = stmt.executeQuery();
            
            String attributes[] = {"Apriori_", "This_", "This_1", "a_", "alogrithm_", "be_", "for_", "is_", "paragraph_", "paragraph_1", "test_", "testing_", "used_", "will_"};
            while(result.next()){
                int id = result.getInt(1);
                ArrayList<String> arr = new ArrayList<>();
                for(int i = 0; i < attributes.length; i++){
                    if(result.getInt(i+2) == 1){
                        arr.add(attributes[i]);
                    }
                }
                out.println(id + " = "+ arr);
            }
            
            con.close();
        }
        catch (ClassNotFoundException | SQLException ex) {
        } 
    }
}
