package server.protocol;

import static server.Server.createUser;
import static server.Server.sendPrivateMessage;
import static server.Server.sendPublicMessage;
import static server.protocol.ProtocolMethods.CREATE_USER;
import static server.protocol.ProtocolMethods.PRIVATE_MESSAGE;
import static server.protocol.ProtocolMethods.PUBLIC_MESSAGE;
import static server.protocol.ProtocolParser.parse;
import static server.protocol.ProtocolValidator.validate;

import java.io.IOException;

import server.client.ConnectedClient;

public class Protocol {

    public static final String SEPARATOR = "|";

    public static void execute(ConnectedClient sender, String message) throws IOException {
        System.out.printf("\n--//--\nMessage received: \n%s\n--//--\n", message);

        if (!validate(message)) {
            System.out.println("Invalid message!");
            return;
        }

        ParsedMessage parsedMessage = parse(message);

        switch (parsedMessage.getMethod()) {
            case CREATE_USER:
                createUser(sender, parsedMessage);
                break;

            case PRIVATE_MESSAGE:
                sendPrivateMessage(sender, parsedMessage);
                break;

            case PUBLIC_MESSAGE:
                sendPublicMessage(sender, parsedMessage);
                break;
        }
    }

    public static String getLineFirstValue(String line) {
        return line.substring(0, line.indexOf(SEPARATOR)).trim();
    }

    public static String getLineLastValue(String line) {
        return line.substring(line.indexOf(SEPARATOR) + 1).trim();
    }

}
