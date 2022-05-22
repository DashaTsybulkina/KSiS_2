package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ManagerOfClient extends Thread {
    private Server server;
    private PrintWriter outMessage;
    private Scanner inMessage;
    Socket socket;

    public ManagerOfClient(Socket socket, Server server) {
        try {
            this.socket = socket;
            this.server = server;
            this.outMessage = new PrintWriter(socket.getOutputStream());
            this.inMessage = new Scanner(socket.getInputStream());
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!server.isInterrupted()) {
            while (inMessage.hasNext()) {
                String clientMessage = inMessage.nextLine();
                if (clientMessage.equals("###End###")) {
                    break;
                }
                server.sendMessageMulticast(clientMessage);
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {}
        }
    }

    public void sendMessage(String msg) {
        try {
            outMessage.println(msg);
            outMessage.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void closeConnection() {
        server.removeClient(this);
        interrupt();
        try {
            socket.shutdownOutput();
            socket.shutdownInput();
        } catch (IOException e) {
            e.printStackTrace();
        }
        outMessage.close();
        inMessage.close();
    }
}
