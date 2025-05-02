package client.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static protocol.Protocol.formatProperty;
import static protocol.ProtocolMethods.CREATE_USER;
import static protocol.ProtocolMethods.GET_USERS;
import static protocol.ProtocolMethods.SEND_PRIVATE_MESSAGE;
import static protocol.ProtocolMethods.SEND_PUBLIC_MESSAGE;
import static protocol.ProtocolProperties.CONTENT;
import static protocol.ProtocolProperties.RECIPIENT;
import static protocol.ProtocolProperties.USERNAME;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import client.Client;

public class ChatGUI extends JFrame {

    private final Client client;

    private String username;
    private boolean isUserCreated = false;
    private final Map<String, String> chatHistory = new HashMap<>();

    private final JList<String> userList;
    private final JTextArea chatArea;
    private final JTextField txtMessage;
    private final JCheckBox ckPublic;
    private final JButton btSend;

    public ChatGUI(Client client) {
        super("Cliente para Chat");
        this.client = client;

        inputUsername("Digite seu nome:");
        client.sendMessage(GET_USERS);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);

        userList = new JList<>();
        userList.setSelectionMode(SINGLE_SELECTION);
        userList.addListSelectionListener(e -> onUserSelectionChange());

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        txtMessage = new JTextField();

        ckPublic = new JCheckBox("Mensagem pública");

        btSend = new JButton("Enviar mensagem");
        btSend.addActionListener(e -> onClickSendButton());
    }

    public void render() {
        JScrollPane usersScrollPane = new JScrollPane(userList);
        usersScrollPane.setPreferredSize(new Dimension(120, 0));

        JPanel inputPanel = new JPanel(new BorderLayout());

        inputPanel.add(txtMessage, CENTER);
        inputPanel.add(ckPublic, EAST);
        inputPanel.add(btSend, EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(usersScrollPane, WEST);
        getContentPane().add(new JScrollPane(chatArea), CENTER);
        getContentPane().add(inputPanel, SOUTH);

        while (!isUserCreated) {
            try {
                Thread.sleep(10);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        this.setVisible(true);
    }

    public void inputUsername(String message) {
        username = showInputDialog(null, message);
        String body = formatProperty(USERNAME, username);

        client.sendMessage(1, CREATE_USER, body);
    }

    private void onUserSelectionChange() {
        String userSelected = userList.getSelectedValue();

        if (chatHistory.containsKey(userSelected)) {
            chatArea.setText(chatHistory.get(userSelected));
        } else {
            chatHistory.put(userSelected, "");
            chatArea.setText("");
        }
    }

    private void onClickSendButton() {
        if (ckPublic.isSelected()) {
            this.sendPublicMessage();
        } else {
            this.sendPrivateMessage();
        }
    }

    private void sendPublicMessage() {
        String body = formatProperty(CONTENT, txtMessage.getText());
        client.sendMessage(1, SEND_PUBLIC_MESSAGE, body);

        for (String key : chatHistory.keySet()) {
            updateHistory(key, username, txtMessage.getText());
        }
    }

    private void sendPrivateMessage() {
        String recipient = formatProperty(RECIPIENT, userList.getSelectedValue());
        String content = formatProperty(CONTENT, txtMessage.getText());
        String body = recipient + content;

        client.sendMessage(2, SEND_PRIVATE_MESSAGE, body);

        updateHistory(userList.getSelectedValue(), username, txtMessage.getText());
    }

    public void updateHistory(String key, String sender, String newMessage) {
        String currentValue = chatHistory.get(key);
        String newValue = currentValue.concat(sender + ": " + newMessage);

        chatHistory.replace(key, newValue);
    }

    public void setUsers(final List<String> users) {
        userList.setListData(users.toArray(new String[0]));
    }

    public void setUserCreated(final boolean userCreated) {
        isUserCreated = userCreated;
    }

}