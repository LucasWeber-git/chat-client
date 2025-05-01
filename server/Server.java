import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    //TODO: fazer alguma forma de pegar os clients sem ser static
    public static final List<ConnectedClient> clients = new ArrayList<>();

    public void waitForClients() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            serverSocket = new ServerSocket(8084);

            while (true) {
                socket = serverSocket.accept();

                ConnectedClient client = new ConnectedClient(socket, "Nome");
                clients.add(client);

                System.out.printf("Connected: %s\n", client.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            close(serverSocket, socket);
        }
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
        Server rs = new Server();
        rs.waitForClients();
    }

}
