import java.io.IOException;

public class Protocol {

    public static final String SEPARATOR = "|";

    public static void execute(ConnectedClient sender, String message) throws IOException {
        System.out.printf("\n--//--\nMessage received: \n%s\n--//--\n", message);

        if (!ProtocolValidator.validate(message)) {
            System.out.println("Invalid message!");
            return;
        }

        ParsedMessage parsedMessage = ProtocolParser.parse(message);

        switch (parsedMessage.getMethod()) {
            case ProtocolMethods.CREATE_USER:
                Server.createUser(sender, parsedMessage);
                break;

            case ProtocolMethods.PRIVATE_MESSAGE:
                Server.sendPrivateMessage(sender, parsedMessage);
                break;

            case ProtocolMethods.PUBLIC_MESSAGE:
                Server.sendPublicMessage(sender, parsedMessage);
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
