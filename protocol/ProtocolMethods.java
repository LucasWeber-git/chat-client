package protocol;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolMethods {

    public static final String CREATE_USER = "CREATE_USER";
    public static final String GET_USERS = "GET_USERS";
    public static final String SEND_PUBLIC_MESSAGE = "SEND_PUBLIC_MESSAGE";
    public static final String SEND_PRIVATE_MESSAGE = "SEND_PRIVATE_MESSAGE";

    public static final List<String> allMethods = new ArrayList<>(
        asList(CREATE_USER, GET_USERS, SEND_PUBLIC_MESSAGE, SEND_PRIVATE_MESSAGE));

}
