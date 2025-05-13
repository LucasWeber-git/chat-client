package protocol;

import static protocol.ProtocolParser.parse;
import static protocol.ProtocolValidator.validate;

import protocol.dto.ParsedMessage;

public class Protocol {

    public static final String SEPARATOR = "|";
    public static final String NEW_LINE = "\n";
    public static final String ZERO = "0";

    public static ParsedMessage parseMessage(String message) throws Exception {
        if (!validate(message)) {
            throw new Exception("Invalid Message!");
        }

        return parse(message);
    }

    public static String getLineFirstValue(String line) {
        return line.substring(0, line.indexOf(SEPARATOR)).trim();
    }

    public static String getLineLastValue(String line) {
        return line.substring(line.indexOf(SEPARATOR) + 1).trim();
    }

    public static String formatProperty(String key, String value) {
        return key + SEPARATOR + value + NEW_LINE;
    }

}
