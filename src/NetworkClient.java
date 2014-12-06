import java.net.*;
import java.io.*;

public class NetworkClient {

    public static void send(String serverName, int port, String message) {
        
        try {
            Socket client = new Socket(serverName, port);
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(message);
            client.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        
        int port = Integer.parseInt(args[0]);
        String localhost = "104.236.19.180";
        String message = "hello world";

        NetworkClient.send(localhost, port, message);
    }

}
