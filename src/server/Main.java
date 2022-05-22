package server;

import client.MyMenu;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scInput = new Scanner(System.in);

        Server server = new Server();
        Thread server_thread = new Thread(server);
        boolean isIncorrect = true;
        do {
            server.IP = MyMenu.getAnythingFromConsole("IP сервера: ",
                    "([0-9]{1,3}\\.){3}[0-9]{1,3}|localhost", "Например: 127.0.0.1 или localhost");
            server.Port = Integer.parseInt(MyMenu.getAnythingFromConsole("Порт сервера (0 для автовыбора): ",
                    "\\d{1,5}", "Например: 8080 или 0"));

            try {
                server.serverSocket = new ServerSocket(server.Port, 0, InetAddress.getByName(server.IP));
                isIncorrect = false;
            } catch (IOException e) {
                System.out.println("Ошибка. Выберите другие значения");
            }
        } while (isIncorrect);
        server_thread.start();

        System.out.println("Сервер запущен " + server.serverSocket.getInetAddress().toString() + ":"
                + server.serverSocket.getLocalPort());
        System.out.println("Введите \"###\" для остановки сервера");
        String mes;
        boolean serverShouldRun = true;
        do {
            mes = scInput.nextLine();
            if (mes.equals("###")) {
                serverShouldRun = false;
            } else {
                server.sendMessageMulticast("S: " + mes);
            }
        } while (serverShouldRun);

        server.stopServer();
        scInput.close();
    }
}