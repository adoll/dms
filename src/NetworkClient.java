import java.net.*;
import java.io.*;

public class NetworkClient {

    public static void send(String serverName, int port, String message) {
        
        try {
            Socket client = new Socket(serverName, port);
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(message);

            InputStream inFromServer = client.getInputStream();
            DataInputStream in = new DataInputStream(inFromServer);
            System.out.println(in.readUTF());
            client.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        
        int port = Integer.parseInt(args[0]);
        String localhost = "104.131.19.136";
        String message = "hello world";

        NetworkClient.send(localhost, port, message);
    }

}
