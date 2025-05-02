package server.client;

import static java.lang.Integer.parseInt;
import static server.protocol.Protocol.NEW_LINE;
import static server.protocol.Protocol.SEPARATOR;
import static server.protocol.Protocol.ZERO;
import static server.protocol.Protocol.getLineFirstValue;
import static server.protocol.ProtocolValidator.isHeaderValid;

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

    public void sendMessage(String msg) {
        out.println(msg);
        out.flush();
    }

    public void sendEmptyResponse(String method) {
        sendMessage(ZERO + SEPARATOR + method);
    }

    public void sendResponse(int size, String method, String body) {
        sendMessage(size + SEPARATOR + method + NEW_LINE + body);
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