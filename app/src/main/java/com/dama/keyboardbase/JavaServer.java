package com.dama.keyboardbase;

import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;

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
    String SERVER_IP = "172.16.69.196";
    int SERVERPORT = 12345;
    int CLIENTPORT = 3000;

//    ServerSocket server;
    private DatagramSocket server;
    private DatagramPacket packet;
    private byte[] buffer;
    private KeyboardImeService keyboard;

    JavaServer(KeyboardImeService keyboardImeService) throws SocketException, UnknownHostException {
        keyboard = keyboardImeService;
    }
    public synchronized void run_method() throws IOException {


        boolean run = true;
        Log.d("poszlo", "xD przed");

//        server = new DatagramSocket(SERVERPORT, InetAddress.getByName(SERVER_IP));
        server = new DatagramSocket(CLIENTPORT);
        buffer = new byte[2];
        packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(SERVER_IP), SERVERPORT);

        while (run) {
            try {
                server.receive(packet);
                String message = new String(packet.getData(), 0, packet.getLength());
                InetAddress clientAddress = packet.getAddress();
                int clientPort = packet.getPort();
                if (message.equals("1")) {
                    keyboard.onKeyDown(KeyEvent.KEYCODE_DPAD_LEFT, new KeyEvent(1 , 1));
                }
                else if (message.equals("2")) {
                    keyboard.onKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT, new KeyEvent(1 , 1));
                }
                if (message.equals("3")) {
                    keyboard.onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, new KeyEvent(1 , 1));
                }
                byte[] response = "Response from server".getBytes();

                DatagramPacket responsePacket = new DatagramPacket(response, response.length, clientAddress, clientPort);
                server.send(responsePacket);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("poszlo", "wyjatek");
            }
        }
    }
}

