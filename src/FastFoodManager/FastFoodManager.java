/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FastFoodManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
/**
 *
 * @author giacomo daniele
 */
public class FastFoodManager {

    public static String readQuery(int choosed) {
        String query="";
        try{
            FileReader file;
            file=new FileReader("query.sql");
            BufferedReader buff;
            buff=new BufferedReader(file);
            for(int count=1; count<=choosed; count++){
                do{
                    query+=buff.readLine();
                }while(!query.contains(";"));
                if(count==choosed)
                    return query;
            }
        }
        catch(FileNotFoundException e) {
            System.err.println(e.getMessage());
        }   
        catch(IOException e) {
            System.err.println(e.getMessage());
        }        
        return query;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String menu = "Inserisci il numero della query da eseguire:\n"+
                "1)Inserisci dati di un nuovo cliente;\n"+
                "2)Effettua un nuovo ordine per un dato cliente;\n"+
                "3)Mostra il menu degli alimenti e il menu delle bevande di uno stabilimento;\n"+
                "4) \n"+
                "5) \n";
        Scanner in = new Scanner(System.in);
        
        //Trying loading driver to access postgresql database
        try{        

            Class.forName ("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }  
        
        //Database connection
        boolean dbconn;
        Connection conn = null;
        do {
            dbconn= false;
            try{
                System.out.println("Inserisci il nome utente:");
                String user = in.nextLine();
                System.out.println("Inserisci la password utente:");
                String psw = in.nextLine();
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/progetto_cristoni_guerzoni", user, psw);
            }catch(SQLException e){
                dbconn = true;
                System.out.println("Errore di accesso al database\nVerificare che i dati di accesso siano corretti.");
            }
        }while(dbconn);
        
        //Query chooser;
        int choosed;
        do{
            System.out.print(menu);
            choosed = in.nextInt();
            String sql=readQuery(choosed);
            try{
                Statement stmt = conn.createStatement();


                /*sql = "INSERT INTO STUDENTI VALUES(1, 'rossi', 'mario'),(2, 'bianchi', 'sergio')";
                stmt.executeUpdate(sql);

                sql = "SELECT * FROM STUDENTI";*/
                ResultSet rs = stmt.executeQuery(sql);

                rs.close();
                stmt.close();
                conn.close();
            }
            catch(SQLException e){
                System.err.println(e.getMessage());
            }
        }while(choosed != 0);
    }    
}
