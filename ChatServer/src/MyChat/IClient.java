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
public interface IClient extends Remote {
    public void Notify(Utilisateur user ,String senderId) throws RemoteException;
}
