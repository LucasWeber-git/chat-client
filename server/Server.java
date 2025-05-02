package server;

import static server.protocol.ProtocolProperties.CONTENT;
import static server.protocol.ProtocolProperties.RECIPIENT;
import static server.protocol.ProtocolProperties.USERNAME;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import server.client.ConnectedClient;
import server.protocol.ParsedMessage;

public class Server {

    public static final List<ConnectedClient> clients = new ArrayList<>();

    public void waitForClients() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(8084);

            while (true) {
                socket = serverSocket.accept();

                ConnectedClient client = new ConnectedClient(socket);
                clients.add(client);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(serverSocket, socket);
        }
    }

    public static void createUser(ConnectedClient sender, ParsedMessage message) {
        System.out.println("Usuário conectado: " + message.getProperties().get(USERNAME));
    }

    public static void sendPublicMessage(ConnectedClient sender,  ParsedMessage message) {
        for (ConnectedClient client : Server.clients) {
            if (client == sender) {
                continue;
            }
            client.sendMessage(message.getProperties().get(CONTENT));
        }
    }

    public static void sendPrivateMessage(ConnectedClient sender,  ParsedMessage message) {
        System.out.println("Usuário enviou mensagem privada para: " + message.getProperties().get(RECIPIENT));
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
