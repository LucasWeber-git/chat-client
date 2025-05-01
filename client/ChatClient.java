import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ChatClient implements Runnable {

    private BufferedReader in;
    private PrintWriter out;

    //TODO: integrar este cara com o ChatGUI
    public static void main(String[] args) {
        ChatClient cc = new ChatClient();
        cc.setup();
        cc.sendMessages();

        /*
         * SwingUtilities.invokeLater(() -> {
         * new ChatGUI().setVisible(true);
         * });
         */
    }

    public void setup() {
        try {
            Socket s = new Socket("localhost", 8084);

            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new PrintWriter(s.getOutputStream());

            Thread t = new Thread(this);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessages() {
        try {
            BufferedReader kbIn = new BufferedReader(new InputStreamReader(System.in));
            String kbMsg = "";

            while (true) {
                kbMsg = kbIn.readLine();
                out.println(kbMsg);
                out.flush();
            }
        } catch (Exception e) {
            e.addSuppressed(e);
        }
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