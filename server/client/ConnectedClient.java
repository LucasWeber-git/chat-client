package server.client;

import static java.lang.Integer.parseInt;
import static protocol.Protocol.NEW_LINE;
import static protocol.Protocol.SEPARATOR;
import static protocol.Protocol.ZERO;
import static protocol.Protocol.getLineFirstValue;
import static protocol.ProtocolValidator.isHeaderValid;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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

    public void sendMessage(String method) {
        out.println(ZERO + SEPARATOR + method);
        out.flush();
    }

    public void sendMessage(int size, String method, String body) {
        out.println(size + SEPARATOR + method + NEW_LINE + body);
        out.flush();
    }

    public void waitForMessages() {
        while (true) {
            try {
                messageReceived(in.readLine());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void messageReceived(String header) throws Exception {
        if (isHeaderValid(header)) {
            StringBuilder message = new StringBuilder(header);

            int size = parseInt(getLineFirstValue(header));

            for (int i = 1; i <= size; i++) {
                message.append("\n").append(in.readLine());
            }

            server.processRequest(this, message.toString());
        }
    }

    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
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