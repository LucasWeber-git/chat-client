import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectedClient implements Runnable {

    private final String name;
    private final Socket socket;

    private BufferedReader in;
    private PrintWriter out;

    public ConnectedClient(Socket socket, String name) {
        this.socket = socket;
        this.name = name;

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

    public void waitMessages() {
        String msg = "";

        try {
            while (true) {
                msg = in.readLine();
                for (ConnectedClient c : Server.clients) {
                    if (c == this) {
                        continue;
                    }
                    c.sendMessage(msg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public String getName() {
        return name;
    }

    @Override
    public void run() {
        waitMessages();
    }

}