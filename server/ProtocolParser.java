import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class ProtocolParser {

    public static ParsedMessage parse(String message) throws IOException {
        try (BufferedReader reader = new BufferedReader(new StringReader(message))) {
            String line = reader.readLine();

            Integer size = Integer.valueOf(Protocol.getLineFirstValue(line));
            String method = Protocol.getLineLastValue(line);
            Map<String, String> properties = new HashMap<>();

            while ((line = reader.readLine()) != null) {
                properties.put(Protocol.getLineFirstValue(line), Protocol.getLineLastValue(line));
            }

            return new ParsedMessage(size, method, properties);
        }
    }

}
