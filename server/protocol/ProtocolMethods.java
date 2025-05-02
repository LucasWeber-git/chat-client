package server.protocol;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

public abstract class ProtocolMethods {

    public static final String GET_USERS = "GET_USERS";
    public static final String CREATE_USER = "CREATE_USER";
    public static final String PUBLIC_MESSAGE = "PUBLIC_MESSAGE";
    public static final String PRIVATE_MESSAGE = "PRIVATE_MESSAGE";

    public static final List<String> allMethods = new ArrayList<>(
        asList(GET_USERS, CREATE_USER, PUBLIC_MESSAGE, PRIVATE_MESSAGE));

}
