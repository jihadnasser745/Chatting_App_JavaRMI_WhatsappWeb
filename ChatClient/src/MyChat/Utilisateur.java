/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyChat;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
/**
 *
 * @author Jihad Nasser
 */
public class Utilisateur implements Serializable {
    
    public String Id, nom, password;
    public HashMap<String, Contact> contacts; //hashmap for data of contact to a client 
    public HashMap<String, Groupe> groupes;     //hashmap for data of Groupe to a client  
    
    public Utilisateur(String Id, String name, String pass) {
        this.Id = Id;
        this.nom = name;
        this.password = pass;
        this.contacts = new HashMap();
        this.groupes=new HashMap();
    }
    
    public void LireHorsLigneMessageDeTousLesContactsEtLesGroupes() {
        for (Contact con : contacts.values()) {
            con.LireHorsLigneMessageDeCeContact();
        }
        for (Groupe groupe : groupes.values()) {
            groupe.LireHorsLigneMessageDeCeContact();
        }
    }
    
    public static class Contact implements Serializable {

        public String Id;
        public String nom;
        public ArrayList<MessageItem> DiscusEnLigne;
        public ArrayList<MessageItem> DiscusHorsLigne;

        public Contact(String Id, String name) {
            this.Id = Id;
            this.nom = name;
            this.DiscusEnLigne = new ArrayList();
            this.DiscusHorsLigne = new ArrayList();
        }

        public void AjouteMessageEnLigne(String senderId, String recieverId,String message) {
            DiscusEnLigne.add(new MessageItem(senderId, recieverId, message));
        }

        public void AjouteMessageHorsLigne(String senderId, String recieverId,String message) {
            DiscusHorsLigne.add(new MessageItem(senderId, recieverId, message));
        }

        public void LireHorsLigneMessageDeCeContact() {
            for (MessageItem mI : DiscusHorsLigne) {
                AjouteMessageEnLigne(mI.senderId, mI.recieverId,mI.message);
            }
            DiscusHorsLigne = new ArrayList();
        }

        public class MessageItem implements Serializable {

            public String senderId;
            public String recieverId;
            public String message;

            public MessageItem(String senderId, String recievedId, String message) {
                this.senderId = senderId;
                this.recieverId = recievedId;
                this.message = message;
            }
        }

    }
    
    public static class Groupe implements Serializable {

        public String IDGroupe;
        public String NomDuGroupe;
        public String[] IdGroupeMembres;
        public ArrayList<MessageItem> DiscusEnLigne;
        public ArrayList<MessageItem> DiscusHorsLigne;

        public Groupe(String Id, String name,String[]groupMembersId) {
            this.IDGroupe = Id;
            this.NomDuGroupe = name;
            this.IdGroupeMembres=groupMembersId;
            this.DiscusEnLigne = new ArrayList();
            this.DiscusHorsLigne = new ArrayList();
        }

        public void AjouteMessageEnLigne(String senderId, String recieverId, String message) {
            DiscusEnLigne.add(new MessageItem(senderId, recieverId, message));
        }

        public void AjouteMessageHorsLigne(String senderId, String recieverId,String message) {
            DiscusHorsLigne.add(new MessageItem(senderId, recieverId, message));
        }

        public void LireHorsLigneMessageDeCeContact() {
            for (MessageItem mI : DiscusHorsLigne) {
                AjouteMessageEnLigne(mI.senderId, mI.recieverId,mI.message);
            }
            DiscusHorsLigne = new ArrayList();
        }

        public class MessageItem implements Serializable {

            public String senderId;
            public String recieverId;
            public String message;

            public MessageItem(String senderId, String recievedId, String message) {
                this.senderId = senderId;
                this.recieverId = recievedId;
                this.message = message;
            }
        }
    }
}
