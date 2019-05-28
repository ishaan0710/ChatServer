import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    ArrayList socketList = new ArrayList();
    ArrayList users = new ArrayList();
    ServerSocket serverSocket;
    Socket socket;

    public final static int PORT=999;
    public final static String LOGOUT_MESSAGE="@@logoutme@@:";
    public final static String UPDATE_USERS="updateuserslist";

    Server() {
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server Started at port " + PORT );

            while(true) {
                socket = serverSocket.accept();
                Thread thread = new GetSocket(socket, socketList, users);
                thread.start();

            }
        } catch(IOException e) {
            System.err.println(e);
        }
    }

    public static void main (String[] args) {
        new Server();
    }
}

