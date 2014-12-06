package com.forbes.dms;

import java.net.*;
import java.io.*;

public class NetworkServer extends Thread {
    
    private ServerSocket serverSocket;

    public NetworkServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        serverSocket.setSoTimeout(10000);
    }

    public void run() {
        
        while(true) {
            try {
                Socket server = serverSocket.accept();
                DataInputStream in = new DataInputStream(server.getInputStream());
                System.out.println(in.readUTF());
                server.close();
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        
        int port = Integer.parseInt(args[0]);
        try {
            Thread t = new NetworkServer(port);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
