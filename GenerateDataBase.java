package randomdb;

import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class GenerateDataBase {
    
    static ArrayList<String> extractWords(char a[]){
        ArrayList<String> words = new ArrayList<>();
        String now = "";
        for(char ch : a){
            if(ch==' ' || ch=='.' || ch==','){
                if(now.length() > 0)
                    words.add(now);
                now = "";
                continue;
            }
            if(ch >= 'a' && ch <= 'z'){
                now += ch;
                continue;
            }
            if(ch >= 'A' && ch <= 'Z'){
                now += ch;
            }
        }
        if(now.length() > 0)
            words.add(now);
        Collections.sort(words);
        return words;
    }
    
    static ArrayList<String> getAttributes(ArrayList<String> words){
        ArrayList<String> attributes = new ArrayList<>();
        String previous = "";
        int counter = 1;
        for(String str : words){
            if(str.equals(previous)){
                attributes.add(str + "_" +counter);
                counter++;
            }
            else{
                attributes.add(str + "_");
                counter = 1;
            }
            previous = str;
        }
        return attributes;
    }
    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            out.println("Driver Found");
            
            final String URL = "jdbc:mysql://localhost:3306/randomdb";
            Connection con = DriverManager.getConnection(URL, "root", "");
            out.println("Connection Established");
            
            
            String para = "This is a test paragraph. This paragraph will be used for testing Apriori alogrithm.";
            ArrayList<String> words = extractWords(para.toCharArray());
            ArrayList<String> attributes = getAttributes(words);
            
            String attributes_SQL = "";
            for(int i = 0; i < attributes.size(); i++){
                attributes_SQL += attributes.get(i) + " boolean" + ", ";
            }
            
            final String create_table = "create table if not exists transactions (id int auto_increment, "
                    + attributes_SQL +"primary key(id))";
             
            PreparedStatement ps = con.prepareStatement(create_table);
            out.println(ps.execute());
            
            
            
            final String data_entry = "insert into transactions values(default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement entry = con.prepareStatement(data_entry);
            
            for(int i = 0; i < 100; i++){
                for(int j = 1; j <= attributes.size(); j++){
                    int value = (int)(Math.random() * 1000);
                    entry.setInt(j, value%2);
                }
                entry.execute();
            }
            
            con.close();
        } 
        catch (ClassNotFoundException | SQLException ex) {
            out.println(ex.toString());
        }   
    }
}
