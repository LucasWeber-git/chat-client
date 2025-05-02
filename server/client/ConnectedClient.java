package server.client;

import static java.lang.Integer.parseInt;
import static server.protocol.Protocol.getLineFirstValue;
import static server.protocol.ProtocolValidator.isHeaderValid;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.protocol.Protocol;

public class ConnectedClient implements Runnable {

    private final Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    public ConnectedClient(Socket socket) {
        this.socket = socket;
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
        try {
            while (true) {
                messageReceived(in.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void messageReceived(String header) throws IOException {
        if (isHeaderValid(header)) {
            StringBuilder message = new StringBuilder(header);

            int size = parseInt(getLineFirstValue(header));

            for (int i = 1; i <= size; i++) {
                message.append("\n").append(in.readLine());
            }

            Protocol.execute(this, message.toString());
        }
    }

    public void sendMessage(String msg) {
        out.println(msg);
        out.flush();
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

    @Override
    public void run() {
        waitForMessages();
    }

}