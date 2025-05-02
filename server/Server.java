package server;

import static protocol.Errors.DUPLICATED_USER;
import static protocol.Protocol.SEPARATOR;
import static protocol.ProtocolMethods.CREATE_USER;
import static protocol.ProtocolMethods.GET_USERS;
import static protocol.ProtocolMethods.SEND_PRIVATE_MESSAGE;
import static protocol.ProtocolMethods.SEND_PUBLIC_MESSAGE;
import static protocol.ProtocolProperties.CONTENT;
import static protocol.ProtocolProperties.RECIPIENT;
import static protocol.ProtocolProperties.USERNAME;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import server.client.ConnectedClient;
import protocol.ParsedMessage;
import protocol.Protocol;

public class Server {

    private final List<ConnectedClient> clients = new ArrayList<>();

    public void waitForClients() {
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
        System.out.printf("\n--//--\nRequest received: \n%s\n--//--\n", message);

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

    public void createUser(ConnectedClient sender, ParsedMessage message) throws Exception {
        String username = message.getProperty(USERNAME);

        if (findClient(username) != null) {
            sender.sendResponse(1, CREATE_USER, DUPLICATED_USER);
        }

        int pos = clients.indexOf(sender);

        sender.setUsername(username);
        clients.set(pos, sender);

        System.out.println("Usuário criado: " + username);
        sender.sendEmptyResponse(CREATE_USER);
    }

    public void getUsers(ConnectedClient sender) {
        List<String> usernames = clients.stream()
            .map(ConnectedClient::getUsername)
            .collect(Collectors.toList());

        System.out.println("Users retrieved");
        sender.sendResponse(1, GET_USERS, String.join(SEPARATOR, usernames));
    }

    public void sendPublicMessage(ConnectedClient sender, ParsedMessage message) throws Exception {
        for (ConnectedClient client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(message.getProperty(CONTENT));
        }

        System.out.println("Public message sent by " + sender.getUsername());
        sender.sendEmptyResponse(SEND_PUBLIC_MESSAGE);
    }

    public void sendPrivateMessage(ConnectedClient sender, ParsedMessage message) throws Exception {
        String username = sender.getUsername();
        ConnectedClient recipient = findClient(message.getProperty(RECIPIENT));

        recipient.sendMessage(message.getProperty(CONTENT));

        System.out.printf("\nMessage sent from %s to %s", username, recipient.getUsername());
        sender.sendEmptyResponse(SEND_PRIVATE_MESSAGE);
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

    public static void main(String[] args) {
        Server server = new Server();
        server.waitForClients();
    }

}
