/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MyChat;

import java.awt.Color;
import java.awt.Font;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import MyChat.Utilisateur.Contact;
import MyChat.Utilisateur.Groupe;
import javax.swing.BoxLayout;
import javax.swing.border.Border;
import javax.swing.border.SoftBevelBorder;

/**
 *
 * @author Salah
 */
public class ClientGui extends javax.swing.JFrame {

    /**
     * Creates new form ClientGui
     */
    public String Id;
    IServeur serverRef;
    ClientImp client;
    String username;
    public Utilisateur me;
    public ArrayList<String> UtilisateurSpeciale = new ArrayList();
    Border brd = new SoftBevelBorder(2);

    public ClientGui(String Id) throws NotBoundException, MalformedURLException {
        this.Id = Id;
        initComponents();
        setVisible(true);

        try {
            serverRef = (IServeur) Naming.lookup(ClientImp.RMIRef);
            client = new ClientImp();
            me = serverRef.connect(Id, client);
            username = me.nom;
            for (Contact con : me.contacts.values()) {
                if (!con.DiscusHorsLigne.isEmpty()) {
                    UtilisateurSpeciale.add(con.Id);
                }
            }
            for (Groupe gp : me.groupes.values()) {
                if (!gp.DiscusHorsLigne.isEmpty()) {
                    UtilisateurSpeciale.add(gp.IDGroupe);
                }
            }
            Refresh();
            me.LireHorsLigneMessageDeTousLesContactsEtLesGroupes();
            serverRef.LireHorsLigneMessageDeTousLesContacts(Id);

        } catch (RemoteException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }

        MonIdLabel.setText(Id);
        MonUserNameLabel.setText(username);

    }

    ArrayList<JButton> contactsBtn = new ArrayList();
    int i = -1;

    public void Refresh() {

        while (i >= 0) {
            this.remove(contactsBtn.get(i));
            contactsBtn.remove(i);
            this.Jpanel.validate();
            this.validate();
            i--;
        }
        for (Contact con : me.contacts.values()) {
            i++;
            JButton contactBtn = new JButton(con.nom);
            contactBtn.setFont(new java.awt.Font("Times New Roman", 0, 20));
            contactBtn.setBounds(5, 120 + 60 * i, 230, 60);
            contactBtn.setBackground(Color.GREEN);
            contactBtn.setBorder(brd);
            contactBtn.setBorderPainted(true);
            contactBtn.setName(con.Id.trim());
            int nbOfmessage = 0;
            if (UtilisateurSpeciale.contains(con.Id.trim()) && !recieverId.equals(con.Id.trim())) {
                contactBtn.setFont(new Font("Times New Roman", 1, 20));
                String[] name = contactBtn.getText().split("\\(");
                if (name.length == 1) {
                    if (con.DiscusHorsLigne.size() != 0) {
                        nbOfmessage = con.DiscusHorsLigne.size();
                        contactBtn.setText(name[0] + "(" + nbOfmessage + ")");
                    }
                }
            }
            contactBtn.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    chatBtnActionPerformed(evt);
                }
            });
            
            Jpanel.add(contactBtn);
            this.validate();
            this.Jpanel.validate();
            this.pack();
            contactsBtn.add(contactBtn);
        }

        for (Groupe gp : me.groupes.values()) {
            i++;
            JButton contactBtn = new JButton(gp.NomDuGroupe);
            contactBtn.setFont(new java.awt.Font("Times New Roman", 0, 20));
            contactBtn.setBounds(5, 120 + 60 * i, 230, 60);
            contactBtn.setBackground(Color.WHITE);
            contactBtn.setBorder(brd);
            contactBtn.setBorderPainted(true);
            contactBtn.setName(gp.IDGroupe.trim());
            int nbOfmessage = 0;
            if (UtilisateurSpeciale.contains(gp.IDGroupe.trim()) && !recieverId.equals(gp.IDGroupe.trim())) {
                contactBtn.setFont(new Font("Times New Roman", 1, 20));
                String[] name = contactBtn.getText().split("\\(");
                if (name.length == 1) {
                    if (gp.DiscusHorsLigne.size() != 0) {
                        nbOfmessage = gp.DiscusHorsLigne.size();
                        contactBtn.setText(name[0] + "(" + nbOfmessage + ")"); // pour montrer le nomber de message non lus par l'utilisateur
                    }
                }
            }
            contactBtn.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    chatBtnActionPerformed(evt);
                }
            });
            Jpanel.add(contactBtn);
            this.pack();
            this.Jpanel.validate();
            this.validate();
            contactsBtn.add(contactBtn);
        }

        if (!recieverId.isEmpty()) {
            chatPnl.removeAll();
            String contactId = recieverId;
            recieverId = contactId;
            if (recieverId.contains("cl")) {          
                ArrayList<Contact.MessageItem> chat = me.contacts.get(contactId).DiscusEnLigne;
                int i = -1;
                for (Contact.MessageItem msg : chat) {
                    i++;
                    String message = msg.message;
                    int messageLn = message.length() * 12;
                    JLabel messageLbl;
                    if (msg.senderId.equals(Id)) {
                        chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                        messageLbl = new JLabel(message,JLabel.RIGHT);
                        messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                        messageLbl.setOpaque(true);
                        messageLbl.setBounds(chatPnl.getWidth() - messageLn - 5, 5 + 45 * i, messageLn, 30);
                        messageLbl.setHorizontalAlignment(SwingConstants.RIGHT);
                        messageLbl.setBackground(Color.GREEN);
                        messageLbl.setBorder(brd);

                    } else {
                        chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                        messageLbl = new JLabel(message,JLabel.LEFT);
                        messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                        messageLbl.setOpaque(true);
                        messageLbl.setBounds(5, 5 + 45 * i, messageLn, 30);
                        messageLbl.setHorizontalAlignment(SwingConstants.LEFT);
                        messageLbl.setBackground(Color.GRAY);
                        messageLbl.setBorder(brd);
                    }
                    chatPnl.add(messageLbl);
                    this.pack();
                }
                
            } else {
                ArrayList<Groupe.MessageItem> chat = me.groupes.get(contactId).DiscusEnLigne;
                int i = -1;
                for (Groupe.MessageItem msg : chat) {
                    i++;
                    String message = msg.message;
                    int messageLn = message.length() * 12;
                    String senderName;
                    if (msg.senderId.equals(me.Id)) {
                        senderName = "me";
                    } else {
                        Contact con = me.contacts.get(msg.senderId.trim());
                        if (con != null) {
                            senderName = con.nom.trim();
                        } else {
                            senderName = msg.senderId.trim();
                        }

                    }
                    int senderNameLn = senderName.length() * 12;
                    JLabel senderLbl = new JLabel();
                    senderLbl.setFont(new Font(senderLbl.getFont().getName(), Font.PLAIN, 12));
                    JLabel messageLbl;
                    senderLbl.setOpaque(true);
                    senderLbl.setText(senderName);
                    if (msg.senderId.equals(Id)) {
                        chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                        messageLbl = new JLabel(message,JLabel.RIGHT);
                        messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                        messageLbl.setOpaque(true);
                        senderLbl.setBounds(chatPnl.getWidth() - messageLn - 5, 5 + 55 * i, senderNameLn, 10);
                        messageLbl.setBounds(chatPnl.getWidth() - messageLn - 5, 5 + 10 + 55 * i, messageLn, 30);
                        messageLbl.setHorizontalAlignment(SwingConstants.RIGHT);
                        senderLbl.setBackground(Color.GREEN);
                        messageLbl.setBackground(Color.GREEN);
                        messageLbl.setBorder(brd);

                    } else {
                        chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                        messageLbl = new JLabel(message,JLabel.LEFT);
                        messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                        messageLbl.setOpaque(true);
                        senderLbl.setBounds(5, 5 + 55 * i, senderNameLn, 10);
                        messageLbl.setBounds(5, 5 + 10 + 55 * i, messageLn, 30);
                        messageLbl.setHorizontalAlignment(SwingConstants.LEFT);
                        senderLbl.setBackground(Color.GRAY);
                        messageLbl.setBackground(Color.GRAY);
                        messageLbl.setBorder(brd);
                    }
                    chatPnl.add(senderLbl);
                    chatPnl.add(messageLbl);
                    this.pack();
                }
                
            }

            if (UtilisateurSpeciale.contains(contactId)) {
                UtilisateurSpeciale.remove(contactId);
            }
        }
        this.repaint();
        this.Jpanel.repaint();

    }
    String recieverId = "";

    private void chatBtnActionPerformed(java.awt.event.ActionEvent evt) {
        chatPnl.removeAll();
        EnvoieBtn.setEnabled(true);
        JButton clickedBtn = (JButton) evt.getSource();
        clickedBtn.setFont(new Font("Times New Roman", 0, 20));
        String contactId = clickedBtn.getName().trim();
        recieverId = contactId;
        if (recieverId.contains("cl")) {
            ArrayList<Contact.MessageItem> chat = me.contacts.get(contactId).DiscusEnLigne;

            int i = -1;
            for (Contact.MessageItem msg : chat) {
                i++;
                String message = msg.message;
                int messageLn = message.length() * 12;
                JLabel messageLbl;
                if (msg.senderId.equals(Id)) {
                    chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                    messageLbl = new JLabel(message,JLabel.RIGHT);
                    messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                    messageLbl.setOpaque(true);
                    messageLbl.setBounds(chatPnl.getWidth() - messageLn - 5, 5 + 45 * i, messageLn, 30);
                    messageLbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    messageLbl.setBackground(Color.GREEN);
                    messageLbl.setBorder(brd);

                } else {
                    chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                    messageLbl = new JLabel(message,JLabel.LEFT);
                    messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                    messageLbl.setOpaque(true);
                    messageLbl.setBounds(5, 5 + 45 * i, messageLn, 30);
                    messageLbl.setHorizontalAlignment(SwingConstants.LEFT);
                    messageLbl.setBackground(Color.GRAY);
                    messageLbl.setBorder(brd);
                }
                chatPnl.add(messageLbl);
                this.pack();
            }
        this.repaint();

            if (UtilisateurSpeciale.contains(contactId)) {
                UtilisateurSpeciale.remove(contactId);
            }
            clickedBtn.setText(me.contacts.get(contactId).nom);
        } else {         
            ArrayList<Groupe.MessageItem> chat = me.groupes.get(contactId).DiscusEnLigne;

            int i = -1;
            for (Groupe.MessageItem msg : chat) {
                i++;
                String senderName;
                if (msg.senderId.equals(me.Id)) {
                    senderName = "me";
                } else {
                    Contact con = me.contacts.get(msg.senderId.trim());
                    if (con != null) {
                        senderName = con.nom.trim();
                    } else {
                        senderName = msg.senderId.trim();
                    }
                    if (con != null) {
                        senderName = con.nom.trim();
                    } else {
                        senderName = msg.senderId.trim();
                    }
                }
                int senderNameLn = senderName.length() * 12;
                String message = msg.message;
                int messageLn = message.length() * 12;
                JLabel senderLbl = new JLabel();
                senderLbl.setFont(new Font(senderLbl.getFont().getName(), Font.PLAIN, 12));
                JLabel messageLbl;
                senderLbl.setOpaque(true);
                senderLbl.setText(senderName);
                if (msg.senderId.equals(Id)) {
                    chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                    messageLbl = new JLabel(message,JLabel.RIGHT);
                    messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                    messageLbl.setOpaque(true);
                    senderLbl.setBounds(chatPnl.getWidth() - messageLn - 5, 5 + 55 * i, senderNameLn, 10);
                    messageLbl.setBounds(chatPnl.getWidth() - messageLn - 5, 5 + 10 + 55 * i, messageLn, 30);
                    messageLbl.setHorizontalAlignment(SwingConstants.RIGHT);
                    senderLbl.setBackground(Color.GREEN);
                    messageLbl.setBackground(Color.GREEN);
                    messageLbl.setBorder(brd);

                } else {
                    chatPnl.setLayout(new BoxLayout(chatPnl, BoxLayout.Y_AXIS));
                    messageLbl = new JLabel(message,JLabel.LEFT);
                    messageLbl.setFont(new Font(messageLbl.getFont().getName(), Font.PLAIN, 18));
                    messageLbl.setOpaque(true);
                    senderLbl.setBounds(5, 5 + 55 * i, senderNameLn, 10);
                    messageLbl.setBounds(5, 5 + 10 + 55 * i, messageLn, 30);
                    messageLbl.setHorizontalAlignment(SwingConstants.LEFT);
                    senderLbl.setBackground(Color.GRAY);
                    messageLbl.setBackground(Color.GRAY);
                    messageLbl.setBorder(brd);
                }
                chatPnl.add(senderLbl);
                chatPnl.add(messageLbl);
                this.pack();
            }
            this.repaint();
            this.Jpanel.repaint();

            if (UtilisateurSpeciale.contains(contactId)) {
                UtilisateurSpeciale.remove(contactId);
            }
                
            clickedBtn.setText(me.groupes.get(contactId).NomDuGroupe);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Jpanel = new javax.swing.JPanel();
        MonIdLabel = new javax.swing.JLabel();
        MonUserNameLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        ContactIdText = new javax.swing.JTextField();
        AjouterUnContactBtn = new javax.swing.JButton();
        MessageText = new javax.swing.JTextField();
        EnvoieBtn = new javax.swing.JButton();
        jPanel = new javax.swing.JScrollPane();
        chatPnl = new javax.swing.JPanel();
        refreshBtn = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        addGrpBtn = new javax.swing.JMenu();
        bCastBtn = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("WhasApp");
        setBackground(new java.awt.Color(255, 0, 0));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        Jpanel.setBackground(new java.awt.Color(0, 51, 51));
        Jpanel.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        Jpanel.setAutoscrolls(true);

        MonIdLabel.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        MonIdLabel.setForeground(new java.awt.Color(51, 0, 255));
        MonIdLabel.setText("Mon ID");

        MonUserNameLabel.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        MonUserNameLabel.setForeground(new java.awt.Color(51, 0, 255));
        MonUserNameLabel.setText("Mon UserName");

        jLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 0, 255));
        jLabel1.setText("ID                      :");

        jLabel2.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 0, 255));
        jLabel2.setText("Mon UserName:");

        ContactIdText.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N

        AjouterUnContactBtn.setFont(new java.awt.Font("Agency FB", 1, 18)); // NOI18N
        AjouterUnContactBtn.setForeground(new java.awt.Color(51, 0, 255));
        AjouterUnContactBtn.setText("Ajouter Un Contact");
        AjouterUnContactBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        AjouterUnContactBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AjouterUnContactBtnActionPerformed(evt);
            }
        });

        MessageText.setFont(new java.awt.Font("sansserif", 0, 18)); // NOI18N
        MessageText.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 0, 0)));

        EnvoieBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/MyChat/SendButton.jpg"))); // NOI18N
        EnvoieBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        EnvoieBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EnvoieBtnActionPerformed(evt);
            }
        });

        jPanel.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jPanel.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jPanel.setAutoscrolls(true);

        chatPnl.setBackground(new java.awt.Color(0, 102, 102));
        chatPnl.setAutoscrolls(true);

        javax.swing.GroupLayout chatPnlLayout = new javax.swing.GroupLayout(chatPnl);
        chatPnl.setLayout(chatPnlLayout);
        chatPnlLayout.setHorizontalGroup(
            chatPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 734, Short.MAX_VALUE)
        );
        chatPnlLayout.setVerticalGroup(
            chatPnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 449, Short.MAX_VALUE)
        );

        jPanel.setViewportView(chatPnl);

        refreshBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/MyChat/RefreshBtn.jpg"))); // NOI18N
        refreshBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout JpanelLayout = new javax.swing.GroupLayout(Jpanel);
        Jpanel.setLayout(JpanelLayout);
        JpanelLayout.setHorizontalGroup(
            JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(JpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(AjouterUnContactBtn, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .addComponent(ContactIdText))
                .addGap(44, 44, 44)
                .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(JpanelLayout.createSequentialGroup()
                        .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(60, 60, 60)
                        .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(MonUserNameLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(MonIdLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshBtn))
                    .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(JpanelLayout.createSequentialGroup()
                        .addComponent(MessageText, javax.swing.GroupLayout.PREFERRED_SIZE, 644, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(EnvoieBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(39, Short.MAX_VALUE))
        );
        JpanelLayout.setVerticalGroup(
            JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JpanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(JpanelLayout.createSequentialGroup()
                        .addComponent(ContactIdText, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(AjouterUnContactBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(MessageText, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(JpanelLayout.createSequentialGroup()
                        .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(refreshBtn, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, JpanelLayout.createSequentialGroup()
                                .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(MonIdLabel)
                                    .addComponent(jLabel1))
                                .addGap(18, 18, 18)
                                .addGroup(JpanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(MonUserNameLabel)
                                    .addComponent(jLabel2))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(EnvoieBtn)))
                .addGap(14, 14, 14))
        );

        addGrpBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addGrpBtn.setForeground(new java.awt.Color(0, 0, 255));
        addGrpBtn.setText("Ajouter Un Groupe");
        addGrpBtn.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        addGrpBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AjouterUnGrpBtnMouseClicked(evt);
            }
        });
        jMenuBar2.add(addGrpBtn);

        bCastBtn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        bCastBtn.setForeground(new java.awt.Color(0, 0, 255));
        bCastBtn.setText("BroadCast");
        bCastBtn.setFont(new java.awt.Font("Times New Roman", 1, 24)); // NOI18N
        bCastBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                bCastBtnMouseClicked(evt);
            }
        });
        jMenuBar2.add(bCastBtn);

        setJMenuBar(jMenuBar2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Jpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Jpanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            serverRef.disconnect(Id);

        } catch (RemoteException ex) {
            Logger.getLogger(ClientGui.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_formWindowClosing

    private void AjouterUnContactBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AjouterUnContactBtnActionPerformed
        String contactId = ContactIdText.getText().trim();
        try {
            Utilisateur user = serverRef.AddContact(Id, contactId);
            me = user;
        } catch (RemoteException ex) {
            Logger.getLogger(ClientGui.class
                    .getName()).log(Level.SEVERE, null, ex);
            ContactIdText.setText("Impossible!");
            System.out.println("Exception : " +ex.getMessage() );
        }
                Refresh();
                ContactIdText.setText("");
    }//GEN-LAST:event_AjouterUnContactBtnActionPerformed

    private void EnvoieBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EnvoieBtnActionPerformed
        // TODO add your handling code here:
        try {
            if (recieverId.contains("cl")) {
                me = serverRef.EnvoieMessage(Id, recieverId, MessageText.getText());
            } else {
                System.out.println(recieverId);
                me = serverRef.EnvoieGroupeMessage(recieverId.trim(), Id, me.groupes.get(recieverId).IdGroupeMembres, MessageText.getText());
            }
            Refresh();
            MessageText.setText("");

        } catch (RemoteException ex) {
            Logger.getLogger(ClientGui.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_EnvoieBtnActionPerformed

    private void AjouterUnGrpBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AjouterUnGrpBtnMouseClicked
        // TODO add your handling code here:
        GroupeForm gF = new GroupeForm(me, serverRef);
        this.dispose();
        gF.setVisible(true);
    }//GEN-LAST:event_AjouterUnGrpBtnMouseClicked

    private void bCastBtnMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bCastBtnMouseClicked
        // TODO add your handling code here:
        BroadCastForm bc = new BroadCastForm(me, serverRef);
        this.dispose();
        bc.setVisible(true);
    }//GEN-LAST:event_bCastBtnMouseClicked

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        // TODO add your handling code here:
        this.dispose();
        try {
            ClientGui cl = new ClientGui(Id);
        } catch (NotBoundException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedURLException ex) {
            Logger.getLogger(ClientGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_refreshBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);

        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientGui.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ClientGui("").setVisible(true);

                } catch (NotBoundException ex) {
                    Logger.getLogger(ClientGui.class
                            .getName()).log(Level.SEVERE, null, ex);

                } catch (MalformedURLException ex) {
                    Logger.getLogger(ClientGui.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton AjouterUnContactBtn;
    private javax.swing.JTextField ContactIdText;
    private javax.swing.JButton EnvoieBtn;
    private javax.swing.JPanel Jpanel;
    private javax.swing.JTextField MessageText;
    private javax.swing.JLabel MonIdLabel;
    private javax.swing.JLabel MonUserNameLabel;
    private javax.swing.JMenu addGrpBtn;
    private javax.swing.JMenu bCastBtn;
    private javax.swing.JPanel chatPnl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JScrollPane jPanel;
    private javax.swing.JButton refreshBtn;
    // End of variables declaration//GEN-END:variables
}
