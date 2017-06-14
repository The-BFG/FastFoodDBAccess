/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FastFoodManager;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author giacomo daniele
 */
public class FastFoodManager {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String genericMenu = "Che genere di operazioni vuoi eseguire sul Database:\n"+
                "1)Inserimento di nuovi dati;\n"+
                "2)Aggiornamento di dati gia esistenti;\n"+
                "3)Interrogazioni di default;\n"+
                "0)Esci.";
        String insertMenu = "Inserisci il numero dell'operazione di inserimento da eseguire:\n"+
                "1)Inserisci dati di un nuovo cliente;\n"+
                "2)Inserisci un nuovo ordine per un dato cliente in uno stabilimento;\n"+
                "3)Inserisci una nuova fornitura per uno stabilimento da parte di un dato fornitore;\n"+
                "4)Aggiungi un cliente ai clienti fedeli.\n"+
                "5)Apertura nuovo stabilimento\n"+
                "0)Torna al menu precedente.";
        String updateMenu = "Inserisci il numero dell'operazione di aggiornamento da eseguire:\n"+
                "1)Aggiorna il prezzo di un prodotto venduto da un determinato fornitore;\n"+
                "2)Aggiorna il prezzo di un cibo presente nel menu di uno stabilimento;\n"+
                "3)Aggiorna il prezzo di una bevanda presente nel menu di uno stabilimento;\n"+
                "0)Tornaal menu precedente.";
        String queryMenu = "Inserisci il numero dell'operazione di visualizzazione da eseguire:\n"+
                "1)Mostra il menu degli alimenti e il menu delle bevande di uno stabilimento;\n"+
                "2)Mostra storico degli ordini di un cliente in ordine cronologico;\n"+
                "0)Torna al menu precedente.";
        String cf,nome,cognome,indirizzo,citta,mail,codiceFedeltà,fornitore,stab;
        int forni,bagni,casse;
        
        //Database connection and check user data
        DBAccess access = new DBAccess();
        //Menu;
        int choosedG=-1;
        if(access.getStatus() == true){
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
                switch(choosedG){
                    case 0:
                        break;
                    case 1:
                        System.out.println(insertMenu);
                        int choosedI = in.nextInt();
                        switch(choosedI){
                            case 1:
                                System.out.println("Inserisci il codice fiscale del nuovo cliente");
                                in.nextLine();
                                cf = in.nextLine();
                                System.out.println("Inserisci il nome:");
                                nome = in.nextLine();
                                System.out.println("Inserisci il cognome:");
                                cognome = in.nextLine();
                                System.out.println("Inserisci l'indirizzo di residenza:");
                                indirizzo = in.nextLine();
                                System.out.println("Inserisci la città di residenza:");
                                citta = in.nextLine();
                                System.out.println("Inserisci la mail del cliente:");
                                mail = in.nextLine();
                                
                                access.insertPersona(cf, nome, cognome, indirizzo, citta);
                                access.insertCliente(cf, mail);
                                break;
                            case 2:
                                cf = access.selectCliente();
                                stab = access.selectStabilimento();
                                ArrayList<String> elencoCibi = access.selectCibi(stab);
                                ArrayList<String> elencoBevande = access.selectBevande(stab);
                                if(access.insertOrdine(cf, stab, elencoCibi, elencoBevande))
                                    System.out.println("Inserimento avvenuto con successo.");
                                else
                                    System.out.println("Inserimento non riuscito.");
                                break;
                            case 3:
                                stab = access.selectStabilimento();
                                fornitore = access.selectFornitore();
                                ArrayList<String> elencoProdotti = access.selectProdotti(fornitore);
                                if(access.insertFornitura(stab,fornitore,elencoProdotti))
                                    System.out.println("Inserimento avvenuto con successo.");
                                else
                                    System.out.println("Inserimento non riuscito.");
                                break;
                            case 4:
                                cf = access.selectCliente();
                                codiceFedeltà = access.insertCartaFedele(cf);
                                System.out.println("Il numero della carta fedelta' è:\t" + codiceFedeltà);
                                break;
                            case 5:
                                System.out.println("Inserisci il nome del nuovo stabilimento:");
                                in.nextLine();
                                nome = in.nextLine();
                                System.out.println("Inserisci la città:");
                                citta = in.nextLine();
                                System.out.println("Inserisci l'indirizzo:");
                                indirizzo = in.nextLine();
                                System.out.println("Inserisci il numero di forni:");
                                forni = in.nextInt();
                                System.out.println("Inserisci il numero di bagni:");
                                bagni = in.nextInt();
                                System.out.println("Inserisci il numero di casse:");
                                casse = in.nextInt();
                                if(access.insertStabilimento(nome,citta,indirizzo,forni,bagni,casse))
                                    System.out.println("Inserimento avvenuto con successo.");
                                else
                                    System.out.println("Inserimento non avvenuto.");
                                break;
                        }
                        break;
                    case 2:
                        System.out.println(updateMenu);
                        int choosedU = in.nextInt();
                        switch(choosedU){
                            case 1:
                                fornitore = access.selectFornitore();
                                String prodotto = access.selectProdotto(fornitore);
                                if(access.updateInventarioFornitore(fornitore,prodotto))
                                    System.out.println("Aggiornamento avvenuto con successo.");
                                else
                                    System.out.println("Aggiornamento non avvenuto.");
                                break;
                            case 2:
                                in.nextLine();
                                stab = access.selectStabilimento();
                                String cibo = access.selectCibo(stab);
                                if(access.updateListinoCibi(stab,cibo))
                                    System.out.println("Aggiornamento avvenuto con successo.");
                                else
                                    System.out.println("Aggiornamento non avvenuto.");
                                break;
                            case 3:
                                stab = access.selectStabilimento();
                                String bevanda = access.selectBevanda(stab);
                                if(access.updateListinoBevande(stab,bevanda))
                                    System.out.println("Aggiornamento avvenuto con successo.");
                                else
                                    System.out.println("Aggiornamento non avvenuto.");
                                break;
                        }
                        break;
                    case 3:
                        System.out.println(queryMenu);
                        int choosedQ = in.nextInt();
                        switch(choosedQ){
                            case 1:
                                stab = access.selectStabilimento();
                                access.getListinoCibi(stab);
                                access.getListinoBevande(stab);
                                break;
                            case 2:
                                cf = access.selectCliente();
                                access.showClientHistory(cf);
                                break;
                        }
                        break;
                    default:
                        System.out.println("Opzione non valida.");
                }
            }while(choosedG != 0);
        }
        else
            System.out.println("Errore di accesso al Database.");
    }    
}
