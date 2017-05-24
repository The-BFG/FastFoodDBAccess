/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package FastFoodManager;
import java.io.IOException;
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
                "2)Aggiorna il prezzo di un alimento presente nel menu di uno stabilimento;\n"+
                "0)Tornaal menu precedente.";
        String queryMenu = "Inserisci il numero dell'operazione di visualizzazione da eseguire:\n"+
                "1)Mostra il menu degli alimenti e il menu delle bevande di uno stabilimento;\n"+
                "2)Mostra storico degli ordini di un cliente in ordine cronologico;\n"+
                "0)Torna al menu precedente.";

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
                                String cf = in.nextLine();
                                System.out.println("Inserisci il nome:");
                                String nome = in.nextLine();
                                System.out.println("Inserisci il cognome:");
                                String cognome = in.nextLine();
                                System.out.println("Inserisci l'indirizzo di residenza:");
                                String indirizzo = in.nextLine();
                                System.out.println("Inserisci la città di residenza:");
                                String citta = in.nextLine();
                                System.out.println("Inserisci la mail del cliente:");
                                String mail = in.nextLine();
                                
                                access.insertPersona(cf, nome, cognome, indirizzo, citta);
                                access.insertCliente(cf, mail);
                                break;
                            case 2:
                                String cliente = access.selectCliente();
                                String stab = access.selectStabilimento();
                                ArrayList<String> elencoCibi = access.selectCibi(stab);
                                ArrayList<String> elencoBevande = access.selectBevande(stab);
                                if(access.insertOrdine(cliente, stab, elencoCibi, elencoBevande))
                                    System.out.println("Inserimento avvenuto con successo.");
                                else
                                    System.out.println("Inserimento non riuscito.");
                            case 3:

                                break;
                            case 4:
                                
                                break;
                            case 5:
                                
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
                                String stab = access.selectStabilimento();
                                access.getListinoCibi(stab);
                                access.getListinoBevande(stab);
                                break;
                            case 2:

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
