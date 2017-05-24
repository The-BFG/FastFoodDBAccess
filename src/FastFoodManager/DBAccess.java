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
import java.sql.Types;
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
    
    public boolean insertOrdine(String cf, String stab, ArrayList<String> elencoCibi, ArrayList<String> elencoBevande){
        try{
            cStmt = conn.prepareCall("{call ordine(?,?,?,?)}");
            cStmt.setString(1,cf);
            cStmt.setString(2,stab);
            cStmt.setArray(3, conn.createArrayOf("VARCHAR", elencoCibi.toArray()));
            cStmt.setArray(4, conn.createArrayOf("VARCHAR", elencoBevande.toArray()));
            cStmt.execute();
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    public boolean insertStabilimento(String nome, String citta, String indirizzo, int forni, int bagni, int casse){
        try{
            ps = conn.prepareStatement("INSERT INTO stabilimento(nome,citta,indirizzo,numero_forni,numero_bagni,numero_casse)");
            ps.setString(1,nome);
            ps.setString(2,citta);
            ps.setString(3,indirizzo);
            ps.setInt(4,forni);
            ps.setInt(5,bagni);
            ps.setInt(6,casse);
            ps.executeUpdate();
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    public String insertCartaFedele(String cf){
        String carta = "";
        try{
            cStmt = conn.prepareCall("{? = call fedele(?)}");
            cStmt.registerOutParameter(1,Types.CHAR); 
            cStmt.setString(2,cf);
            cStmt.execute();
            carta = cStmt.getString(1);
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return carta;
    }
    
    public ArrayList<String> getListinoCibi(String stab){
        ArrayList<String> elencoCibi = new ArrayList<String>();
        try{
            ps = conn.prepareStatement( "SELECT c.nome_cibo, c.prezzo "+
                                        "FROM listino_cibo AS c, stabilimento AS s "+
                                        "WHERE c.nome_stabilimento=s.nome AND c.nome_stabilimento=?;");
            ps.setString(1,stab);
            rs = ps.executeQuery();
            System.out.printf("\nListino cibi per lo stabilimento %s:\n%-30s %-30s\n\n",stab,"NOME CIBO","PREZZO");
            while(rs.next()){
                elencoCibi.add(rs.getString(1));
                System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getInt(2));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return elencoCibi;
    }
    
    public ArrayList<String> getListinoBevande(String stab){
        ArrayList<String> elencoBevande = new ArrayList<String>();
        try{
            ps = conn.prepareStatement("SELECT b.nome_bevanda, b.prezzo "+
                                        "FROM listino_bevande AS b, stabilimento AS s "+
                                        "WHERE b.nome_stabilimento=s.nome AND b.nome_stabilimento=?;");
            ps.setString(1,stab);
            rs = ps.executeQuery();                                
            System.out.printf("\nListino bevande per lo stabilimento %s:\n%-30s %-30s\n\n",stab,"NOME BEVANDA","PREZZO");
            while(rs.next()){
                elencoBevande.add(rs.getString(1));
                System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getInt(2));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return elencoBevande;
    }
    
     public ArrayList<String> selectCibi(String stab){
        ArrayList<String> elencoCibi;
        ArrayList<String> elencoScelte = new ArrayList<String>();
        String cibo;
        do{
            elencoCibi = getListinoCibi(stab);
            System.out.println("\nInserisci il nome del cibo che vuoi aggiungere all'ordine scegliendo tra quelli sopra indicati:\n"+
                                "Inserisci 0 quando hai terminato le scelte.");
            cibo= in.nextLine();
            if(elencoCibi.contains(cibo)){
                elencoScelte.add(cibo);
                System.out.println("Numero di " + cibo + "  che desidera ordinare?(Inserisci un numero da 1 a 50)");
                Integer qt = in.nextInt();
                elencoScelte.add(qt.toString());
            }
            else if(!"0".equals(cibo))
                System.out.println("\nCibo scelto non disponibile in questo stabilimento.\n");
        }while((!elencoCibi.contains(cibo) || !"0".equals(cibo)) && !"0".equals(cibo) );
        return elencoScelte;
    }
    
    public ArrayList<String> selectBevande(String stab){
        ArrayList<String> elencoBevande;
        ArrayList<String> elencoScelte = new ArrayList<String>();
        String bevanda;
        do{
            elencoBevande = getListinoBevande(stab);
            System.out.println("\nInserisci il nome della bevanda che vuoi aggiungere all'ordine scegliendo tra quelle sopra indicate:\n"+
                                "Inserisci 0 quando hai terminato le scelte.");
            bevanda = in.nextLine();
            if(elencoBevande.contains(bevanda)){
                elencoScelte.add(bevanda);
                System.out.println("Numero di " + bevanda + "  che desidera ordinare?(Inserisci un numero da 1 a 50)");
                Integer qt = in.nextInt();
                elencoScelte.add(qt.toString());
            }
            else if(!"0".equals(bevanda))
                System.out.println("\nBevanda scelta non disponibile in questo stabilimento.\n");
        }while((!elencoBevande.contains(bevanda) || !"0".equals(bevanda)) && !"0".equals(bevanda) );
        return elencoScelte;
    }
    
    public String selectCliente(){
        ArrayList<String> elencoClienti = new ArrayList<String>();
        String cliente = "";
        do{
            try{
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT cf,nome,cognome FROM persona ORDER BY cognome");
                System.out.printf("\nElenco di tutti i clienti :\n%-30s %-30s %-30s\n\n","CODICE FISCALE","NOME","COGNOME");
                while(rs.next()){
                    elencoClienti.add(rs.getString(1));
                    System.out.printf("%-30s %-30s %-30s\n",rs.getString(1), rs.getString(2), rs.getString(3));
                }
                System.out.println("\nInserisci il codice fiscale del cliente:");
                cliente= in.nextLine().toUpperCase();
                if(!elencoClienti.contains(cliente))
                    System.out.println("\nCodice fiscale del cliente non esistente.\n");
            }catch(SQLException e){
                System.err.println(e.getMessage());
            }
        }while(!elencoClienti.contains(cliente));
        return cliente;
    }
    
    public String selectStabilimento(){
        ArrayList<String> stabElenco = new ArrayList<String>();
        String stab = null; 
        do{
            try{
                stmt = conn.createStatement();
                rs = stmt.executeQuery("SELECT nome, citta, indirizzo FROM stabilimento;");
                System.out.println("Inserisci il nome dello stabilimento di cui vuoi visualizzare il menu scegliendo tra i seguenti:");
                System.out.printf("%-30s %-30s %-30s\n\n", "NOME STABILIMENTO","CITTA'","INDIRIZZO");
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
                System.err.println(e.getMessage());
            }
        }while(!stabElenco.contains(stab));
        return stab;
    }
}
