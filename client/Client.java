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
import static protocol.ProtocolProperties.ERROR;
import static protocol.ProtocolProperties.USERNAMES;
import static protocol.ProtocolValidator.isHeaderValid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

import javax.swing.SwingUtilities;

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

            startGUI();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startGUI() {
        gui = new ChatGUI(this);
        SwingUtilities.invokeLater(() -> gui.setVisible(true));
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
        System.out.printf("\n--//--\nMessage received: \n%s\n--//--\n", message);

        ParsedMessage parsedMessage = Protocol.parseMessage(message);

        switch (parsedMessage.getMethod()) {
            case CREATE_USER:
                handleCreateUserResponse(parsedMessage);
                break;

            case GET_USERS:
                handleGetUsersResponse(parsedMessage);
                break;

            case SEND_PRIVATE_MESSAGE:
                handlePrivateMessageResponse();
                break;

            case SEND_PUBLIC_MESSAGE:
                handlePublicMessageResponse();
                break;
        }
    }

    private void handleCreateUserResponse(ParsedMessage message) {
        if (DUPLICATED_USER.equals(message.getPropertyNullable(ERROR))) {
            gui.inputUsername("Usuário já cadastrado! Digite outro nome:");
        }
    }

    private void handleGetUsersResponse(ParsedMessage message) throws Exception {
        String usernames = message.getProperty(USERNAMES);
        List<String> newUsers = asList(usernames.split(","));

        gui.setUsers(newUsers);
        gui.render();
    }

    private void handlePrivateMessageResponse() {
        System.out.println("Private message sent");
        // TODO: aqui tem que atualizar o histórico
    }

    private void handlePublicMessageResponse() {
        System.out.println("Public message sent");
        // TODO: aqui tem que atualizar o histórico
    }

    public void sendMessage(String method) {
        out.println(ZERO + SEPARATOR + method);
        out.flush();
    }

    public void sendMessage(int size, String method, String body) {
        out.println(size + SEPARATOR + method + NEW_LINE + body);
        out.flush();
    }

}