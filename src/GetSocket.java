import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class GetSocket extends Thread {
    private Socket socket;
    private ArrayList socketList;
    private ArrayList users;
    private String userName;
    private static final String delimiter = " ***** ";

    GetSocket(Socket socket, ArrayList socketList, ArrayList users) {
        this.socket = socket;
        this.socketList = socketList;
        this.users = users;

        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            userName = inputStream.readUTF();
            socketList.add(socket);
            users.add(userName);
            broadcastMessage(delimiter + userName + " Logged in at " + (new Date()) + delimiter);
            sendNewUserList();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void run() {
        String chatReceived;
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            while(true) {
                chatReceived = inputStream.readUTF();
                if (chatReceived.toLowerCase().equals(Server.LOGOUT_MESSAGE))
                    break;
                broadcastMessage(userName + " said: " + chatReceived);
            }
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            outputStream.writeUTF(Server.LOGOUT_MESSAGE);
            outputStream.flush();
            users.remove(userName);
            broadcastMessage(delimiter + userName + " Logged out at " + new Date() + delimiter);
            socketList.remove(socket);
            socket.close();

        } catch (IOException e) {
            System.err.println(e);
        }
    }

    public void sendNewUserList() {
        broadcastMessage(Server.UPDATE_USERS + users.toString());
    }

    public void broadcastMessage(String message) {
        Iterator<Socket> iterator = socketList.iterator();
        while(iterator.hasNext()) {
            try {
                Socket tempSock = iterator.next();
                DataOutputStream outputStream = new DataOutputStream(tempSock.getOutputStream());
                outputStream.writeUTF(message);
                outputStream.flush();
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }
}
