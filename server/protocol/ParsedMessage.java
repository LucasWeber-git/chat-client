package server.protocol;

import java.util.Map;

public class ParsedMessage {

    private final Integer size;
    private final String method;
    private final Map<String, String> properties;

    public ParsedMessage(final Integer size, final String method, final Map<String, String> properties) {
        this.size = size;
        this.method = method;
        this.properties = properties;
    }

    public Integer getSize() {
        return size;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

}
