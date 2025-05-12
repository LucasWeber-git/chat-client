package client.gui;

import static java.awt.BorderLayout.CENTER;
import static java.awt.BorderLayout.EAST;
import static java.awt.BorderLayout.SOUTH;
import static java.awt.BorderLayout.WEST;
import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static javax.swing.JOptionPane.showInputDialog;
import static javax.swing.ListSelectionModel.SINGLE_SELECTION;
import static protocol.Protocol.formatProperty;
import static protocol.domain.ProtocolMethods.CREATE_USER;
import static protocol.domain.ProtocolMethods.GET_USERS;
import static protocol.domain.ProtocolMethods.SEND_PRIVATE_MESSAGE;
import static protocol.domain.ProtocolMethods.SEND_PUBLIC_MESSAGE;
import static protocol.domain.ProtocolProperties.CONTENT;
import static protocol.domain.ProtocolProperties.RECIPIENT;
import static protocol.domain.ProtocolProperties.USERNAME;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import client.Client;

public class ChatGUI extends JFrame {

    private final Client client;

    private String username;
    private final Map<String, String> chatHistory = new HashMap<>();

    private final DefaultListModel<String> userListModel;
    private final JList<String> userList;
    private final JTextArea chatArea;
    private final JTextField txtMessage;
    private final JCheckBox ckPublic;
    private final JButton btSend;

    public ChatGUI(Client client) {
        super("Cliente para Chat");
        this.client = client;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 500);
        setLocationRelativeTo(null);

        userListModel = new DefaultListModel<>();

        userList = new JList<>(userListModel);
        userList.setSelectionMode(SINGLE_SELECTION);
        userList.addListSelectionListener(this::onUserSelectionChange);

        chatArea = new JTextArea();
        chatArea.setEditable(false);

        txtMessage = new JTextField();

        ckPublic = new JCheckBox("Mensagem pública");

        btSend = new JButton("Enviar mensagem");
        btSend.addActionListener(this::onClickSendButton);
    }

    public void render() {
        JScrollPane usersScrollPane = new JScrollPane(userList);
        usersScrollPane.setPreferredSize(new Dimension(120, 0));

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(ckPublic);
        buttonPanel.add(btSend);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(txtMessage, CENTER);
        inputPanel.add(buttonPanel, EAST);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(usersScrollPane, WEST);
        getContentPane().add(new JScrollPane(chatArea), CENTER);
        getContentPane().add(inputPanel, SOUTH);

        inputUsername("Digite seu nome:");

        client.sendMessage(GET_USERS);
    }

    public void inputUsername(String message) {
        username = showInputDialog(null, message);
        String body = formatProperty(USERNAME, username);

        client.sendMessage(1, CREATE_USER, body);
    }

    public void onUserCreated() {
        SwingUtilities.invokeLater(() -> {
            this.setTitle("Cliente para Chat | " + username);
            this.setVisible(true);
        });
    }

    private void onUserSelectionChange(EventObject e) {
        reloadChat();
    }

    private void onClickSendButton(EventObject e) {
        if (txtMessage.getText() == null || txtMessage.getText().isBlank()) {
            return;
        }

        if (ckPublic.isSelected()) {
            this.sendPublicMessage();
        } else {
            this.sendPrivateMessage();
        }
        txtMessage.setText("");
    }

    private void sendPublicMessage() {
        String body = formatProperty(CONTENT, txtMessage.getText());
        client.sendMessage(1, SEND_PUBLIC_MESSAGE, body);

        for (String key : chatHistory.keySet()) {
            addMessageToHistory(key, username, txtMessage.getText());
        }
    }

    private void sendPrivateMessage() {
        String recipient = formatProperty(RECIPIENT, userList.getSelectedValue());
        String content = formatProperty(CONTENT, txtMessage.getText());
        String body = recipient + content;

        client.sendMessage(2, SEND_PRIVATE_MESSAGE, body);

        addMessageToHistory(userList.getSelectedValue(), username, txtMessage.getText());
    }

    public void addMessageToHistory(String key, String sender, String newMessage) {
        String formattedMessage = sender + ": " + newMessage + "\n";

        if (chatHistory.containsKey(key)) {
            String currentValue = chatHistory.get(key);
            String newValue = currentValue.concat(formattedMessage);

            chatHistory.replace(key, newValue);
        } else {
            chatHistory.put(key, formattedMessage);
        }

        reloadChat();
    }

    private void reloadChat() {
        String userSelected = userList.getSelectedValue();

        if (chatHistory.containsKey(userSelected)) {
            chatArea.setText(chatHistory.get(userSelected));
        } else {
            chatHistory.put(userSelected, "");
            chatArea.setText("");
        }
    }

    public void setUsers(List<String> newUsers) {
        newUsers.remove(username);
        newUsers.sort(CASE_INSENSITIVE_ORDER);

        String selected = userList.getSelectedValue();

        userListModel.clear();
        userListModel.addAll(newUsers);

        if (selected != null) {
            userList.setSelectedValue(selected, rootPaneCheckingEnabled);
        }
    }

}