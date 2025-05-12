package protocol.domain;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolMethods {

    public static final String CREATE_USER = "CREATE_USER";
    public static final String GET_USERS = "GET_USERS";
    public static final String USER_CREATED = "USER_CREATED";
    public static final String SEND_PUBLIC_MESSAGE = "SEND_PUBLIC_MESSAGE";
    public static final String SEND_PRIVATE_MESSAGE = "SEND_PRIVATE_MESSAGE";

    public static final List<String> allMethods = new ArrayList<>(
        asList(CREATE_USER, GET_USERS, USER_CREATED, SEND_PUBLIC_MESSAGE, SEND_PRIVATE_MESSAGE));

}
