package client;

import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;
import static protocol.Errors.DUPLICATED_USER;
import static protocol.Protocol.NEW_LINE;
import static protocol.Protocol.SEPARATOR;
import static protocol.Protocol.ZERO;
import static protocol.Protocol.getLineFirstValue;
import static protocol.ProtocolMethods.CREATE_USER;
import static protocol.ProtocolMethods.GET_USERS;
import static protocol.ProtocolMethods.SEND_PRIVATE_MESSAGE;
import static protocol.ProtocolMethods.SEND_PUBLIC_MESSAGE;
import static protocol.ProtocolMethods.USER_CREATED;
import static protocol.ProtocolProperties.CONTENT;
import static protocol.ProtocolProperties.ERROR;
import static protocol.ProtocolProperties.SENDER;
import static protocol.ProtocolProperties.USERNAMES;
import static protocol.ProtocolValidator.isHeaderValid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import client.gui.ChatGUI;
import protocol.ParsedMessage;
import protocol.Protocol;

public class Client implements Runnable {

    private BufferedReader in;
    private PrintWriter out;
    private ChatGUI gui;

    public static void main(String[] args) {
        Client client = new Client();
        client.setup();
    }

    @Override
    public void run() {
        waitForMessages();
    }

    private void setup() {
        try {
            Socket socket = new Socket("localhost", 8084);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            Thread t = new Thread(this);
            t.start();

            gui = new ChatGUI(this);

            gui.inputUsername("Digite seu nome:");
            gui.render();
            
            sendMessage(GET_USERS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void waitForMessages() {
        try {
            while (true) {
                messageReceived(in.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void messageReceived(String header) throws Exception {
        if (header == null || header.isBlank()) {
            return;
        }

        if (isHeaderValid(header)) {
            StringBuilder message = new StringBuilder(header);

            int size = parseInt(getLineFirstValue(header));

            for (int i = 1; i <= size; i++) {
                message.append("\n").append(in.readLine());
            }

            processMessage(message.toString());
        }
    }

    private void processMessage(String message) throws Exception {
        System.out.printf("\nMessage received: \n%s\n", message);

        ParsedMessage parsedMessage = Protocol.parseMessage(message);

        switch (parsedMessage.getMethod()) {
            case CREATE_USER:
                handleCreateUserResponse(parsedMessage);
                break;

            case GET_USERS:
                handleGetUsersResponse(parsedMessage);
                break;

            case USER_CREATED:
                handleUserCreated();

            case SEND_PRIVATE_MESSAGE:
                handlePrivateMessageResponse(parsedMessage);
                break;

            case SEND_PUBLIC_MESSAGE:
                handlePublicMessageResponse(parsedMessage);
                break;
        }
    }

    private void handleCreateUserResponse(ParsedMessage message) {
        if (DUPLICATED_USER.equals(message.getPropertyNullable(ERROR))) {
            gui.inputUsername("Usuário já cadastrado! Digite outro nome:");
        } else {
            gui.setUserCreated(true);
        }
    }

    private void handleGetUsersResponse(ParsedMessage message) throws Exception {
        String usernames = message.getProperty(USERNAMES);
        List<String> newUsers = new ArrayList<>(asList(usernames.split(",")));

        gui.setUsers(newUsers);
    }

    private void handleUserCreated() {
        sendMessage(GET_USERS);
    }

    private void handlePrivateMessageResponse(ParsedMessage message) throws Exception {
        if (message.getSize() != 0) {
            String sender = message.getProperty(SENDER);
            String content = message.getProperty(CONTENT);

            gui.updateHistory(sender, sender, content);
        }
    }

    private void handlePublicMessageResponse(ParsedMessage message) throws Exception {
        if (message.getSize() != 0) {
            String sender = message.getProperty(SENDER);
            String content = message.getProperty(CONTENT);

            gui.updateHistory(sender, sender, content);
        }
    }

    public void sendMessage(String method) {
        String message = ZERO + SEPARATOR + method;

        out.println(message);
        out.flush();

        System.out.printf("\nMessage sent: \n%s\n", message);
    }

    public void sendMessage(int size, String method, String body) {
        String message = size + SEPARATOR + method + NEW_LINE + body;

        out.println(message);
        out.flush();

        System.out.printf("\nMessage sent: \n%s", message);
    }

}