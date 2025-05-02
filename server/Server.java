package server;

import static protocol.Errors.DUPLICATED_USER;
import static protocol.Protocol.formatProperty;
import static protocol.ProtocolMethods.CREATE_USER;
import static protocol.ProtocolMethods.GET_USERS;
import static protocol.ProtocolMethods.SEND_PRIVATE_MESSAGE;
import static protocol.ProtocolMethods.SEND_PUBLIC_MESSAGE;
import static protocol.ProtocolMethods.USER_CREATED;
import static protocol.ProtocolProperties.CONTENT;
import static protocol.ProtocolProperties.RECIPIENT;
import static protocol.ProtocolProperties.SENDER;
import static protocol.ProtocolProperties.USERNAME;
import static protocol.ProtocolProperties.USERNAMES;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import protocol.ParsedMessage;
import protocol.Protocol;
import server.client.ConnectedClient;

public class Server {

    private final List<ConnectedClient> clients = new ArrayList<>();

    public static void main(String[] args) {
        Server server = new Server();
        server.waitForClients();
    }

    private void waitForClients() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(8084);

            while (true) {
                socket = serverSocket.accept();

                ConnectedClient client = new ConnectedClient(socket, this);
                clients.add(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(serverSocket, socket);
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

        if (findClient(username) != null) {
            sender.sendMessage(1, CREATE_USER, DUPLICATED_USER);
            return;
        }

        int pos = clients.indexOf(sender);

        sender.setUsername(username);
        clients.set(pos, sender);

        sender.sendMessage(CREATE_USER);

        for (ConnectedClient client : clients) {
            if (client != sender) {
                client.sendMessage(USER_CREATED);
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

        sender.sendMessage(SEND_PUBLIC_MESSAGE);
    }

    private void sendPrivateMessage(ConnectedClient sender, ParsedMessage message) throws Exception {
        String username = sender.getUsername();
        ConnectedClient recipient = findClient(message.getProperty(RECIPIENT));

        String senderUser = formatProperty(SENDER, sender.getUsername());
        String content = formatProperty(CONTENT, message.getProperty(CONTENT));
        String body = senderUser + content;

        recipient.sendMessage(2, SEND_PRIVATE_MESSAGE, body);

        sender.sendMessage(SEND_PRIVATE_MESSAGE);
    }

    private ConnectedClient findClient(String username) {
        return clients.stream()
            .filter(c -> username.equals(c.getUsername()))
            .findFirst()
            .orElse(null);
    }

    private void close(ServerSocket serverSocket, Socket socket) {
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (socket != null) {
                socket.close();
            }

            for (ConnectedClient client : clients) {
                client.close();
            }

            System.out.println("Server stopped.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
