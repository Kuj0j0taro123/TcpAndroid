package com.example.tcpandroid;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServerService extends Service {
    private static final int SERVER_PORT = 12345;
    private ServerSocket serverSocket;
    private boolean running = false;

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                startServer();
            }
        }).start();
    }

    private void startServer() {
        try {
            serverSocket = new ServerSocket(SERVER_PORT);
            Log.d("TcpServer", "Server started on port " + SERVER_PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept(); // Așteaptă conexiuni de la client
                handleClient(clientSocket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter output = new PrintWriter(clientSocket.getOutputStream(), true);

            String message = input.readLine();
            Log.d("TcpServer", "Received from client: " + message);

            // Trimite un răspuns înapoi clientului
            output.println("Hello from Android Server");

            input.close();
            output.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // Nu este necesar pentru acest serviciu
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
