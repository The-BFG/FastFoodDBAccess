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
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author giacomo daniele
 */
public class FastFoodManager {

    /**
     *
     * @param choosed
     * @return
     */
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
                if(count==choosed){
                    buff.close();
                    file.close();
                    return query;
                }
                else
                    query="";
            }
            buff.close();
            file.close();
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
     *
     * @param choosed
     * @param file
     * @return
     */
    public static ArrayList<String> readQuery (Integer choosed, String file){
        ArrayList<String> query = new ArrayList<String>();
        
        try{
            BufferedReader buff;
            buff=new BufferedReader(new FileReader(file));
            String row;
            //System.out.println("--" + choosed.toString());
            
            while((row=buff.readLine()) != null ) {
                if(row.contains("--" + choosed.toString())){
                    do{
                       row = buff.readLine();
                       if(row.contains("--")) break;
                       query.add(row);
                       while(!row.contains(";")){
                           row = buff.readLine();
                           query.set(query.size()-1, (query.get(query.size()-1)+" "+row));
                           //System.out.println(row);
                       }
                       //System.out.println(query.get(query.size()-1));
                    }while(!row.contains("--"));
                }
            }
            System.out.println("\n");
            buff.close();
        }
        catch(FileNotFoundException e) {
            System.out.println(e.getMessage());
        }   
        catch(IOException e) {
            System.out.println(e.getMessage());
        } 
        return query;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String genericMenu = "Che genere di operazioni vuoi eseguire sul Database:\n"+
                "1)Inserimento di nuovi dati;\n"+
                "2)Aggiornamento di dati gia esistenti;\n"+
                "3)Interrogazioni di default;\n"+
                "0)Esci.";
        String insertMenu = "Inserisci il numero dell'operazione di inserimento da eseguire:\n"+
                "1)Inserisci dati di un nuovo cliente;\n"+
                "2)Inserisci un nuovo ordine per un dato cliente;\n"+
                "3)Inserisci una nuova fornitura per uno stabilimento da parte di un dato fornitore;\n"+
                "4)Aggiungi un cliente ai clienti fedeli.\n"+
                "5)Apertura nuovo stabilimento\n"+
                "0)Torna al menu precedente.";
        String updateMenu = "Inserisci il numero dell'operazione di aggiornamento da eseguire:\n"+
                "1)Aggiorna il prezzo di un prodotto venduto da un determinato fornitore;\n"+
                "2)Aggiorna il prezzo di un alimento presente nel menu di uno stabilimento;\n"+
                "0)Tornaal menu precedente.";
        String queryMenu = "Inserisci il numero dell'operazione di visualizzazione da eseguire:\n"+
                "1)Mostra il menu degli alimenti e il menu delle bevande di uno stabilimento;\n"+
                "2)Mostra storico degli ordini di un cliente in ordine cronologico;\n"+
                "0)Torna al menu precedente.";
        Scanner in = new Scanner(System.in);
        Connection conn = null;
        PreparedStatement ps = null;
        Statement stmt = null;
        CallableStatement cStmt = null;
        ResultSet rs;
        //Trying loading driver to access postgresql database
        try{        

            Class.forName ("org.postgresql.Driver");
        }
        catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }  
        
        //Database connection and check user dataprogramma
        boolean dbconn= false;
        do {
            try{
                System.out.println("Inserisci il nome utente amministratore postgres:");
                String user = in.nextLine();
                System.out.println("Inserisci la password utente:");
                String psw = in.nextLine();
                conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/progetto_cristoni_guerzoni", user, psw);
            }catch(SQLException e){
                dbconn = true;
                System.out.println("Errore di accesso al database\nVerificare che i dati di accesso siano corretti.\n");
            }
        }while(dbconn);
        
        //Menu;
        int choosedG=-1;
        //System.out.println(System.getProperty("user.dir"));
        if(conn!=null){
            do{
                do{
                    System.out.println("\n");
                    System.out.println(genericMenu);
                    try{
                        choosedG = in.nextInt();
                    }catch(InputMismatchException imsm){
                        System.out.println("Scelta non ammessa");
                        in.nextLine();
                    }
                }while(choosedG == -1);
                try{
                    switch(choosedG){
                        case 0:
                            break;
                        case 1:
                            System.out.println(insertMenu);
                            int choosedI = in.nextInt();
                            ArrayList<String> query;
                            switch(choosedI){
                                case 1:
                                    query = readQuery(1, "../insert.sql");
                                    ps = conn.prepareStatement("INSERT INTO PERSONA(cf,nome,cognome,indirizzo_residenza,citta_residenza) VALUES (?,?,?,?,?);");
                                    System.out.println("Inserisci il codice fiscale del nuovo cliente");
                                    in.nextLine();
                                    String cf = in.nextLine();
                                    System.out.println(cf.toUpperCase());
                                    ps.setString(1,cf);
                                    System.out.println("Inserisci il nome:");
                                    ps.setString(2,in.nextLine());
                                    System.out.println("Inserisci il cognome:");
                                    ps.setString(3,in.nextLine());
                                    System.out.println("Inserisci l'indirizzo di residenza:");
                                    ps.setString(4,in.nextLine());
                                    System.out.println("Inserisci la città di residenza:");
                                    ps.setString(5,in.nextLine());
                                    try{
                                        ps.executeUpdate();
                                    }catch(SQLException e){
                                        System.err.println(e.getMessage());
                                    }finally{
                                        ps = conn.prepareStatement("INSERT INTO CLIENTE(cf,email,numero_carta) VALUES (?,?,?);");
                                        ps.setString(1,cf.toUpperCase());
                                        System.out.println("Inserisci la mail del cliente:");
                                        ps.setString(2,in.nextLine());
                                        ps.setString(3,null);
                                        ps.executeUpdate();
                                        System.out.println("Inserimento avvenuto con successo.");
                                    }
                                    break;
                                case 2:

                                    break;
                            }
                            break;
                        case 2:
                            System.out.println(updateMenu);
                            int choosedU = in.nextInt();
                            switch(choosedU){
                                case 1:
                                    break;
                            }
                            break;
                        case 3:
                            System.out.println(queryMenu);
                            int choosedQ = in.nextInt();
                            switch(choosedQ){
                                case 1:
                                    query = readQuery(1, "../query.sql");
                                    stmt = conn.createStatement();
                                    System.out.println("Inserisci il nome dello stabilimento di cui vuoi visualizzare il menu scegliendo tra i seguenti:");
                                    //rs = stmt.executeQuery(query.get(0));
                                    rs = stmt.executeQuery("SELECT nome, citta, indirizzo FROM stabilimento;");
                                    ArrayList<String> stabElenco = new ArrayList<String>();
                                    while(rs.next()){//rs.close();
                                        stabElenco.add(rs.getString(1));
                                        System.out.printf("%-30s %-30s %-30s\n",rs.getString(1), rs.getString(2), rs.getString(3));
                                    }
                                    System.out.println("\nNome stabilimento scelto?");
                                    in.nextLine();
                                    String stab = in.nextLine();
                                    if(stabElenco.contains(stab)){
                                        ps = conn.prepareStatement( "SELECT c.nome_cibo, c.prezzo "+
                                                                "FROM listino_cibo AS c, stabilimento AS s "+
                                                                "WHERE c.nome_stabilimento=s.nome AND c.nome_stabilimento=?;");
                                        ps.setString(1,stab);
                                        rs = ps.executeQuery();
                                        System.out.printf("\nListino cibi per lo stabilimento %s:\n%-30s %-30s\n",stab,"Nome cibo","Prezzo");
                                        while(rs.next()){
                                            System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getInt(2));
                                        } 
                                        //ps = conn.prepareStatement(query.get(2));
                                        ps = conn.prepareStatement("SELECT b.nome_bevanda, b.prezzo "+
                                                                    "FROM listino_bevande AS b, stabilimento AS s "+
                                                                    "WHERE b.nome_stabilimento=s.nome AND b.nome_stabilimento=?;");
                                        ps.setString(1,stab);
                                        rs = ps.executeQuery();                                
                                        System.out.printf("\nListino bevande:\n%-30s %-30s\n","Nome bevanda","Prezzo");
                                        while(rs.next()){
                                            System.out.printf("%-30s %-30s\n",rs.getString(1),  rs.getInt(2));
                                        }
                                    }else
                                        System.out.println("\nNome stabilimento non presente in elenco, verificare di aver inserito il nome corretto(comprese maiuscole)");
                                    break;
                            }
                            break;
                        default:
                            System.out.println("Opzione non valida.");
                    }
                }catch(SQLException e){
                    System.err.println(e.getMessage());
                }
            
            
            /*
                            ps= conn.prepareStatement(sql);
                            stmt = conn.createStatement();
                sql = "INSERT INTO STUDENTI VALUES(1, 'rossi', 'mario'),(2, 'bianchi', 'sergio')";
                stmt.executeUpdate(sql); sql = "SELECT * FROM STUDENTI";
                //rs = stmt.executeQuery(sql            }￼
            
            cStmt = conn.prepareCall(query.get(1).replace(';', ' '));
                                cStmt.setString(1, in.nextLine());
                                rs = cStmt.executeQuery();*/
            }while(choosedG != 0);
        }
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
    }    
}
