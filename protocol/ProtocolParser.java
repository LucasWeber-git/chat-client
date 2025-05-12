package protocol;

import static protocol.Protocol.getLineFirstValue;
import static protocol.Protocol.getLineLastValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import protocol.dto.ParsedMessage;

public class ProtocolParser {

    public static ParsedMessage parse(String message) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(message))) {
            String line = reader.readLine();

            Integer size = Integer.valueOf(getLineFirstValue(line));
            String method = getLineLastValue(line);
            Map<String, String> properties = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                properties.put(getLineFirstValue(line), getLineLastValue(line));
            }

            return new ParsedMessage(size, method, properties);
        }
    }

}
