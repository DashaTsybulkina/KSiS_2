package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Server extends Thread {

    public int Port;
    public String IP;
    public ServerSocket serverSocket;


    private final ArrayList<ManagerOfClient> clients = new ArrayList<ManagerOfClient>();

    @Override
    public void run() {
        Socket clientSocket;

        try {
            while (true) {

                clientSocket = serverSocket.accept();


                ManagerOfClient client = new ManagerOfClient(clientSocket, this);
                synchronized (clients) {
                    clients.add(client);
                }

                new Thread(client).start();
            }
        }
        catch (SocketException ignored) {}
        catch (IOException e) {
            e.printStackTrace();
            stopServer();
        }
    }


    public void sendMessageMulticast(String msg) {
        System.out.println("\t\t--- " + msg);

        synchronized (clients) {
            for (ManagerOfClient cl : clients) {
                cl.sendMessage(msg);
            }
        }
    }

    public void stopServer() {
        sendMessageMulticast("Сервер останавливается\n###End###");

        interrupt();
        System.out.println("Сервер больше не ищет новые подключения");

        try {
            synchronized (clients) {
                while (clients.size() != 0) {
                    clients.get(0).closeConnection();
                }
            }

            serverSocket.close();
        }
        catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
        System.out.println("Сервер остановлен");
    }


    public void removeClient(ManagerOfClient client) {
        synchronized (clients) {
            clients.remove(client);
        }
    }

}
