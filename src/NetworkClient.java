import java.net.*;
import java.io.*;

public class NetworkClient {

    public static void send(String serverName, int port, String message) {
        
        try {
            Socket client = new Socket(serverName, port);
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF("Hello World");
            client.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        
        int port = Integer.parseInt(args[0]);
        String localhost = "0.0.0.0";
        String message = "hello world";

        NetworkClient.send(localhost, port, message);
    }

}
