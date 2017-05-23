/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FastFoodManager;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author giacomo daniele
 */
public class DBAccess {
    static Scanner in = new Scanner(System.in);
    static Connection conn = null;
    static PreparedStatement ps = null;
    static Statement stmt = null;
    static CallableStatement cStmt;
    static ResultSet rs;
    private boolean dbconn = true;
    
    public DBAccess(){
        try{        
            Class.forName ("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            System.err.println(e.getMessage());
        }  
        do{
            try{
                System.out.println("Inserisci il nome utente amministratore postgres:");
                String user = in.nextLine();
                System.out.println("Inserisci la password utente:");
                String psw = in.nextLine();
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/progetto_cristoni_guerzoni", user, psw);
            }catch(SQLException e){
                dbconn = false;
                System.out.println("Errore di accesso al database\nVerificare che i dati di accesso siano corretti.\n");
            }
        }while(!dbconn);
    }
    
    public boolean getStatus() {
        return dbconn;
    }
    
    public void closeConnection() {
        try{
            if(ps!=null)
                ps.close();
            if(stmt!=null)
                stmt.close();
            if(ps!=null)
                ps.close();
            if(conn!=null)
                conn.close();
        }
        catch(SQLException e){
            System.err.println(e.getMessage());
        }
        dbconn = false;
    }
    
    public String selectStabilimento(){
        ArrayList<String> stabElenco = new ArrayList<String>();
        String stab = null; 
        do{
            try{
                stmt = conn.createStatement();
                System.out.println("Inserisci il nome dello stabilimento di cui vuoi visualizzare il menu scegliendo tra i seguenti:");
                rs = stmt.executeQuery("SELECT nome, citta, indirizzo FROM stabilimento;");
                while(rs.next()){
                    stabElenco.add(rs.getString(1));
                    System.out.printf("%-30s %-30s %-30s\n",rs.getString(1), rs.getString(2), rs.getString(3));
                }
                System.out.println("\nNome stabilimento scelto?");
                stab = in.nextLine();
                if(!stabElenco.contains(stab))
                    System.out.println("\nNome stabilimento non presente in elenco, verificare di aver inserito il nome corretto(comprese maiuscole).\n");
            }
            catch(SQLException e){
                System.out.println("Errore in lettura dal DB");
            }
        }while(!stabElenco.contains(stab));
        return stab;
    }
    
    public boolean insertPersona(String cf, String nome, String cognome, String indirizzo, String citta){
        try{
            ps = conn.prepareStatement("INSERT INTO PERSONA(cf,nome,cognome,indirizzo_residenza,citta_residenza) VALUES (?,?,?,?,?);");
            ps.setString(1,cf.toUpperCase());
            ps.setString(2,nome);
            ps.setString(3,cognome);
            ps.setString(4,indirizzo);
            ps.setString(5,citta);
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println("Inserimento della persona non avvenuto.");
            return false;
        }
        System.out.println("Inserimento della persona avvenuto con successo.");
        return true;
    }
    
    public boolean insertCliente(String cf, String mail){
        try{
            ps = conn.prepareStatement("INSERT INTO CLIENTE(cf,email,numero_carta) VALUES (?,?,?);");
            ps.setString(1,cf);
            ps.setString(2,mail);
            ps.setString(3,null);
            ps.executeUpdate();
        }catch(SQLException e){
            System.out.println("Inserimento del cliente non avvenuto.");
            return false;
        }
        System.out.println("Inserimento del cliente avvenuto con successo.");
        return true;
    }
    
    public void getListinoCibi(String stab){
        try{
            ps = conn.prepareStatement( "SELECT c.nome_cibo, c.prezzo "+
                                        "FROM listino_cibo AS c, stabilimento AS s "+
                                        "WHERE c.nome_stabilimento=s.nome AND c.nome_stabilimento=?;");
            ps.setString(1,stab);
            rs = ps.executeQuery();
            System.out.printf("\nListino cibi per lo stabilimento %s:\n%-30s %-30s\n",stab,"Nome cibo","Prezzo");
            while(rs.next()){
                System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getInt(2));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    
    public void getListinoBevande(String stab){
        try{
            ps = conn.prepareStatement("SELECT b.nome_bevanda, b.prezzo "+
                                                                "FROM listino_bevande AS b, stabilimento AS s "+
                                                                "WHERE b.nome_stabilimento=s.nome AND b.nome_stabilimento=?;");
            ps.setString(1,stab);
            rs = ps.executeQuery();                                
            System.out.printf("\nListino bevande:\n%-30s %-30s\n","Nome bevanda","Prezzo");
            while(rs.next()){
                System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getInt(2));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    
    public void getClienti(){
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT cf,nome,cognome FROM persona ORDER BY cognome");
            System.out.printf("\nElenco di tutti i clienti :\n%-30s %-30s %-30s\n","Codice fiscale","Nome","Cognome");
            while(rs.next())
                    System.out.printf("%-30s %-30s %-30s\n",rs.getString(1), rs.getString(2), rs.getString(3));
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        
    }
}
