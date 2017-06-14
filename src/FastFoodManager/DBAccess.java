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
import java.util.Calendar;
import java.util.Scanner;
import java.util.Calendar;;
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
            dbconn = true;
            try{
                System.out.println("Inserisci il nome utente amministratore postgres:");
                String user = in.nextLine();
                System.out.println("Inserisci la password utente:");
                String psw = in.nextLine();
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db_cristoni_guerzoni", user, psw);
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
            System.out.println(e.getMessage());
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
            ps = conn.prepareStatement("INSERT INTO stabilimento(nome,citta,indirizzo,numero_forni,numero_bagni,numero_casse) VALUES (?,?,?,?,?,?)");
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
    
    public boolean insertFornitura(String stab, String fornitore, ArrayList<String> elencoProdotti){
        try{
            cStmt = conn.prepareCall("{call fornitura(?,?,?)}");
            cStmt.setString(1,stab);
            cStmt.setString(2,fornitore);
            cStmt.setArray(3, conn.createArrayOf("VARCHAR", elencoProdotti.toArray()));
            cStmt.execute();
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    public boolean updateListinoCibi(String stab, String cibo){
        try{
            ps = conn.prepareStatement("UPDATE listino_cibo SET prezzo = ? WHERE nome_cibo = ? and nome_stabilimento = ?;");
            ps.setFloat(1,nuovoPrezzo(cibo));
            ps.setString(2,cibo);
            ps.setString(3,stab);
            ps.executeUpdate();
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    public boolean updateListinoBevande(String stab, String bevanda){
        try{
            ps = conn.prepareStatement("UPDATE listino_bevande SET prezzo = ? WHERE nome_bevanda = ? and nome_stabilimento = ?;");
            ps.setFloat(1,nuovoPrezzo(bevanda));
            ps.setString(2,bevanda);
            ps.setString(3,stab);
            ps.executeUpdate();
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
    }
    
    public boolean updateInventarioFornitore(String fornitore, String prodotto){
        try{
            ps = conn.prepareStatement("UPDATE inventario_fornitore SET costo = ? WHERE p_iva = ? and nome_prodotto = ?;");
            ps.setFloat(1,nuovoPrezzo(prodotto));
            ps.setString(2,fornitore);
            ps.setString(3,prodotto);
            ps.executeUpdate();
        }catch(SQLException e){
            System.err.println(e.getMessage());
            return false;
        }
        return true;
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
                System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getFloat(2));
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
                System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getFloat(2));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return elencoBevande;
    }
    
    public ArrayList<String> getFornitori(){
        ArrayList<String> elencoFornitori = new ArrayList<String>();
        try{
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT p_iva, nome_ditta, citta, indirizzo  FROM fornitore;");
            System.out.printf("\nElenco dei fornitori:\n%-30s %-30s %-30s %-30s\n\n","PARTITA IVA","NOME DITTA","CITTA'","INDIRIZZO");
            while(rs.next()){
                elencoFornitori.add(rs.getString(1));
                System.out.printf("%-30s %-30s %-30s %-30s\n",rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return elencoFornitori;
    }
    
    public ArrayList<String> getProdotti(String fornitore){
        ArrayList<String> elencoProdotti = new ArrayList<String>();
        try{
            ps = conn.prepareStatement("SELECT p.nome, i.costo "+
                                        "FROM prodotto AS p, inventario_fornitore AS i "+
                                        "WHERE p.nome = i.nome_prodotto AND i.p_iva=?;");
            ps.setString(1,fornitore);
            rs = ps.executeQuery();                                
            System.out.printf("\nElenco prodotti venduti da %s:\n%-30s %-30s\n\n",fornitore,"NOME PRODOTTO","PREZZO");
            while(rs.next()){
                elencoProdotti.add(rs.getString(1));
                System.out.printf("%-30s %-30s\n",rs.getString(1), rs.getFloat(2));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
        return elencoProdotti;
    }
    
    public void showClientHistory(String cf){
        try{
            ps = conn.prepareStatement("select data,codice,nome_stabilimento,alimento,quantita "
                    + "from ("
                        + "(select o.cf,o.data,o.codice,o.nome_stabilimento,b.nome_bevanda as alimento,b.quantita "
                        + "from ordine as o, bevanda_ordine as b order by o.data,o.codice,nome_bevanda) "
                        + "union "
                        + "(select o.cf,o.data,o.codice,o.nome_stabilimento,c.nome_cibo as alimento,c.quantita "
                        + "from ordine as o, cibo_ordine as c order by o.data,o.codice,c.nome_cibo )"
                    + ") as t where cf = ?  ORDER BY alimento;");
            ps.setString(1,cf);
            rs = ps.executeQuery();
            System.out.println("\nElenco ordini del cliente con codice fiscale "+ cf +" :");
            System.out.printf("%-12s %-10s %-20s %-20s %-10s\n\n","DATA","SCONTRINO","STABILIMENTO","ALIMENTO","QUANTITÀ");
            while(rs.next()){
                System.out.printf("%-12s %-10s %-20s %-20s %-10s\n",rs.getString(1), rs.getString(2),rs.getString(3),rs.getString(4),rs.getInt(5));
            }
        }catch(SQLException e){
            System.err.println(e.getMessage());
        }
    }
    
    public ArrayList<String> selectProdotti(String fornitore){
        ArrayList<String> elencoProdotti;
        ArrayList<String> elencoScelti = new ArrayList<String>();
        String prodotto;
        do{
            elencoProdotti = getProdotti(fornitore);
            System.out.println("\nInserisci il nome dei prodotti che vuoi aggiungere alla fornitura scegliendo tra quelli sopra indicati:\n"+
                                "Inserisci 0 quando hai terminato le scelte.");
            prodotto = in.nextLine();
            if(elencoProdotti.contains(prodotto)){
                elencoScelti.add(prodotto);
                System.out.println("Quantità di " + prodotto + " che desidera ordinare?(Inserisci un numero da 1 a 100)");
                Integer qt = in.nextInt();
                elencoScelti.add(qt.toString());
                String scadenza = setScadenzaProdotto();
                System.out.println(scadenza);
                elencoScelti.add(scadenza);
            }
            else if(!"0".equals(prodotto))
                System.out.println("\nProdotto scelta non disponibile in questo stabilimento.\n");
        }while((!elencoProdotti.contains(prodotto) || !"0".equals(prodotto)) && !"0".equals(prodotto) );
        return elencoScelti;
    }
    
    public String selectFornitore(){
        String piva;
        ArrayList<String> elencoFornitori;
        do{
            elencoFornitori = getFornitori();
            System.out.println("\nIscerisci la partita iva del fornitore che vuoi scegliere:");
            piva = in.nextLine();
            if(!elencoFornitori.contains(piva))
                System.out.println("Partita iva non presente in elenco.");
        }while(!elencoFornitori.contains(piva));
        return piva;
    }
    
    public String selectProdotto(String stab){
        String prodotto;
        ArrayList<String> elencoProdotti;
        do{
            elencoProdotti = getProdotti(stab);
            System.out.println("\nInserisci il nome del prodotto di cui vuoi cambiare il prezzo scegliendo tra quelli sopra indicati:");
            prodotto = in.nextLine();
            if(!elencoProdotti.contains(prodotto))
                System.out.println("\nProdotto scelto non disponibile presso questo fornitore.\n");
        }while(!elencoProdotti.contains(prodotto));
        return prodotto;
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
    
    public String selectCibo(String stab){
        String cibo;
        ArrayList<String> elencoCibi;
        do{
            elencoCibi = getListinoCibi(stab);
            System.out.println("\nInserisci il nome del cibo di cui vuoi cambiare il prezzo scegliendo tra quelli sopra indicati:");
            cibo = in.nextLine();
            if(!elencoCibi.contains(cibo))
                System.out.println("\nCibo scelto non disponibile in questo stabilimento.\n");
        }while(!elencoCibi.contains(cibo));
        return cibo;
    }
    
    public String selectBevanda(String stab){
        String bevanda;
        ArrayList<String> elencoBevande;
        do{
            elencoBevande = getListinoBevande(stab);
            System.out.println("\nInserisci il nome della bevanda di cui vuoi cambiare il prezzo scegliendo tra quelli sopra indicati:");
            bevanda = in.nextLine();
            if(!elencoBevande.contains(bevanda))
                System.out.println("\nBevanda scelta non disponibile in questo stabilimento.\n");
        }while(!elencoBevande.contains(bevanda));
        return bevanda;
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
                rs = stmt.executeQuery("SELECT p.cf,p.nome,p.cognome FROM persona as p,cliente as c WHERE p.cf=c.cf ORDER BY p.cognome");
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
                rs = stmt.executeQuery("SELECT nome, citta, indirizzo FROM stabilimento ORDER BY nome;");
                System.out.println("Inserisci il nome dello stabilimento che vuoi scegliere tra i seguenti:");
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
    
    public String setScadenzaProdotto(){
        String date ="";
        Boolean valDate =true;
        String yyyy, MM, gg;
        Calendar today = Calendar.getInstance();
        Calendar cal = Calendar.getInstance();
        in.nextLine();
        do{
            try{
                date = "";
                valDate = true;
                System.out.println("Inserisci l'anno di scadenza(yyyy):");
                yyyy = in.nextLine();
                date += yyyy+"-";
                cal.set(Calendar.YEAR, Integer.parseInt(yyyy));
                System.out.println("Inserisci il mese di scadenza(MM):");
                MM = in.nextLine();
                date += MM+"-";
                cal.set(Calendar.MONTH, Integer.parseInt(MM));
                System.out.println("Inserisci il giorno di scadenza(gg):");
                gg = in.nextLine();
                date += gg;
                cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(gg));

                try{
                    cal.getTime();
                }catch(Exception e){
                    valDate = false;
                    System.out.println("Data inserita non corretta");
                }
                //if(!(today.get(Calendar.YEAR)<= Integer.parseInt(yyyy) && today.get(Calendar.MONTH)<= Integer.parseInt(MM) && today.get(Calendar.DAY_OF_MONTH)< Integer.parseInt(gg))){
                if(today.after(cal)){
                    valDate = false;
                    System.out.println("La data di scadenza deve essere più grande della data odierna.");
                }
            }catch(NumberFormatException nf){
                valDate = false;
                System.out.println("Valore inserito non corretto per una data.");
            }
        }while(valDate==false);
        return date;
    }
    
    public Float nuovoPrezzo(String alimento){
        System.out.println("Inserisci il prezzo di: "+ alimento);
        return in.nextFloat();
    }
}
