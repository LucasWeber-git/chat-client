package server;

import static protocol.Protocol.formatProperty;
import static protocol.domain.ErrorMessages.DUPLICATED_USER;
import static protocol.domain.ProtocolMethods.CREATE_USER;
import static protocol.domain.ProtocolMethods.GET_USERS;
import static protocol.domain.ProtocolMethods.SEND_PRIVATE_MESSAGE;
import static protocol.domain.ProtocolMethods.SEND_PUBLIC_MESSAGE;
import static protocol.domain.ProtocolMethods.USER_CREATED;
import static protocol.domain.ProtocolProperties.CONTENT;
import static protocol.domain.ProtocolProperties.RECIPIENT;
import static protocol.domain.ProtocolProperties.SENDER;
import static protocol.domain.ProtocolProperties.USERNAME;
import static protocol.domain.ProtocolProperties.USERNAMES;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import protocol.Protocol;
import protocol.dto.ParsedMessage;
import server.client.ConnectedClient;

public class Server {

    private final List<ConnectedClient> clients = new ArrayList<>();

    public static void main(String[] args) {
        Server server = new Server();
        server.waitForClients();
    }

    private void waitForClients() {
        try {
            ServerSocket serverSocket = new ServerSocket(8084);

            while (true) {
                Socket socket = serverSocket.accept();
                new ConnectedClient(socket, this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void processRequest(ConnectedClient sender, String message) throws Exception {
        System.out.printf("\nMessage received: \n%s\n", message);

        ParsedMessage parsedMessage = Protocol.parseMessage(message);

        switch (parsedMessage.getMethod()) {
            case CREATE_USER:
                createUser(sender, parsedMessage);
                break;

            case GET_USERS:
                getUsers(sender);
                break;

            case SEND_PRIVATE_MESSAGE:
                sendPrivateMessage(sender, parsedMessage);
                break;

            case SEND_PUBLIC_MESSAGE:
                sendPublicMessage(sender, parsedMessage);
                break;
        }
    }

    private void createUser(ConnectedClient sender, ParsedMessage message) throws Exception {
        String username = message.getProperty(USERNAME);

        if (findClientByUsername(username) != null) {
            sender.sendErrorMessage(CREATE_USER, DUPLICATED_USER);
            return;
        }

        sender.setUsername(username);
        clients.add(sender);

        sender.sendEmptyMessage(CREATE_USER);

        for (ConnectedClient client : clients) {
            if (client != sender) {
                client.sendEmptyMessage(USER_CREATED);
            }
        }
    }

    private void getUsers(ConnectedClient sender) {
        List<String> usernames = clients.stream()
            .map(ConnectedClient::getUsername)
            .collect(Collectors.toList());

        String body = formatProperty(USERNAMES, String.join(",", usernames));

        sender.sendMessage(1, GET_USERS, body);
    }

    private void sendPublicMessage(ConnectedClient sender, ParsedMessage message) throws Exception {
        for (ConnectedClient client : clients) {
            if (client != sender) {
                String senderUser = formatProperty(SENDER, sender.getUsername());
                String content = formatProperty(CONTENT, message.getProperty(CONTENT));
                String body = senderUser + content;

                client.sendMessage(2, SEND_PUBLIC_MESSAGE, body);
            }
        }

        sender.sendEmptyMessage(SEND_PUBLIC_MESSAGE);
    }

    private void sendPrivateMessage(ConnectedClient sender, ParsedMessage message) throws Exception {
        ConnectedClient recipient = findClientByUsername(message.getProperty(RECIPIENT));

        String senderUser = formatProperty(SENDER, sender.getUsername());
        String content = formatProperty(CONTENT, message.getProperty(CONTENT));
        String body = senderUser + content;

        recipient.sendMessage(2, SEND_PRIVATE_MESSAGE, body);

        sender.sendEmptyMessage(SEND_PRIVATE_MESSAGE);
    }

    private ConnectedClient findClientByUsername(String username) {
        return clients.stream()
            .filter(c -> username.equalsIgnoreCase(c.getUsername()))
            .findFirst()
            .orElse(null);
    }

}
