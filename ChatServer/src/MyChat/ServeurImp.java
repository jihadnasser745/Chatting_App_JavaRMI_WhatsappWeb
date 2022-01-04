/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyChat;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import MyChat.Utilisateur.Contact;
import java.rmi.server.ExportException;

/**
 *
 * @author Jihad Nasser
 */
public class ServeurImp extends UnicastRemoteObject implements IServeur{
    
    static HashMap<String, IClient> UtilisateurConnecte;
    static HashMap<String, Utilisateur> ClientsUtilisateur;

    static int newId = 0; // for generation of id for esh client 
    
    public ServeurImp() throws RemoteException {
        UtilisateurConnecte = new HashMap();
        ClientsUtilisateur = new HashMap();
    }

    @Override
    public Utilisateur connect(String id, IClient client) throws RemoteException {
        UtilisateurConnecte.put(id, client);
        System.out.println("le client de l'Id:  " + id + " est maintenant connecté  ");
        Utilisateur user = ClientsUtilisateur.get(id);
        return user;
    }

    @Override
    public void LireHorsLigneMessageDeTousLesContacts(String id) throws RemoteException {
        ClientsUtilisateur.get(id).LireHorsLigneMessageDeTousLesContactsEtLesGroupes();
        System.out.println("le client de l'Id: " + id + " lit ses messages hors ligne  ");
    }

    @Override
    public void disconnect(String id) throws RemoteException {
        UtilisateurConnecte.remove(id);
        System.out.println("le client de l'Id: " + id + " est maintenant déconnecté ");
    }

    
public static void main(String[] args) {

        try {
            System.out.println(" Création du registre, port: 2000 ...");
            LocateRegistry.createRegistry(2000);
            System.out.println(" Le registre est créé avec succès  ");

            System.out.println();

            System.out.println("[*] Running ServeurImp on rmi://127.0.0.1:2000/ServeurImp");
            IServeur server = new ServeurImp();
            System.out.println(" ServeurImp a bien été activé  ");
            System.out.println(".... ");
            Naming.rebind("rmi://127.0.0.1:2000/ServeurImp", server);
        } catch (RemoteException | MalformedURLException ex) {
            Logger.getLogger(ServeurImp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public String CreateClient(String name, String pass) throws RemoteException {
        newId++;
        Utilisateur user = new Utilisateur("cl" + newId, name, pass);
        ClientsUtilisateur.put("cl" + newId, user);
        System.out.println("Generation d'un nouveau Client avec un id: cl " + newId + " et un nom: " + name + " et un mot de pass: " + pass);
        return "cl" + newId;
    }

    @Override
    public String IdIfValidPass(String id, String pass) throws RemoteException {
        if (!ClientsUtilisateur.containsKey(id)) {
            throw new RemoteException("Un Cleint avec un id invalide: " + id + " a essayé de se connecter ");
        } else {
            String val = ClientsUtilisateur.get(id).password;
            val = val.trim();
            if (val.equals(pass)) {
                System.out.println("le client de l'id: " + id + " et un mot de pass: " + pass + " a réussie de connecter ");
                return id;
            } else {
                throw new ExportException("le client de l'id: " + id + " a tenté de se connecter avec un mot de passe non valide:  " + pass);
            }
        }
    }

    @Override
    public Utilisateur AddContact(String myId, String contactId) throws RemoteException {  
        if (!ClientsUtilisateur.containsKey(contactId)) {
            throw new ExportException(" le client de l'Id: " + myId + " ne peut pas ajouter " + contactId + " Car il'est Invalide");
        }
        if (ClientsUtilisateur.get(myId).contacts.containsKey(contactId)) {
            throw new ExportException("le client de l'Id: " + myId + " échec de l'ajout du contact:  " + contactId + " car il'est déjà dans ses contacts ");
        }
        ClientsUtilisateur.get(myId).contacts.put(contactId, new Contact(contactId, ClientsUtilisateur.get(contactId).nom));
        ClientsUtilisateur.get(contactId).contacts.put(myId, new Contact(myId, ClientsUtilisateur.get(myId).nom));
        System.out.println("le client de l'Id: " + myId + " ajouter le contact: " + contactId);
        return EnvoieMessage(myId, contactId, "Je t'ai ajouté à mes contacts ");
    }

    @Override
    public Utilisateur EnvoieMessage(String senderId, String recieverId, String message) throws RemoteException {
        ClientsUtilisateur.get(senderId).contacts.get(recieverId).AjouteMessageEnLigne(senderId, recieverId, message);
        IClient client = UtilisateurConnecte.get(recieverId);
        if (client != null) {
            ClientsUtilisateur.get(recieverId).contacts.get(senderId).AjouteMessageEnLigne(senderId, recieverId, message);
            client.Notify(ClientsUtilisateur.get(recieverId), senderId);
            System.out.println("Le Client " + senderId + " envoyer en ligne à:  " + recieverId );

        } else {
            ClientsUtilisateur.get(recieverId).contacts.get(senderId).AjouteMessageHorsLigne(senderId, recieverId, message);
            System.out.println("Le CLient " + senderId + " envoyer hors ligne à: " + recieverId );
        }
        return ClientsUtilisateur.get(senderId);
    }

    @Override
    public Utilisateur AddGroupe(String groupName, String[] memberIds) throws RemoteException {

        newId++;
        for (String memberId : memberIds) {
            ClientsUtilisateur.get(memberId).groupes.put("Groupe" + newId, new Utilisateur.Groupe("Groupe" + newId, groupName, memberIds));
        }
        System.out.println(" un nouveau groupe est créé avec les identifiants de membre: " + memberIds.toString());
        return EnvoieGroupeMessage("Groupe" + newId, memberIds[0], memberIds, "Je t'ai ajouté à ce groupe ");
    }

    @Override
    public Utilisateur EnvoieGroupeMessage(String groupId, String senderId, String[] memberIds, String message) throws RemoteException {
        for (String memberId : memberIds) {
            IClient client = UtilisateurConnecte.get(memberId);
            if (client != null) {
                ClientsUtilisateur.get(memberId).groupes.get(groupId).AjouteMessageEnLigne(senderId, groupId ,message);
                client.Notify(ClientsUtilisateur.get(memberId), groupId);
                System.out.println("Le Client " + senderId + " envoyer en ligne au groupe:  " + groupId );
            } else {
                ClientsUtilisateur.get(memberId).groupes.get(groupId).AjouteMessageHorsLigne(senderId, groupId, message);
                System.out.println("Le Client " + senderId + " envoyer hors ligne au groupe:  " + groupId );
            }
        }
        return ClientsUtilisateur.get(senderId.trim());
    }

    @Override
    public Utilisateur EnvoieBroadCast(String senderId, String[] recieversIds, String message) throws RemoteException {

        for (String recieverId : recieversIds) {
            if (!recieverId.equals(senderId)) {
                ClientsUtilisateur.get(senderId).contacts.get(recieverId).AjouteMessageEnLigne(senderId, recieverId, message);
                IClient client = UtilisateurConnecte.get(recieverId);
                if (client != null) {
                    ClientsUtilisateur.get(recieverId).contacts.get(senderId).AjouteMessageEnLigne(senderId, recieverId,message);
                    client.Notify(ClientsUtilisateur.get(recieverId), senderId);
                    System.out.println("Le Client " + senderId + " envoyer en ligne une diffusion à: " + recieverId );
                } else {
                    ClientsUtilisateur.get(recieverId).contacts.get(senderId).AjouteMessageHorsLigne(senderId, recieverId, message);
                    System.out.println("Le Client " + senderId + " envoyer hors ligne une diffusion à: " + recieverId);
                }
            }
        }
        UtilisateurConnecte.get(senderId).Notify(ClientsUtilisateur.get(senderId), "0");
        return ClientsUtilisateur.get(senderId.trim());
    }
    
}
