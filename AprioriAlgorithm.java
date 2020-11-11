package randomdb;

import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


public class AprioriAlgorithm {
    
    static ArrayList<ArrayList<String>> itemSet;
    
    static final int SUPPORT = 20;
    static final int CONFIDENCE = 40;
    static final int TOTAL_TRANSACTIONS = 100;
    
    static void generateFrequentItemSets(int x, ArrayList<String> arr, int size, Connection con, TreeSet<String> has) throws SQLException{
        if(has.size() > size) return;
        
        if(has.size()==size){
            ArrayList<String> list = new ArrayList<>();
            for(String str : has)   list.add(str);
            String now = list.get(0);
            for(int i = 1; i < size; i++){
                now += " and " + list.get(i);
            }
            
            PreparedStatement count = con.prepareStatement("select count("+now+") from transactions where "+now+" = 1");
            ResultSet res = count.executeQuery();
            res.next();
            int freq = res.getInt(1);
            int percent = (int)(freq*100/(TOTAL_TRANSACTIONS*1.0));
            if(percent >= SUPPORT)  itemSet.add(list);
            return;
        }
        
        for(int i = x; i < arr.size(); i++){
            if(!has.contains(arr.get(i))){
                has.add(arr.get(i));
                generateFrequentItemSets(i+1, arr, size, con, has);
                has.remove(arr.get(i));
            }
        }
        
    }
    
    static void generateRules(ArrayList<ArrayList<String>> frequentItems, Connection con) throws SQLException{
        for(ArrayList<String> curItem : frequentItems){
            if(curItem.size()==1)   continue;
            TreeSet<String> front = new TreeSet<>(), remain = new TreeSet<>();
            
            for(String str : curItem)  remain.add(str);
            
            mineValidRules(0, curItem, front, remain, con);
        }
    }
    
    static void mineValidRules(int i, ArrayList<String> arr, TreeSet<String> one, TreeSet<String> two, Connection con) throws SQLException{
        if(i>=arr.size())    return;
        String now = arr.get(i);
        one.add(now);
        two.remove(now);
        if(!one.isEmpty() && !two.isEmpty()){
            String item = "";
            Iterator itA = one.iterator();
            item = "" + (String)itA.next();
            while(itA.hasNext()){
                item += " and " + itA.next();
            }
            PreparedStatement count = con.prepareStatement("select count("+item+") from transactions where "+item+" = 1");
            ResultSet res = count.executeQuery();
            res.next();
            int freqA = res.getInt(1);
            Iterator itB = two.iterator();
            while(itB.hasNext()){
                item += " and " + itB.next();
            }
            count = con.prepareStatement("select count("+item+") from transactions where "+item+" = 1");
            res = count.executeQuery();
            res.next();
            int freqB = res.getInt(1);
            
            int percent = (int)(freqB*100/(freqA*1.0));
            if(percent >= CONFIDENCE)
             out.println(one + "   =>   " + two);
        }
        mineValidRules(i+1, arr, one, two, con);
        one.remove(now);
        two.add(now);
        mineValidRules(i+1, arr, one, two, con);
    }
    
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            out.println("Driver Found");
            
            final String URL = "jdbc:mysql://localhost:3306/randomdb";
            Connection con = DriverManager.getConnection(URL, "root", "");
            out.println("Connection Established");
            
            String arr[] = {"Apriori_", "This_", "This_1", "a_", "alogrithm_", "be_", "for_", "is_", "paragraph_", "paragraph_1", "test_", "testing_", "used_", "will_"};
            ArrayList<String> attributes = new ArrayList<>();
            attributes.addAll(Arrays.asList(arr));
            TreeSet<String> has = new TreeSet<>();
            itemSet = new ArrayList<>();
            int prevSize = itemSet.size();
            int idx = 1;
            while(true){
                generateFrequentItemSets(0, attributes, idx, con, has);
                if(prevSize == itemSet.size())  break;
                prevSize = itemSet.size();
                idx++;
            }
            
            generateRules(itemSet, con);
            
            con.close();
        } 
        catch (ClassNotFoundException | SQLException ex) {
            out.println(ex.toString());
        }   
    }
}