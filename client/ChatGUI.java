import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ChatGUI extends JFrame {

    public ChatGUI(List<String> users) {
        super("Cliente para Chat");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);

        JScrollPane usersListPane = buildUsersList(users);
        JScrollPane chatAreaPane = buildChatArea();
        JPanel inputPanel = buildInputPanel();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(usersListPane, WEST);
        getContentPane().add(chatAreaPane, CENTER);
        getContentPane().add(inputPanel, SOUTH);
    }

    private JScrollPane buildUsersList(List<String> users) {
        JList<String> userList = new JList<>(users.toArray(new String[0]));

        JScrollPane userScrollPane = new JScrollPane(userList);
        userScrollPane.setPreferredSize(new Dimension(120, 0));

        return userScrollPane;
    }

    private JScrollPane buildChatArea() {
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);

        return new JScrollPane(chatArea);
    }

    private JPanel buildInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(new JTextField(), CENTER);
        inputPanel.add(new JButton("Enviar Mensagem"), EAST);

        return inputPanel;
    }

}