package server.protocol;

import static java.lang.Integer.parseInt;
import static server.protocol.Protocol.SEPARATOR;
import static server.protocol.Protocol.getLineFirstValue;
import static server.protocol.Protocol.getLineLastValue;
import static server.protocol.ProtocolMethods.allMethods;
import static server.protocol.ProtocolProperties.allProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class ProtocolValidator {

    public static boolean validate(String message) {
        if (message == null || message.length() == 0) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(message))) {
            String header = reader.readLine();

            if (!isHeaderValid(header)) {
                return false;
            }

            Integer size = Integer.valueOf(getLineFirstValue(header));
            if (!isPropertiesValid(reader, size)) {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isHeaderValid(String header) {
        if (!header.contains(SEPARATOR)) {
            System.out.println("Missing separator character on header");
            return false;
        }

        String size = getLineFirstValue(header);
        String method = getLineLastValue(header);

        if (!allMethods.contains(method)) {
            System.out.printf("\nMethod %s is not recognized", method);
        }

        if (!isNumeric(size)) {
            System.out.println("Size is not numeric");
            return false;
        }

        return true;
    }

    private static boolean isPropertiesValid(BufferedReader reader, Integer size) throws IOException {
        String line;
        int lineCount = 1;

        while ((line = reader.readLine()) != null && lineCount <= size) {
            if (!line.contains(SEPARATOR)) {
                System.out.printf("\nMissing separator character on line %d", lineCount);
                return false;
            }

            String property = getLineFirstValue(line);
            if (!allProperties.contains(property)) {
                System.out.printf("\nProperty %s is not recognized", property);
                return false;
            }

            lineCount++;
        }
        return true;
    }

    private static boolean isNumeric(String value) {
        if (value.isEmpty()) {
            return false;
        }

        try {
            parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
