package com.dama.keyboardbase;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;

class JavaServer {
    String fromClient;
    String toClient;

//    ServerSocket server;
    private DatagramSocket server;
    private byte[] buffer;

    JavaServer() throws SocketException, UnknownHostException {
        server = new DatagramSocket(8080);
        buffer = new byte[1024];
    }
    public void run_method() throws IOException {
        boolean run = true;
        Log.d("poszlo", "xD przed");

        while (run) {
            try {
                Log.d("poszlo", "xD po");
                Log.d("poszlo", server.getInetAddress().getLocalHost().getHostAddress());

                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                Log.d("poszlo", "pakiet" + packet);
                server.receive(packet);
                Log.d("poszlo", "przetworzone");
                String message = new String(packet.getData(), 0, packet.getLength());
//                System.out.println("Received message: " + message);
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                Log.d("poszlo", "1");
                Log.d("poszlo", message);

//                if (message.equals("1")) {
//                } else if (message.equals("2")) {
//                    System.out.println("Right");
//                } else if (message.equals("3")) {
//                    System.out.println("OK");
//                }

                // Send a response back to the client
                byte[] response = "Response from server".getBytes();
                Log.d("poszlo", "odpowiedz");

                DatagramPacket responsePacket = new DatagramPacket(response, response.length, clientAddress, clientPort);
                Log.d("poszlo", "przeslanie");
                server.send(responsePacket);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("poszlo", "wyjatek");
            }
        }
    }
}

