package server.client;

import static java.lang.Integer.parseInt;
import static protocol.Protocol.NEW_LINE;
import static protocol.Protocol.ZERO;
import static protocol.Protocol.formatProperty;
import static protocol.Protocol.getLineFirstValue;
import static protocol.ProtocolValidator.isHeaderValid;
import static protocol.domain.ProtocolProperties.ERROR;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

import server.Server;

public class ConnectedClient implements Runnable {

    private final Socket socket;
    private final Server server;

    private BufferedReader in;
    private PrintWriter out;
    private String username;

    public ConnectedClient(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        this.setup();
    }

    public void setup() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            Thread thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(int size, String method, String body) {
        String header = formatProperty(String.valueOf(size), method);

        send(header + body);
    }

    public void sendEmptyMessage(String method) {
        String header = formatProperty(ZERO, method);

        send(header);
    }

    public void sendErrorMessage(String method, String errorMessage) {
        String header = formatProperty("1", method);
        String body = formatProperty(ERROR, errorMessage);

        send(header + body);
    }

    private void send(String message) {
        out.println(message);
        out.flush();

        System.out.printf("\nMessage sent: \n%s", message);
    }

    public void waitForMessages() {
        while (true) {
            try {
                messageReceived(in.readLine());
            } catch (SocketException e) {
                System.out.println("\nClient disconnected");
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
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

            server.processRequest(this, message.toString());
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void run() {
        waitForMessages();
    }

}