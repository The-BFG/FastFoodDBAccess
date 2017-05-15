/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FastFoodManager;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 *
 * @author giacomo daniele
 */
public class FastFoodManager {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try{
            Class.forName ("org.postgresql.Driver");
            // Load the Driver
            Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/progetto_cristoni_guerzoni", "postgres", "algoritmi" );
            Statement stmt = conn.createStatement();
            
            String sql;
            sql = "INSERT INTO STUDENTI VALUES(1, 'rossi', 'mario'),(2, 'bianchi', 'sergio')";
            stmt.executeUpdate(sql);
            
            sql = "SELECT * FROM STUDENTI";
            ResultSet rs = stmt.executeQuery(sql);
            
            rs.close();
            stmt.close();
            conn.close();
        }
        catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        
        
        
        
        
    }
    
}
