package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket clientSocket;

    private Scanner inMessage;
    Thread listeningThread;

    private PrintWriter outMessage;

    private String nickName = "";

    Scanner scInput;


    public void main() {
        scInput = new Scanner(System.in);

        mainMenu();
        scInput.close();
    }

    private void mainMenu() {
        short answer;
        do {
            answer = MyMenu.choose("Главное меню:", new String[]{"Выйти из приложения", "Подключиться к серверу"});
            switch (answer) {
                case 1 :beInChat();
            }
        } while (answer != 0);
        System.out.println("До свидания!");
    }

    private void beInChat() {
        setupNickname();

        try {
            connectToServer();
        } catch (IOException e) {
            System.out.println("Не удалось войти в чат");
            return;
        }


        listeningThread = new Thread(() -> {
            try {
                while (!listeningThread.isInterrupted()) {
                    if (inMessage.hasNext()) {
                        String inMes = inMessage.nextLine();
                        if (inMes.equals("###End###")) {
                            System.out.println("Нажмите Enter, чтобы выйти из чата");
                            break;
                        }
                        System.out.println("\t\t--- " + inMes);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Не удалось слушать чат");
            }
        });
        listeningThread.start();

        System.out.println("Введите \"###\", чтобы выйти из чата");
        sendOnlyMessage(nickName + " вошёл в чат!");
        String message;
        boolean remainInChat = true;
        do {
            message = scInput.nextLine();
            if (listeningThread.isAlive()) {
                if (message.equals("###")) {
                    remainInChat = false;
                    leaveChat();
                } else {
                    sendMessage(message);
                }
            } else {
                remainInChat = false;
            }
        } while (remainInChat);
    }

    private void setupNickname() {
        nickName = MyMenu.getAnythingFromConsole("Введите свой ник: ", "^(\\w|[а-яА-Я]){4,10}$", "Требуется 4-10 символов (латиница, кириллица, цифры, _)");
    }

    private void connectToServer() throws IOException{
        String server_host = MyMenu.getAnythingFromConsole("IP сервера: ", "([0-9]{1,3}\\.){3}[0-9]{1,3}", "Например: 127.0.0.1");
        int server_port = Integer.parseInt(MyMenu.getAnythingFromConsole("Порт сервера: ", "\\d{1,5}", "Например: 8080"));

        System.out.println("Пытаемся подключиться к серверу " + server_host + ":" + server_port + " ...");

        clientSocket = new Socket(server_host, server_port);
        inMessage = new Scanner(clientSocket.getInputStream());
        outMessage = new PrintWriter(clientSocket.getOutputStream());

        System.out.println("Успешно подключено");
    }


    private void sendMessage(String message) {

        String messageStr = nickName + ": " + message;

        outMessage.println(messageStr);
        outMessage.flush();
    }

    private void sendOnlyMessage(String message) {
        outMessage.println(message);
        outMessage.flush();
    }

    private void leaveChat() {
        try {
            outMessage.println(nickName + " вышел из чата!");
            sendOnlyMessage("###End###");

            outMessage.close();
            listeningThread.interrupt();
            listeningThread.join();
            inMessage.close();
            clientSocket.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
