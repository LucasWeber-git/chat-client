package server;

import static server.protocol.Protocol.SEPARATOR;
import static server.protocol.ProtocolMethods.CREATE_USER;
import static server.protocol.ProtocolMethods.GET_USERS;
import static server.protocol.ProtocolMethods.PRIVATE_MESSAGE;
import static server.protocol.ProtocolMethods.PUBLIC_MESSAGE;
import static server.protocol.ProtocolProperties.CONTENT;
import static server.protocol.ProtocolProperties.RECIPIENT;
import static server.protocol.ProtocolProperties.USERNAME;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import server.client.ConnectedClient;
import server.protocol.ParsedMessage;
import server.protocol.Protocol;

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
            case GET_USERS:
                getUsers(sender);
                break;

            case CREATE_USER:
                createUser(sender, parsedMessage);
                break;

            case PRIVATE_MESSAGE:
                sendPrivateMessage(sender, parsedMessage);
                break;

            case PUBLIC_MESSAGE:
                sendPublicMessage(sender, parsedMessage);
                break;
        }
    }

    public void createUser(ConnectedClient sender, ParsedMessage message) throws Exception {
        String username = message.getProperty(USERNAME);
        int pos = clients.indexOf(sender);

        sender.setUsername(username);
        clients.set(pos, sender);

        System.out.println("Usuário criado: " + username);
        sender.sendEmptyResponse(CREATE_USER);
    }

    public void sendPublicMessage(ConnectedClient sender, ParsedMessage message) throws Exception {
        for (ConnectedClient client : clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(message.getProperty(CONTENT));
        }

        System.out.println("Public message sent by " + sender.getUsername());
        sender.sendEmptyResponse(PUBLIC_MESSAGE);
    }

    public void sendPrivateMessage(ConnectedClient sender, ParsedMessage message) throws Exception {
        String username = sender.getUsername();
        ConnectedClient recipient = findClient(message.getProperty(RECIPIENT));

        recipient.sendMessage(message.getProperty(CONTENT));

        System.out.printf("\nMessage sent from %s to %s", username, recipient.getUsername());
        sender.sendEmptyResponse(PRIVATE_MESSAGE);
    }

    public void getUsers(ConnectedClient sender) {
        List<String> usernames = clients.stream()
            .map(ConnectedClient::getUsername)
            .collect(Collectors.toList());

        System.out.println("Users retrieved");
        sender.sendResponse(1, GET_USERS, String.join(SEPARATOR, usernames));
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
