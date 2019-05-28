import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements ActionListener {
    Socket socket;
    DataInputStream inputStream;
    DataOutputStream outputStream;

    JButton sendButton, logoutButton, loginButton, exitButton;
    JFrame chatWindow;
    JTextArea txtBroadcast;
    JTextArea txtMessage;
    JList userList;

    public Client() {
        displayGUI();
    }

    public void displayGUI() {
        chatWindow = new JFrame();
        txtBroadcast = new JTextArea(5,30);
        txtBroadcast.setEditable(false);
        txtMessage = new JTextArea(2,20);
        userList = new JList();

        sendButton = new JButton("Send");
        loginButton = new JButton("Log in");
        logoutButton = new JButton("Log out");
        exitButton = new JButton("Exit");

        JPanel topHeading = new JPanel();
        topHeading.setLayout(new BorderLayout());
        topHeading.add(new JLabel("BroadCast messages from all online users", JLabel.CENTER),"North");
        topHeading.add(new JScrollPane(txtBroadcast),"Center");

        JPanel sendPanel = new JPanel();
        sendPanel.setLayout(new FlowLayout());
        sendPanel.add(new JScrollPane(txtMessage));
        sendPanel.add(sendButton);

        JPanel logPanel = new JPanel();
        logPanel.setLayout(new FlowLayout());
        logPanel.add(loginButton);
        logPanel.add(logoutButton);
        logPanel.add(exitButton);

        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridLayout(2,1));
        southPanel.add(sendPanel);
        southPanel.add(logPanel);

        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BorderLayout());
        userPanel.add(new JLabel("Online Users",JLabel.CENTER),"East");
        userPanel.add(new JScrollPane(userList),"South");

        chatWindow.add(userPanel,"East");
        chatWindow.add(topHeading,"Center");
        chatWindow.add(southPanel,"South");

        chatWindow.pack();
        chatWindow.setTitle("Login for chat");
        chatWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        chatWindow.setVisible(true);
        sendButton.addActionListener(this);
        logoutButton.addActionListener(this);
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);

        txtMessage.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtMessage.selectAll();
            }
        });

        chatWindow.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(socket!=null) {
                    JOptionPane.showMessageDialog(chatWindow, "You are logged out right now", "Exit", JOptionPane.INFORMATION_MESSAGE);
                    logoutSession();
                }
                System.exit(0);
            }
        });


    }

    private void logoutSession() {
        if(socket==null)
            return;
        try {
            outputStream.writeUTF(Server.LOGOUT_MESSAGE);
            Thread.sleep(500);
            socket=null;
            loginButton.setEnabled(true);
            logoutButton.setEnabled(false);
        } catch (IOException | InterruptedException e) {
            txtBroadcast.append("\n inside logoutMethod " + e);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton)e.getSource();
        if(button == sendButton) {
            if(socket==null) {
                JOptionPane.showMessageDialog(chatWindow, "Please login first");
                return;
            }
            try {
                outputStream.writeUTF(txtMessage.getText());
                txtMessage.setText("");
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
        if(button == loginButton) {
            String userName = JOptionPane.showInputDialog(chatWindow,"Enter your nick name");
            if(userName!=null)
                clientChat(userName);
        }
        if(button == logoutButton) {
            if(socket!=null) {
                logoutSession();
            }
        }
        if(button == exitButton) {
            if(socket!=null) {
                JOptionPane.showMessageDialog(chatWindow,"You are logged out", "Exit", JOptionPane.INFORMATION_MESSAGE);
                logoutSession();
            }
            System.exit(0);
        }

    }

    private void clientChat(String userName) {
        try {
            socket = new Socket(InetAddress.getLocalHost(), Server.PORT);
            inputStream = new DataInputStream(socket.getInputStream());
            outputStream = new DataOutputStream(socket.getOutputStream());
            ClientThread clientThread = new ClientThread(inputStream, this);
            clientThread.start();
            outputStream.writeUTF(userName);
            chatWindow.setTitle(userName + " Chat Window");
        } catch (IOException e) {
            System.err.println(e);
        }
        logoutButton.setEnabled(true);
        loginButton.setEnabled(false);
    }

    public static void main(String[] args) {
        new Client();
    }
}
