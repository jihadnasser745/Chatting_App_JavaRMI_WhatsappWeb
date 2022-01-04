/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyChat;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.JTextArea;

/**
 *
 * @author Jihad Nasser
 */
public class ClientImp extends UnicastRemoteObject implements IClient {
  
    public static String RMIRef = "rmi://127.0.0.1:2000/ServeurImp"; 

    JTextArea area;
    static ConnecteGui form;

    public ClientImp() throws RemoteException {

    }

    @Override
    public void Notify(Utilisateur user, String senderId) throws RemoteException {
        form.chat.me = user;
        if (!senderId.equals("0")) {
            form.chat.UtilisateurSpeciale.add(senderId);
        }
        form.chat.Refresh();
    }

    public static void main(String[] args) throws NotBoundException {

        form = new ConnecteGui();
    }
}
