/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyChat;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Jihad Nasser
 */
public interface IServeur extends Remote {
    public Utilisateur connect(String id, IClient client) throws RemoteException;
    
    public void LireHorsLigneMessageDeTousLesContacts(String id) throws RemoteException;

    public void disconnect(String id) throws RemoteException;

    public String CreateClient(String name, String pass) throws RemoteException;

    public String IdIfValidPass(String id, String pass) throws RemoteException;
    
    public Utilisateur AddContact(String myId, String contactId) throws RemoteException;
    
    public Utilisateur EnvoieMessage(String senderId, String recieverId, String message) throws RemoteException;

    public Utilisateur AddGroupe(String groupName, String[] memberIds) throws RemoteException;

    public Utilisateur EnvoieGroupeMessage(String groupId, String senderId, String[] memberIds,  String message) throws RemoteException;

    public Utilisateur EnvoieBroadCast(String senderId, String[] recieversIds, String message) throws RemoteException;
}
