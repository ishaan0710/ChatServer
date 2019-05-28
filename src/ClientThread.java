import javax.swing.text.BadLocationException;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientThread extends Thread {
    DataInputStream inputStream;
    Client client;

    ClientThread(DataInputStream inputStream, Client client) {
        this.inputStream = inputStream;
        this.client = client;
    }

    @Override
    public void run() {
        String message = "";
        while(true) {
            try {
                message = inputStream.readUTF();
                if(message.startsWith(Server.UPDATE_USERS))
                    updateUserList(message);
                else if(message.equals(Server.LOGOUT_MESSAGE))
                    break;
                else
                    client.txtBroadcast.append("\n" + message);
                int lineOffset = client.txtBroadcast.getLineStartOffset(client.txtBroadcast.getLineCount() - 1);
                client.txtBroadcast.setCaretPosition(lineOffset);
            } catch (IOException| BadLocationException e) {
                System.err.println(e);
            }
        }
    }

    private void updateUserList(String message) {
        Vector userList = new Vector();
        message = message.replace("[","");
        message = message.replace("]","");
        message = message.replace(Server.UPDATE_USERS,"");
        StringTokenizer tokenizer = new StringTokenizer(message,",");
        while(tokenizer.hasMoreTokens()) {
            userList.add(tokenizer.nextToken());
        }
        client.userList.setListData(userList);
    }
}
