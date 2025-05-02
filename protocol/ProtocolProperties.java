package protocol;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolProperties {

    public static final String CONTENT = "content";
    public static final String ERROR = "error";
    public static final String RECIPIENT = "recipient";
    public static final String SENDER = "sender";
    public static final String USERNAME = "username";
    public static final String USERNAMES = "usernames";

    public static final List<String> allProperties = new ArrayList<>(
        asList(CONTENT, ERROR, RECIPIENT, SENDER, USERNAME, USERNAMES));

}
