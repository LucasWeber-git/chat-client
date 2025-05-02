package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.SwingUtilities;

import client.gui.ChatGUI;

public class Client implements Runnable {

    private BufferedReader in;
    private PrintWriter out;

    public static void main(String[] args) {
        Client client = new Client();
        client.setup();

        SwingUtilities.invokeLater(() -> {
            new ChatGUI(client).setVisible(true);
        });
    }

    public void setup() {
        try {
            Socket socket = new Socket("localhost", 8084);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            Thread t = new Thread(this);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void send(String message) {
        out.println(message);
        out.flush();
    }

    public void waitForMessages() {
        try {
            while (true) {
                System.out.println(in.readLine());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        waitForMessages();
    }

}